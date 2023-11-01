package com.example.mockmvcintframework.controller;

import static org.springframework.http.ResponseEntity.*;

import com.example.mockmvcintframework.dto.post.InstagramFeedDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileDTO;
import com.example.mockmvcintframework.service.InstagramService;
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
  public ResponseEntity<InstagramProfileDTO> extractProfileDetailsByUsername(
      @RequestParam(value = "username") String username) {
    return ok(instagramService.extractProfileDetails(username));
  }

  @GetMapping("/feed-details")
  public ResponseEntity<InstagramFeedDTO> extractFeedDetailsByUsername(
      @RequestParam(value = "username") String username,
      @RequestParam(value = "timeout", defaultValue = "60", required = false) Integer timeout,
      @RequestParam(value = "rounds", defaultValue = "5", required = false) Integer rounds,
      @RequestParam(value = "nextMaxId", required = false) String nextMaxId) {
    return ok(instagramService.extractFeedDetails(username, timeout, rounds, nextMaxId));
  }

  @GetMapping("/followers-details")
  public ResponseEntity<List<InstagramProfileDTO>> extractFollowersDetailsByUsername(
      @RequestParam(value = "username") String username,
      @RequestParam(value = "timeout", defaultValue = "60", required = false) Integer timeout,
      @RequestParam(value = "rounds", defaultValue = "5", required = false) Integer rounds,
      @RequestParam(value = "nextMaxId", required = false) String nextMaxId) {
    return ok(instagramService.extractFollowers(username, timeout, rounds, nextMaxId));
  }

  @GetMapping("/following-details")
  public ResponseEntity<List<InstagramProfileDTO>> extractFollowingDetailsByUsername(
      @RequestParam(value = "username") String username,
      @RequestParam(value = "timeout", defaultValue = "60", required = false) Integer timeout,
      @RequestParam(value = "rounds", defaultValue = "5", required = false) Integer rounds,
      @RequestParam(value = "nextMaxId", required = false) String nextMaxId) {
    return ok(instagramService.extractFollowing(username, timeout, rounds, nextMaxId));
  }
}
