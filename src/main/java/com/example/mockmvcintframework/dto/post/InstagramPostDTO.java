package com.example.mockmvcintframework.dto.post;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.example.mockmvcintframework.dto.post.partial.CarouselPostDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.ImagePostDetailsDTO;
import com.example.mockmvcintframework.dto.post.partial.VideoPostDetailsDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("carousel")
    private CarouselPostDetailsDTO carousel;
    @JsonProperty("image")
    private ImagePostDetailsDTO image;
    @JsonProperty("video")
    private VideoPostDetailsDTO video;
    @JsonProperty("captionText")
    private String captionText;
    @JsonProperty("likeCount")
    private Integer likeCount;
    @JsonProperty("commentCount")
    private Integer commentCount;
    @JsonProperty("takenAt")
    private Long takenAt;
    @JsonProperty("deviceTimestamp")
    private Long deviceTimestamp;
}
