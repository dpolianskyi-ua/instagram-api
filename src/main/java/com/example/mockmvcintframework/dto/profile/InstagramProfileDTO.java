package com.example.mockmvcintframework.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class InstagramProfileDTO {
  @JsonProperty("primaryKey")
  Long primaryKey;

  @JsonProperty("isPrivate")
  Boolean isPrivate;

  @JsonProperty("username")
  String username;

  @JsonProperty("fullName")
  String fullName;

  @JsonProperty("externalURL")
  String externalURL;

  @JsonProperty("profilePicture")
  InstagramProfilePictureDTO profilePicture;

  @JsonProperty("biography")
  String biography;

  @JsonProperty("counts")
  InstagramProfileCountsDTO profileCounts;
}
