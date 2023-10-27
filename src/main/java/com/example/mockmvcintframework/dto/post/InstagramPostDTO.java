package com.example.mockmvcintframework.dto.post;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.example.mockmvcintframework.dto.post.partial.CarouselPostDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.ImagePostDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.LocationDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.VideoPostDetailsDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonInclude(NON_NULL)
public class InstagramPostDTO {
  @JsonProperty("primaryKey")
  private Long primaryKey;

  @JsonProperty("contentType")
  private TimelineContentType contentType;

  @JsonProperty("captionText")
  private String captionText;

  @JsonProperty("location")
  private LocationDetailsDTO location;

  @JsonProperty("takenAt")
  private LocalDateTime takenAt;

  @JsonProperty("likeCount")
  private Integer likeCount;

  @JsonProperty("commentCount")
  private Integer commentCount;

  @JsonProperty("image")
  private ImagePostDetailsDTO image;

  @JsonProperty("video")
  private VideoPostDetailsDTO video;

  @JsonProperty("carousel")
  private CarouselPostDetailsDTO carousel;
}
