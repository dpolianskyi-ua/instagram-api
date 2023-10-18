package com.example.mockmvcintframework.service;

import static com.example.mockmvcintframework.dto.post.TimelineContentType.NA;
import static com.github.instagram4j.instagram4j.utils.IGChallengeUtils.resolveTwoFactor;
import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.example.mockmvcintframework.dto.InstagramCredentialsRequestDTO;
import com.example.mockmvcintframework.dto.post.InstagramFeedDTO;
import com.example.mockmvcintframework.dto.post.InstagramPostDTO;
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

  public InstagramFeedDTO extractFeedDetails(String username) {
    var timeoutSec = 30;
    var user = getUserAction(username).getUser();
    var primaryKey = user.getPk();
    var items = new ArrayList<TimelineMedia>();

    String nextMaxId = getNextTimelineMedia(primaryKey, null, items, timeoutSec);

    while (nextMaxId != null) {
      nextMaxId = getNextTimelineMedia(primaryKey, nextMaxId, items, timeoutSec);
    }

    // TODO: sort incoming array

    //    sortedTimelineMediaItems.putAll(timelineMediaItems);

    return InstagramFeedDTO.builder()
        .posts(items.stream().map(this::populatePostDetails).toList())
        .build();
  }

  public List<InstagramProfileDTO> extractFollowers(String username, boolean isCaptured) {
    return (isCaptured) ? getFeedDetails(getUserAction(username).followersFeed(), 60) : emptyList();
  }

  public List<InstagramProfileDTO> extractFollowing(String username) {
    return getFeedDetails(getUserAction(username).followingFeed(), 60);
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

    return userFeed.getNext_max_id();
  }

  private InstagramPostDTO populatePostDetails(TimelineMedia media) {
    var postBuilder =
        InstagramPostDTO.builder()
            .primaryKey(media.getPk())
            .captionText(ofNullable(media.getCaption()).map(Comment::getText).orElse(EMPTY))
            .likeCount(media.getLike_count())
            .commentCount(media.getComment_count())
            .takenAt(media.getTaken_at())
            .deviceTimestamp(media.getDevice_timestamp());

    if (media instanceof TimelineCarouselMedia) {
      return timelineMediaService.prepareCarousel((TimelineCarouselMedia) media, postBuilder);
    } else if (media instanceof TimelineImageMedia) {
      return timelineMediaService.prepareImage((TimelineImageMedia) media, postBuilder);
    } else if (media instanceof TimelineVideoMedia) {
      return timelineMediaService.prepareVideo((TimelineVideoMedia) media, postBuilder);
    } else {
      return postBuilder.contentType(NA).build();
    }
  }

  @NotNull
  private List<InstagramProfileDTO> getFeedDetails(
      FeedIterable<FriendshipsFeedsRequest, FeedUsersResponse> feed, int timeoutSec) {
    var userDetailsDtos = new HashSet<InstagramProfileDTO>();

    for (FeedUsersResponse feedUsersResponse : feed) {
      List<Profile> users = feedUsersResponse.getUsers();

      var preparedUserDetails =
          users.stream()
              .map(this::buildCommonProfileDetails)
              .map(InstagramProfileDTOBuilder::build)
              .toList();

      userDetailsDtos.addAll(preparedUserDetails);

      wait(timeoutSec);
    }

    return new ArrayList<>(userDetailsDtos);
  }

  @SneakyThrows
  private void wait(int sec) {
    Thread.sleep(1000L * sec);
  }
}
