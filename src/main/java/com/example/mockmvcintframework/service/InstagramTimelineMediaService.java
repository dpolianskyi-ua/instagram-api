package com.example.mockmvcintframework.service;

import static com.example.mockmvcintframework.dto.post.TimelineContentType.*;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.example.mockmvcintframework.dto.post.InstagramPostDTO;
import com.example.mockmvcintframework.dto.post.InstagramPostDTO.InstagramPostDTOBuilder;
import com.example.mockmvcintframework.dto.post.partial.*;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramTimelineMediaService {

  public InstagramPostDTO prepare(TimelineMedia media, InstagramPostDTOBuilder postBuilder) {
    if (media instanceof TimelineCarouselMedia) {
      return prepareCarousel((TimelineCarouselMedia) media, postBuilder);
    } else if (media instanceof TimelineImageMedia) {
      return prepareImage((TimelineImageMedia) media, postBuilder);
    } else if (media instanceof TimelineVideoMedia) {
      return prepareVideo((TimelineVideoMedia) media, postBuilder);
    } else {
      return postBuilder.contentType(NA).build();
    }
  }

  private InstagramPostDTO prepareImage(
      TimelineImageMedia media, InstagramPostDTOBuilder postBuilder) {
    var imageVersionsMetas = media.getCandidates();

    if (isEmpty(imageVersionsMetas)) {
      log.warn("No image detected. TimelineImageMedia: [{}]", media);

      return postBuilder.contentType(IMAGE).build();
    } else {
      var primaryMeta = imageVersionsMetas.get(0);

      var imageDetails =
          ImagePostDetailsDTO.builder()
              .viewCount(media.getView_count())
              .width(primaryMeta.getWidth())
              .height(primaryMeta.getHeight())
              .url(primaryMeta.getUrl())
              .build();

      return postBuilder.contentType(IMAGE).image(imageDetails).build();
    }
  }

  private InstagramPostDTO prepareVideo(
      TimelineVideoMedia media, InstagramPostDTOBuilder postBuilder) {
    var videoVersionsMetas = media.getVideo_versions();

    if (isEmpty(videoVersionsMetas)) {
      log.warn("No video detected. TimelineVideoMedia: [{}]", media);

      return postBuilder.contentType(VIDEO).build();
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

      return postBuilder.contentType(VIDEO).video(videoDetails).build();
    }
  }

  private InstagramPostDTO prepareCarousel(
      TimelineCarouselMedia media, InstagramPostDTOBuilder postBuilder) {
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

    return postBuilder.contentType(CAROUSEL).carousel(carouselDetails).build();
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
