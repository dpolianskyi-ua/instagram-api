package com.example.mockmvcintframework.service;

import static com.example.mockmvcintframework.dto.post.TimelineContentType.*;
import static java.time.Instant.ofEpochSecond;
import static java.time.LocalDateTime.ofInstant;
import static java.util.Optional.ofNullable;
import static java.util.TimeZone.getDefault;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.example.mockmvcintframework.dto.post.InstagramPostDTO;
import com.example.mockmvcintframework.dto.post.partial.*;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramTimelineMediaService {

  public InstagramPostDTO prepare(TimelineMedia media) {
    var post = prepareBasePost(media);

    if (media instanceof TimelineCarouselMedia) {
      return prepareCarousel((TimelineCarouselMedia) media, post);
    } else if (media instanceof TimelineImageMedia) {
      return prepareImage((TimelineImageMedia) media, post);
    } else if (media instanceof TimelineVideoMedia) {
      return prepareVideo((TimelineVideoMedia) media, post);
    } else {
      return post.withContentType(NA);
    }
  }

  private InstagramPostDTO prepareBasePost(TimelineMedia media) {
    var postBuilder =
        InstagramPostDTO.builder()
            .primaryKey(media.getPk())
            .captionText(ofNullable(media.getCaption()).map(Comment::getText).orElse(EMPTY))
            .likeCount(media.getLike_count())
            .commentCount(media.getComment_count())
            .takenAt(ofInstant(ofEpochSecond(media.getTaken_at()), getDefault().toZoneId()))
            .build();

    return (media.getLocation() == null)
        ? postBuilder
        : postBuilder.withLocation(getLocation(media));
  }

  private LocationDetailsDTO getLocation(TimelineMedia media) {
    var location = media.getLocation();

    return LocationDetailsDTO.builder()
        .primaryKey(location.getPk())
        .name(location.getName())
        .externalSource(location.getExternal_source())
        .lat(location.getLat())
        .lon(location.getLng())
        .address(location.getAddress())
        .build();
  }

  private InstagramPostDTO prepareImage(TimelineImageMedia media, InstagramPostDTO post) {
    var imageVersionsMetas = media.getCandidates();

    if (isEmpty(imageVersionsMetas)) {
      log.warn("No image detected. TimelineImageMedia: [{}]", media);

      return post.withContentType(IMAGE);
    } else {
      var primaryMeta = imageVersionsMetas.get(0);

      var imageDetails =
          ImagePostDetailsDTO.builder()
              .viewCount(media.getView_count())
              .width(primaryMeta.getWidth())
              .height(primaryMeta.getHeight())
              .url(primaryMeta.getUrl())
              .build();

      return post.withContentType(IMAGE).withImage(imageDetails);
    }
  }

  private InstagramPostDTO prepareVideo(TimelineVideoMedia media, InstagramPostDTO post) {
    var videoVersionsMetas = media.getVideo_versions();

    if (isEmpty(videoVersionsMetas)) {
      log.warn("No video detected. TimelineVideoMedia: [{}]", media);

      return post.withContentType(VIDEO);
    } else {
      var primaryMeta = videoVersionsMetas.get(0);

      var videoDetails =
          VideoPostDetailsDTO.builder()
              .viewCount(media.getView_count())
              .duration(media.getVideo_duration())
              .width(primaryMeta.getWidth())
              .height(primaryMeta.getHeight())
              .url(primaryMeta.getUrl())
              .build();

      return post.withContentType(VIDEO).withVideo(videoDetails);
    }
  }

  private InstagramPostDTO prepareCarousel(TimelineCarouselMedia media, InstagramPostDTO post) {
    var carouselItems =
        media.getCarousel_media().stream()
            .map(
                item -> {
                  var itemBuilder =
                      CarouselItemDTO.builder()
                          .parentId(item.getCarousel_parent_id())
                          .originalWidth(item.getOriginal_width())
                          .originalHeight(item.getOriginal_height());

                  return (item instanceof ImageCarouselItem)
                      ? prepareImageCarouselItem((ImageCarouselItem) item, itemBuilder)
                      : (item instanceof VideoCarouselItem)
                          ? prepareVideoCarouselItem((VideoCarouselItem) item, itemBuilder)
                          : itemBuilder.build();
                })
            .toList();

    var carouselDetails =
        CarouselPostDetailsDTO.builder()
            .mediaCount(media.getCarousel_media_count())
            .items(carouselItems)
            .build();

    return post.withContentType(CAROUSEL).withCarousel(carouselDetails);
  }

  private CarouselItemDTO prepareImageCarouselItem(
      ImageCarouselItem item, CarouselItemDTO.CarouselItemDTOBuilder itemBuilder) {
    var imageVersionsMetas = item.getImage_versions2().getCandidates();

    if (isEmpty(imageVersionsMetas)) {
      log.warn("No carousel image detected. ImageCarouselItem: [{}]", item);

      return itemBuilder.contentType(CarouselItemContentType.IMAGE).build();
    } else {
      var primaryMeta = imageVersionsMetas.get(0);

      return itemBuilder
          .contentType(CarouselItemContentType.IMAGE)
          .width(primaryMeta.getWidth())
          .height(primaryMeta.getHeight())
          .url(primaryMeta.getUrl())
          .build();
    }
  }

  private CarouselItemDTO prepareVideoCarouselItem(
      VideoCarouselItem item, CarouselItemDTO.CarouselItemDTOBuilder itemBuilder) {
    var videoVersionsMetas = item.getVideo_versions();

    if (isEmpty(videoVersionsMetas)) {
      log.warn("No carousel video detected. VideoCarouselItem: [{}]", item);

      return itemBuilder.contentType(CarouselItemContentType.VIDEO).build();
    } else {
      var primaryMeta = videoVersionsMetas.get(0);

      return itemBuilder
          .contentType(CarouselItemContentType.VIDEO)
          .width(primaryMeta.getWidth())
          .height(primaryMeta.getHeight())
          .url(primaryMeta.getUrl())
          .build();
    }
  }
}
