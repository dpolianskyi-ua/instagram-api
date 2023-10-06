package com.example.mockmvcintframework.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class AccommodationDTO {
    @JsonProperty("id")
    Long id;
    @JsonProperty("addressId")
    Long addressId;
    @JsonProperty("personId")
    Long personId;
    @JsonProperty("accommodationDate")
    LocalDate accommodationDate;
    @JsonProperty("isSingleOwned")
    Boolean isSingleOwned;
}
