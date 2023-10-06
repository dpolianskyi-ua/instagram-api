package com.example.mockmvcintframework.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class InstagramCredentialsRequestDTO {
    @JsonProperty("username")
    String username;
    @JsonProperty("password")
    String password;
    @JsonProperty("inputCode")
    String inputCode;
}
