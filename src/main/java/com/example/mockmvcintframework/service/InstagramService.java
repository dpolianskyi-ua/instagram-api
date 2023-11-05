package com.example.mockmvcintframework.service;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS;
import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWING;
import static com.github.instagram4j.instagram4j.utils.IGChallengeUtils.resolveTwoFactor;
import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.lang.String.*;
import static java.time.Instant.*;
import static java.time.LocalDateTime.*;
import static java.util.Optional.ofNullable;
import static java.util.TimeZone.*;
import static java.util.concurrent.TimeUnit.*;
import static org.apache.commons.lang3.ObjectUtils.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.example.mockmvcintframework.dto.InstagramCredentialsRequestDTO;
import com.example.mockmvcintframework.dto.InstagramRequestDTO;
import com.example.mockmvcintframework.dto.post.InstagramFeedDTO;
import com.example.mockmvcintframework.dto.post.InstagramPostDTO;
import com.example.mockmvcintframework.dto.post.InstagramPostDTO.InstagramPostDTOBuilder;
import com.example.mockmvcintframework.dto.post.partial.LocationDetailsDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileCountsDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileDTO.InstagramProfileDTOBuilder;
import com.example.mockmvcintframework.dto.profile.InstagramProfilePictureDTO;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.feed.FeedIterable;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds;
import com.github.instagram4j.instagram4j.responses.feed.FeedUsersResponse;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramService {

  private final InstagramTimelineMediaService timelineMediaService;

  private IGClient igClient;

  @SneakyThrows
  public void initializeClient(InstagramCredentialsRequestDTO dto) {
    var igClientBuilder =
        IGClient.builder().username(dto.getUsername()).password(dto.getPassword());

    if (isNotBlank(dto.getInputCode())) {
      igClientBuilder =
          igClientBuilder.onTwoFactor(
              (client, response) -> resolveTwoFactor(client, response, dto::getInputCode));
    }

    igClient = igClientBuilder.login();

    var updatedOkHttpClient =
        igClient
            .getHttpClient()
            .newBuilder()
            .connectTimeout(30, SECONDS)
            .readTimeout(90, SECONDS)
            .writeTimeout(90, SECONDS)
            .build();

    igClient.setHttpClient(updatedOkHttpClient);
  }

  public InstagramProfileDTO extractProfileDetails(String username) {
    var user = getUserAction(username).getUser();

    var profilePicture =
        InstagramProfilePictureDTO.builder()
            .id(user.getProfile_pic_id())
            .url(user.getProfile_pic_url())
            .build();

    var profileCounts =
        InstagramProfileCountsDTO.builder()
            .media(user.getMedia_count())
            .followers(user.getFollower_count())
            .following(user.getFollowing_count())
            .build();

    return buildCommonProfileDetails(user)
        .externalURL(user.getExternal_url())
        .profilePicture(profilePicture)
        .biography(user.getBiography())
        .profileCounts(profileCounts)
        .build();
  }

  public InstagramFeedDTO extractFeedDetails(InstagramRequestDTO requestDto) {
    var username = requestDto.getUsername();
    var nextMaxId = requestDto.getNextMaxId();
    var timeout = requestDto.getTimeout();
    var rounds = requestDto.getRounds();

    var primaryKey = getUserAction(username).getUser().getPk();
    var items = new ArrayList<TimelineMedia>();
    var nextMaxIds = new ArrayList<String>();
    var requestRounds = 0;

    if (nextMaxId == null) {
      // 1st request
      nextMaxId = getNextTimelineMedia(primaryKey, null, items, timeout);
      rounds--;
      nextMaxIds.add(nextMaxId);
    }

    // ongoing requests
    while (nextMaxId != null && requestRounds < rounds) {
      nextMaxId = getNextTimelineMedia(primaryKey, nextMaxId, items, timeout);
      nextMaxIds.add(nextMaxId);
      requestRounds++;
    }

    log.info("[TIMELINE_MEDIA] List of Next Max IDs: " + join(", ", nextMaxIds));

    return InstagramFeedDTO.builder()
        .posts(items.stream().map(this::populatePostDetails).toList())
        .build();
  }

  public List<InstagramProfileDTO> extractFollowers(InstagramRequestDTO requestDto) {
    var feedIterator = prepareFeedIterator(requestDto, FOLLOWERS);

    return getFeedDetails(requestDto, feedIterator);
  }

  public List<InstagramProfileDTO> extractFollowing(InstagramRequestDTO requestDto) {
    var feedIterator = prepareFeedIterator(requestDto, FOLLOWING);

    return getFeedDetails(requestDto, feedIterator);
  }

  @SneakyThrows
  private UserAction getUserAction(String username) {
    return igClient.actions().users().findByUsername(username).get();
  }

  private <T extends Profile> InstagramProfileDTOBuilder buildCommonProfileDetails(T user) {
    return InstagramProfileDTO.builder()
        .primaryKey(user.getPk())
        .isPrivate(user.is_private())
        .username(user.getUsername())
        .fullName(user.getFull_name());
  }

  private String getNextTimelineMedia(
      Long pk, String nextMaxId, List<TimelineMedia> items, int timeoutSec) {
    wait(timeoutSec);

    var userFeed = new FeedUserRequest(pk, nextMaxId).execute(igClient).join();

    items.addAll(userFeed.getItems());

    var followingNextMaxId = userFeed.getNext_max_id();

    log.debug("[TIMELINE_MEDIA] Following NextMaxID: " + followingNextMaxId);

    return followingNextMaxId;
  }

  private InstagramPostDTO populatePostDetails(TimelineMedia media) {
    var postBuilder =
        InstagramPostDTO.builder()
            .primaryKey(media.getPk())
            .captionText(ofNullable(media.getCaption()).map(Comment::getText).orElse(EMPTY))
            .likeCount(media.getLike_count())
            .commentCount(media.getComment_count())
            .takenAt(ofInstant(ofEpochSecond(media.getTaken_at()), getDefault().toZoneId()));

    postBuilder = fetchLocation(media, postBuilder);

    return timelineMediaService.prepare(media, postBuilder);
  }

  @NotNull
  private Iterator<FeedUsersResponse> prepareFeedIterator(
      InstagramRequestDTO requestDto, FriendshipsFeeds friendshipsFeeds) {
    var username = requestDto.getUsername();
    var nextMaxId = requestDto.getNextMaxId();
    var pk = getUserAction(username).getUser().getPk();
    var friendshipsFeedsRequest = new FriendshipsFeedsRequest(pk, friendshipsFeeds, nextMaxId);

    return new FeedIterable<>(igClient, () -> friendshipsFeedsRequest).iterator();
  }

  @NotNull
  private List<InstagramProfileDTO> getFeedDetails(
      InstagramRequestDTO requestDto, Iterator<FeedUsersResponse> feedUserIterator) {
    var userDetailsDtos = new HashSet<InstagramProfileDTO>();
    var nextMaxIds = new ArrayList<String>();
    var requestRounds = 0;

    while (feedUserIterator.hasNext() && requestRounds < requestDto.getRounds()) {
      FeedUsersResponse response = feedUserIterator.next();

      var preparedUserDetails =
          response.getUsers().stream()
              .map(this::buildCommonProfileDetails)
              .map(InstagramProfileDTOBuilder::build)
              .toList();

      nextMaxIds.add(response.getNext_max_id());
      userDetailsDtos.addAll(preparedUserDetails);
      requestRounds++;
      wait(requestDto.getTimeout());
    }

    log.info("[FEED_USER] List of Next Max IDs: " + join(", ", nextMaxIds));

    return new ArrayList<>(userDetailsDtos);
  }

  private InstagramPostDTOBuilder fetchLocation(
      TimelineMedia media, InstagramPostDTOBuilder postBuilder) {
    var location = media.getLocation();

    if (isEmpty(location)) {
      return postBuilder;
    }

    return postBuilder.location(
        LocationDetailsDTO.builder()
            .primaryKey(location.getPk())
            .name(location.getName())
            .externalSource(location.getExternal_source())
            .lat(location.getLat())
            .lon(location.getLng())
            .address(location.getAddress())
            .build());
  }

  @SneakyThrows
  private void wait(int sec) {
    Thread.sleep(1000L * sec);
  }
}
