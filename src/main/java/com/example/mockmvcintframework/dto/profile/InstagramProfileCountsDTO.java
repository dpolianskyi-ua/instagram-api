package com.example.mockmvcintframework.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class InstagramProfileCountsDTO {
  @JsonProperty("media")
  Integer media;

  @JsonProperty("followers")
  Integer followers;

  @JsonProperty("following")
  Integer following;
}
