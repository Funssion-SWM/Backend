package Funssion.Inforum.domain.series.repository;

import Funssion.Inforum.domain.series.domain.Series;
import Funssion.Inforum.domain.series.dto.request.SeriesRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeriesRepositoryImplTest {

    @Autowired
    SeriesRepository seriesRepository;

    Long userId1 = 999_999L;
    Long userId2 = 999_998L;
    Long seriesId1;
    Long seriesId2;
    Long seriesWithoutImageId;
    Series series1 = Series.builder()
            .title("java")
            .description("java is ...")
            .thumbnailImagePath("https://tmbimage")
            .authorId(userId1)
            .authorName("jinu")
            .authorImagePath("https://image")
            .likes(0L)
            .build();
    Series series2 = Series.builder()
            .title("java")
            .description("java is ...")
            .thumbnailImagePath("https://tmbimage")
            .authorId(userId2)
            .authorName("jinu2")
            .authorImagePath("https://image2")
            .likes(0L)
            .build();
    Series seriesWithoutImage = Series.builder()
            .title("java")
            .description("java is ...")
            .authorId(userId1)
            .authorName("jinu")
            .likes(0L)
            .build();

    @BeforeEach
    void init() {
        seriesId1 = seriesRepository.create(series1);
        seriesId2 = seriesRepository.create(series2);
        seriesWithoutImageId = seriesRepository.create(seriesWithoutImage);
    }

    @Test
    void findById() {
        Series saved1 = seriesRepository.findById(seriesId1).get();
        Series saved2 = seriesRepository.findById(seriesId2).get();
        Series savedWithoutImage = seriesRepository.findById(seriesWithoutImageId).get();

        assertThat(saved1).isEqualTo(series1);
        assertThat(saved2).isEqualTo(series2);
        assertThat(savedWithoutImage).isEqualTo(seriesWithoutImage);
    }

    @Test
    void update() {
        SeriesRequestDto requestDto =
                SeriesRequestDto.builder()
                    .title("updated")
                    .description("updated")
                    .build();
        String newThumbnailImagePath = "https://updated";
        seriesRepository.update(seriesId1, requestDto, newThumbnailImagePath);

        Series updated = seriesRepository.findById(seriesId1).get();

        assertThat(updated.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(updated.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(updated.getThumbnailImagePath()).isEqualTo(newThumbnailImagePath);
    }

    @Test
    void delete() {
        seriesRepository.delete(seriesId1);

        assertThat(seriesRepository.findById(seriesId1)).isNotPresent();
    }
}