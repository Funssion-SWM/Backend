package Funssion.Inforum.domain.post.series;

import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.series.domain.Series;
import Funssion.Inforum.domain.post.series.repository.SeriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class SeriesIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    SeriesRepository seriesRepository;
    @Autowired
    MemoRepository memoRepository;

    Long USER_ID_1 = 9999L;
    Long USER_ID_2 = 10000L;
    Memo memo1 = Memo.builder()
            .authorId(USER_ID_1)
            .authorName("jinu")
            .authorImagePath("https://image")
            .title("java is")
            .description("java is ...")
            .text("{\"content\" : \"java is good\"}")
            .color("yellow")
            .memoTags(List.of("java"))
            .isTemporary(false)
            .build();
    Memo memo2 = Memo.builder()
            .authorId(USER_ID_1)
            .authorName("jinu")
            .authorImagePath("https://image")
            .title("jpa is")
            .description("jpa is ...")
            .text("{\"content\" : \"jpa is good\"}")
            .color("yellow")
            .memoTags(List.of("jpa"))
            .isTemporary(false)
            .build();
    Memo memo3 = Memo.builder()
            .authorId(USER_ID_1)
            .authorName("jinu")
            .authorImagePath("https://image")
            .title("jsp is")
            .description("jsp is ...")
            .text("{\"content\" : \"jsp is good\"}")
            .color("yellow")
            .memoTags(List.of("jsp"))
            .isTemporary(false)
            .build();
    Memo memo4 = Memo.builder()
            .authorId(USER_ID_2)
            .authorName("jinu2")
            .authorImagePath("https://image2")
            .title("jpa is")
            .description("jpa is ...")
            .text("{\"content\" : \"jpa is good\"}")
            .color("yellow")
            .memoTags(List.of("jpa"))
            .isTemporary(false)
            .build();
    Memo memo5 = Memo.builder()
            .authorId(USER_ID_2)
            .authorName("jinu2")
            .authorImagePath("https://image2")
            .title("jpa is")
            .description("jpa is ...")
            .text("{\"content\" : \"jpa is good\"}")
            .color("yellow")
            .memoTags(List.of("jpa"))
            .isTemporary(true)
            .build();
    Memo createdMemo1;
    Memo createdMemo2;
    Memo createdMemo3;
    Memo createdMemo4;
    Memo createdMemo5;
    Series series1 = Series.builder()
            .authorId(USER_ID_1)
            .authorName("jinu")
            .authorImagePath("https://image")
            .title("java is")
            .description("java is good")
            .thumbnailImagePath("https://java")
            .likes(0L)
            .build();
    Series series2 = Series.builder()
            .authorId(USER_ID_2)
            .authorName("jinu2")
            .authorImagePath("https://image2")
            .title("jpa is")
            .description("jpa is good")
            .thumbnailImagePath("https://java")
            .likes(0L)
            .build();
    Series createdSeries1;
    Series createdSeries2;

    MockMultipartFile mockMultipartfile;

    @BeforeEach
    void init() throws IOException {
        createdMemo1 = memoRepository.create(memo1);
        createdMemo2 = memoRepository.create(memo2);
        createdMemo3 = memoRepository.create(memo3);
        createdMemo4 = memoRepository.create(memo4);
        createdMemo5 = memoRepository.create(memo5);

        Long seriesId1 = seriesRepository.create(series1);
        Long seriesId2 = seriesRepository.create(series2);

        memoRepository.updateSeriesIds(seriesId1, USER_ID_1, List.of(createdMemo1.getId()));
        memoRepository.updateSeriesIds(seriesId2, USER_ID_2, List.of(createdMemo4.getId()));

        createdSeries1 = seriesRepository.findById(seriesId1).get();
        createdSeries2 = seriesRepository.findById(seriesId2).get();

        mockMultipartfile = new MockMultipartFile(
                "thumbnailImage",
                "asd.png",
                "image/png",
                new FileInputStream("src/main/resources/static/asd.png"));
    }

    /** memo1,2,3 with user1, memo4,5 with user2
     * memo5 is temporal memo
     * memo1 in series1
     * memo4 in series2
     */
    @Nested
    @DisplayName("시리즈 조회하기")
    class getSeries {

        @Test
        @DisplayName("예외 입력 케이스")
        void exBadRequest() throws Exception {
            mvc.perform(get("/series")
                    .param("period", "30"))
                    .andExpect(status().isBadRequest());

            mvc.perform(get("/series")
                            .param("period", "weeks"))
                    .andExpect(status().isBadRequest());

            mvc.perform(get("/series")
                            .param("orderBy", "newest"))
                    .andExpect(status().isBadRequest());

            mvc.perform(get("/series")
                            .param("pageNum", "-1"))
                    .andExpect(status().isBadRequest());

            mvc.perform(get("/series")
                            .param("resultCntPerPage", "0"))
                    .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("시리즈 단일 조회하기")
        void getSingleSeries() throws Exception {
            mvc.perform(get("/series/" + createdSeries1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries1.getId())));

            mvc.perform(get("/series/" + createdSeries2.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries2.getId())));
        }

        @Test
        @DisplayName("시리즈 리스트 메인 페이지에서 조회하기")
        void getSeriesTest() throws Exception {
            mvc.perform(get("/series"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries2.getId())))
                    .andExpect(content().string(containsString("\"id\":" + createdSeries1.getId())));

            mvc.perform(get("/series")
                            .param("period", "day")
                            .param("orderBy", "new")
                            .param("pageNum", "0")
                            .param("resultCntPerPage", "12"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries2.getId())))
                    .andExpect(content().string(containsString("\"id\":" + createdSeries1.getId())));
        }

        @Test
        @DisplayName("시리즈 검색하기")
        void searchSeries() throws Exception {
            mvc.perform(get("/series")
                    .param("searchString", "java"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries1.getId())));

            mvc.perform(get("/series")
                    .param("searchString", "java jpa")
                    .param("orderBy", "new"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries2.getId())))
                    .andExpect(content().string(containsString("\"id\":" + createdSeries1.getId())));

            mvc.perform(get("/series")
                    .param("searchString", "")
                    .param("orderBy", "new"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries2.getId())))
                    .andExpect(content().string(containsString("\"id\":" + createdSeries1.getId())));

            mvc.perform(get("/series")
                            .param("searchString", " ")
                            .param("orderBy", "new"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"id\":" + createdSeries2.getId())))
                    .andExpect(content().string(containsString("\"id\":" + createdSeries1.getId())));
        }
    }

//    @Nested
//    @DisplayName("시리즈 생성하기")
//    class createSeries {
//
//        @Test
//        @DisplayName("인증 되지 않은 케이스")
//        void exUnAuthorized() throws Exception {
//            mvc.perform(multipart("/series")
//                    .file(mockMultipartfile)
//                    .param("title", "jdk")
//                    .param("description", "jdk is ...")
//                    .param("memoIdList", List.of(createdMemo2.getId(), createdMemo3.getId()).toString()))
//                    .andExpect(status().isUnauthorized());
//        }
//    }
}
