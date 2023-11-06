package com.example.mockmvcintframework.dto.post;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.example.mockmvcintframework.dto.post.partial.CarouselPostDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.ImagePostDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.LocationDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.VideoPostDetailsDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.*;

@Value
@Builder
@With
@JsonInclude(NON_NULL)
public class InstagramPostDTO {
  @JsonProperty("primaryKey")
  Long primaryKey;

  @JsonProperty("contentType")
  TimelineContentType contentType;

  @JsonProperty("captionText")
  String captionText;

  @JsonProperty("location")
  LocationDetailsDTO location;

  @JsonProperty("takenAt")
  LocalDateTime takenAt;

  @JsonProperty("likeCount")
  Integer likeCount;

  @JsonProperty("commentCount")
  Integer commentCount;

  @JsonProperty("image")
  ImagePostDetailsDTO image;

  @JsonProperty("video")
  VideoPostDetailsDTO video;

  @JsonProperty("carousel")
  CarouselPostDetailsDTO carousel;
}
