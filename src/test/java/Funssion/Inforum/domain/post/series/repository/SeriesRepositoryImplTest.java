package Funssion.Inforum.domain.post.series.repository;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.domain.post.series.domain.Series;
import Funssion.Inforum.domain.post.series.dto.request.SeriesRequestDto;
import Funssion.Inforum.domain.score.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static Funssion.Inforum.common.constant.DateType.DAY;
import static Funssion.Inforum.common.constant.OrderType.NEW;
import static Funssion.Inforum.common.constant.Sign.MINUS;
import static Funssion.Inforum.common.constant.Sign.PLUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SeriesRepositoryImplTest {

    @Autowired
    SeriesRepository seriesRepository;

    Long DEFAULT_PAGE_NUM = 0L;
    Long DEFAULT_RESULT_COUNT = 12L;
    Long USER_ID_1 = 999_999L;
    Long USER_ID_2 = 999_998L;
    Long seriesId1;
    Long seriesId2;
    Long seriesWithoutImageId;
    Series series1 = Series.builder()
            .title("java")
            .description("java is ...")
            .thumbnailImagePath("https://tmbimage")
            .authorId(USER_ID_1)
            .authorName("jinu")
            .rank(Rank.BRONZE_5.toString())
            .authorImagePath("https://image")
            .likes(0L)
            .build();
    Series series2 = Series.builder()
            .title("jpa")
            .description("jpa is ...")
            .thumbnailImagePath("https://tmbimage")
            .authorId(USER_ID_2)
            .rank(Rank.BRONZE_5.toString())
            .authorName("jinu2")
            .authorImagePath("https://image2")
            .likes(0L)
            .build();
    Series seriesWithoutImage = Series.builder()
            .title("jdk")
            .description("jdk is ...")
            .authorId(USER_ID_1)
            .rank(Rank.BRONZE_5.toString())
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
    @DisplayName("시리즈 단일 조회하기")
    void findById() {
        Series saved1 = seriesRepository.findById(seriesId1).get();
        Series saved2 = seriesRepository.findById(seriesId2).get();
        Series savedWithoutImage = seriesRepository.findById(seriesWithoutImageId).get();

        assertThat(saved1).isEqualTo(series1);
        assertThat(saved2).isEqualTo(series2);
        assertThat(savedWithoutImage).isEqualTo(seriesWithoutImage);
    }

    @Nested
    @DisplayName("시리즈 여러 개 조회하기")
    class findAll {

        @Test
        @DisplayName("일반 조회")
        void defaultFindAll() {
            List<Series> seriesList = seriesRepository.findAllBy(DAY, NEW, DEFAULT_PAGE_NUM, DEFAULT_RESULT_COUNT);
            assertThat(seriesList).containsExactly(seriesWithoutImage, series2, series1);

            for (int i = 0; i < 12; i++) {
                seriesRepository.create(series1);
            }

            List<Series> seriesListWithOneElement = seriesRepository.findAllBy(DAY, NEW, DEFAULT_PAGE_NUM, DEFAULT_RESULT_COUNT);
            assertThat(seriesListWithOneElement).containsOnly(series1);
        }

        @Test
        @DisplayName("유저 아이디로 조회")
        void findAllByUserId() {
            List<Series> seriesList = seriesRepository.findAllBy(USER_ID_1, DEFAULT_PAGE_NUM, DEFAULT_RESULT_COUNT);
            assertThat(seriesList).containsExactly(seriesWithoutImage, series1);

            for (int i = 0; i < 12; i++) {
                seriesRepository.create(series1);
            }

            List<Series> seriesListWithOneElement = seriesRepository.findAllBy(USER_ID_1, DEFAULT_PAGE_NUM, DEFAULT_RESULT_COUNT);
            assertThat(seriesListWithOneElement).containsOnly(series1);
        }

        @Test
        @DisplayName("시리즈 검색")
        void searchSeries() {
            List<Series> seriesList = seriesRepository.findAllBy(List.of("java", "jdk"), DAY, NEW, DEFAULT_PAGE_NUM, DEFAULT_RESULT_COUNT);
            assertThat(seriesList).containsExactly(seriesWithoutImage, series1);
        }
    }


    @Nested
    @DisplayName("시리즈 수정하기")
    class update {

        @Test
        @DisplayName("시리즈 내용 수정하기")
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
        @DisplayName("시리즈 좋아요 수 수정하기")
        void updateLikes() {
            seriesRepository.updateLikes(seriesId1, PLUS);
            Series likedSeries = seriesRepository.findById(seriesId1).get();

            assertThat(likedSeries.getLikes()).isEqualTo(1);

            seriesRepository.updateLikes(seriesId1, MINUS);
            Series unlikedSeries = seriesRepository.findById(seriesId1).get();

            assertThat(unlikedSeries.getLikes()).isEqualTo(0);
        }

        @Test
        @DisplayName("시리즈 좋아요 수 음수로 수정하기")
        void updateLikesToNegative() {
            assertThatThrownBy(() -> seriesRepository.updateLikes(seriesId1, MINUS))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Test
    @DisplayName("시리즈 삭제하기")
    void delete() {
        seriesRepository.delete(seriesId1);

        assertThat(seriesRepository.findById(seriesId1)).isNotPresent();
    }
}