package com.example.mockmvcintframework.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@JsonInclude(NON_NULL)
public class InstagramRequestDTO {
  @JsonProperty("username")
  @NotNull
  private String username;

  @JsonProperty("timeout")
  private Integer timeout = 60;

  @JsonProperty("rounds")
  private Integer rounds = 5;

  @JsonProperty("nextMaxId")
  private String nextMaxId;
}
