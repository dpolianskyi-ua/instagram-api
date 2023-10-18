package com.example.mockmvcintframework.controller;

import com.example.mockmvcintframework.dto.post.InstagramFeedDTO;
import com.example.mockmvcintframework.dto.profile.InstagramProfileDTO;
import com.example.mockmvcintframework.service.InstagramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instagram/extract")
@RequiredArgsConstructor
public class InstagramExtractController {

  private final InstagramService instagramService;

  @GetMapping("/profile-details")
  public ResponseEntity<InstagramProfileDTO> extractProfileDetailsByUsername(
      @RequestParam(value = "username") String username) {
    return ResponseEntity.ok(instagramService.extractProfileDetails(username));
  }

  @GetMapping("/feed-details")
  public ResponseEntity<InstagramFeedDTO> extractFeedDetailsByUsername(
      @RequestParam(value = "username") String username) {
    return ResponseEntity.ok(instagramService.extractFeedDetails(username));
  }

  @GetMapping("/followers-details")
  public ResponseEntity<List<InstagramProfileDTO>> extractFollowersDetailsByUsername(
      @RequestParam(value = "username") String username,
      @RequestParam(value = "captured", required = false) boolean isCaptured) {
    return ResponseEntity.ok(instagramService.extractFollowers(username, isCaptured));
  }

  @GetMapping("/following-details")
  public ResponseEntity<List<InstagramProfileDTO>> extractFollowingDetailsByUsername(
      @RequestParam(value = "username") String username) {
    return ResponseEntity.ok(instagramService.extractFollowing(username));
  }
}
