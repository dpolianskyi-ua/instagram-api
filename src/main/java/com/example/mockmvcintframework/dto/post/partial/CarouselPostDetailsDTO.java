package com.example.mockmvcintframework.dto.post.partial;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class CarouselPostDetailsDTO {
  @JsonProperty("mediaCount")
  Integer mediaCount;

  @JsonProperty("items")
  List<CarouselItemDTO> items;
}
