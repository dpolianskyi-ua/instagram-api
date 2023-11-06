package com.example.mockmvcintframework.dto.post.partial;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(NON_NULL)
public class CarouselItemDTO {
  @JsonProperty("parentId")
  String parentId;

  @JsonProperty("contentType")
  CarouselItemContentType contentType;

  @JsonProperty("originalWidth")
  Integer originalWidth;

  @JsonProperty("originalHeight")
  Integer originalHeight;

  @JsonProperty("width")
  Integer width;

  @JsonProperty("height")
  Integer height;

  @JsonProperty("url")
  String url;
}
