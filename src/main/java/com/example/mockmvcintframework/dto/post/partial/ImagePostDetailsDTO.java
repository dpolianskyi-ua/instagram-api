package com.example.mockmvcintframework.dto.post.partial;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class ImagePostDetailsDTO {
  @JsonProperty("width")
  Integer width;

  @JsonProperty("height")
  Integer height;

  @JsonProperty("url")
  String url;

  @JsonProperty("viewCount")
  Integer viewCount;
}
