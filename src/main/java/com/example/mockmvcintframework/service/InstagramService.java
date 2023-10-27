package com.example.mockmvcintframework.service;

import static com.github.instagram4j.instagram4j.utils.IGChallengeUtils.resolveTwoFactor;
import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.lang.String.*;
import static java.time.Instant.*;
import static java.time.LocalDateTime.*;
import static java.util.Optional.ofNullable;
import static java.util.TimeZone.*;
import static org.apache.commons.lang3.ObjectUtils.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.example.mockmvcintframework.dto.InstagramCredentialsRequestDTO;
import com.example.mockmvcintframework.dto.post.InstagramFeedDTO;
import com.example.mockmvcintframework.dto.post.InstagramPostDTO;
import com.example.mockmvcintframework.dto.post.partial.LocationDetailsDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileCountsDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileDTO.InstagramProfileDTOBuilder;
import com.example.mockmvcintframework.dto.profile.InstagramProfilePictureDTO;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.location.Location;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
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

  private IGClient client;

  @SneakyThrows
  public void initializeClient(InstagramCredentialsRequestDTO dto) {
    if (isNotBlank(dto.getInputCode())) {
      client =
          IGClient.builder()
              .username(dto.getUsername())
              .password(dto.getPassword())
              .onTwoFactor(
                  (igClient, loginResponse) ->
                      resolveTwoFactor(igClient, loginResponse, dto::getInputCode))
              .login();
    } else {
      client = IGClient.builder().username(dto.getUsername()).password(dto.getPassword()).login();
    }
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

  public InstagramFeedDTO extractFeedDetails(
      String username, String nextMaxId, int timeout, int rounds) {
    var user = getUserAction(username).getUser();
    var primaryKey = user.getPk();
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

  public List<InstagramProfileDTO> extractFollowers(String username, int timeout, int rounds) {
    return getFeedDetails(getUserAction(username).followersFeed().iterator(), timeout, rounds);
  }

  public List<InstagramProfileDTO> extractFollowing(String username, int timeout, int rounds) {
    return getFeedDetails(getUserAction(username).followingFeed().iterator(), timeout, rounds);
  }

  @SneakyThrows
  private UserAction getUserAction(String username) {
    return client.actions().users().findByUsername(username).get();
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

    var userFeed = new FeedUserRequest(pk, nextMaxId).execute(client).join();

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

    Location location = media.getLocation();

    if (isNotEmpty(location)) {
      postBuilder =
          postBuilder.location(
              LocationDetailsDTO.builder()
                  .primaryKey(location.getPk())
                  .name(location.getName())
                  .externalSource(location.getExternal_source())
                  .lat(location.getLat())
                  .lon(location.getLng())
                  .address(location.getAddress())
                  .build());
    }

    return timelineMediaService.prepare(media, postBuilder);
  }

  @NotNull
  private List<InstagramProfileDTO> getFeedDetails(
      Iterator<FeedUsersResponse> feedUserIterator, int timeout, int rounds) {
    var userDetailsDtos = new HashSet<InstagramProfileDTO>();
    var nextMaxIds = new ArrayList<String>();
    var requestRounds = 0;

    while (feedUserIterator.hasNext() && requestRounds < rounds) {
      FeedUsersResponse response = feedUserIterator.next();

      var preparedUserDetails =
          response.getUsers().stream()
              .map(this::buildCommonProfileDetails)
              .map(InstagramProfileDTOBuilder::build)
              .toList();

      nextMaxIds.add(response.getNext_max_id());
      userDetailsDtos.addAll(preparedUserDetails);
      requestRounds++;
      wait(timeout);
    }

    log.info("[FEED_USER] List of Next Max IDs: " + join(", ", nextMaxIds));

    return new ArrayList<>(userDetailsDtos);
  }

  @SneakyThrows
  private void wait(int sec) {
    Thread.sleep(1000L * sec);
  }
}
