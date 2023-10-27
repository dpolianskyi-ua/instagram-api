package com.example.mockmvcintframework.dto.post.partial;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(NON_NULL)
public class LocationDetailsDTO {
  @JsonProperty("primaryKey")
  Long primaryKey;

  @JsonProperty("name")
  String name;

  @JsonProperty("externalSource")
  String externalSource;

  @JsonProperty("latitude")
  Double lat;

  @JsonProperty("longitude")
  Double lon;

  @JsonProperty("address")
  String address;
}
