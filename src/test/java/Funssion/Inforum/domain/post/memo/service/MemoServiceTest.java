package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemoServiceTest {


    @MockBean
    private MemoServiceTest memoService;

    private static MemoListDto memoListDto1;
    private static MemoListDto memoListDto2;
    private static MemoListDto memoListDto3;
    private static MemoListDto memoListDto4;

    @BeforeAll
    static void beforeAll() {
        memoListDto1 = MemoListDto.builder()
                .memoId(1L)
                .memoTitle("JPA")
                .memoText("JPA is JPA")
                .memoDescription("JPA is ...")
                .memoColor("yellow")
                .authorId(1L)
                .authorName("jinu")
                .authorProfileImagePath("jinu-image")
                .createdDate(LocalDateTime.now())
                .likes(0L)
                .isLike(false)
                .isTemporary(false)
                .build();
        memoListDto2 = MemoListDto.builder()
                .memoId(2L)
                .memoTitle("JDK")
                .memoText("JDK is JDK")
                .memoDescription("JDK is ...")
                .memoColor("blue")
                .authorId(1L)
                .authorName("jinu")
                .authorProfileImagePath("jinu-image")
                .createdDate(LocalDateTime.now())
                .likes(9999L)
                .isLike(true)
                .isTemporary(false)
                .build();
        memoListDto3 = MemoListDto.builder()
                .memoId(3L)
                .memoTitle("JWT")
                .memoText("JWT is JWT")
                .memoDescription("JWT is ...")
                .memoColor("yellow")
                .authorId(2L)
                .authorName("jinu2")
                .authorProfileImagePath("jinu2-image")
                .createdDate(LocalDateTime.now())
                .likes(9999L)
                .isLike(false)
                .isTemporary(false)
                .build();
        memoListDto4 = MemoListDto.builder()
                .memoId(4L)
                .memoTitle("JSP")
                .memoText("JSP is JSP")
                .memoDescription("JSP is ...")
                .memoColor("black")
                .authorId(2L)
                .authorName("jinu2")
                .authorProfileImagePath("jinu2-image")
                .createdDate(LocalDateTime.now())
                .likes(0L)
                .isLike(false)
                .isTemporary(true)
                .build();
    }
}
