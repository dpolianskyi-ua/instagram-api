package com.example.mockmvcintframework.controller;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWERS;
import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsFeedsRequest.FriendshipsFeeds.FOLLOWING;
import static org.springframework.http.ResponseEntity.*;

import com.example.mockmvcintframework.dto.InstagramRequestDTO;
import com.example.mockmvcintframework.dto.post.InstagramFeedDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileDTO;
import com.example.mockmvcintframework.service.InstagramService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/instagram/extract")
@RequiredArgsConstructor
public class InstagramExtractController {

  private final InstagramService instagramService;

  @GetMapping("/profile-details")
  public ResponseEntity<InstagramProfileDTO> getProfileDetails(
      @RequestParam(value = "username") String username) {
    return ok(instagramService.extractProfileDetails(username));
  }

  @GetMapping("/feed-details")
  public ResponseEntity<InstagramFeedDTO> getFeedDetails(@Valid InstagramRequestDTO dto) {
    return ok(instagramService.extractFeedDetails(dto));
  }

  @GetMapping("/followers-details")
  public ResponseEntity<List<InstagramProfileDTO>> getFollowers(@Valid InstagramRequestDTO dto) {
    return ok(instagramService.extractUserProfiles(dto, FOLLOWERS));
  }

  @GetMapping("/following-details")
  public ResponseEntity<List<InstagramProfileDTO>> getFollowing(@Valid InstagramRequestDTO dto) {
    return ok(instagramService.extractUserProfiles(dto, FOLLOWING));
  }
}
