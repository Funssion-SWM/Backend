package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.tag.repository.TagRepository;
import Funssion.Inforum.s3.S3Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemoServiceTest {

    @Mock MemoRepository memoRepository;
    @Mock TagRepository tagRepository;
    @Mock MyRepository myRepository;
    @Mock S3Repository s3Repository;
    @InjectMocks MemoService memoService;


    static Memo memo1;
    static Memo memo2;
    static Memo memo3;
    static Memo memo4;

    @BeforeAll
    static void beforeAll() {
        memo1 = Memo.builder()
                .id(1L)
                .title("JPA")
                .text("JPA is JPA")
                .description("JPA is ...")
                .color("yellow")
                .authorId(1L)
                .authorName("jinu")
                .authorImagePath("jinu-image")
                .createdDate(LocalDateTime.now())
                .likes(0L)
                .isTemporary(false)
                .build();
        memo2 = Memo.builder()
                .id(2L)
                .title("JDK")
                .text("JDK is JDK")
                .description("JDK is ...")
                .color("blue")
                .authorId(1L)
                .authorName("jinu")
                .authorImagePath("jinu-image")
                .createdDate(LocalDateTime.now())
                .likes(9999L)
                .isTemporary(false)
                .build();
        memo3 = Memo.builder()
                .id(3L)
                .title("JWT")
                .text("JWT is JWT")
                .description("JWT is ...")
                .color("yellow")
                .authorId(2L)
                .authorName("jinu2")
                .authorImagePath("jinu2-image")
                .createdDate(LocalDateTime.now())
                .likes(9999L)
                .isTemporary(false)
                .build();
        memo4 = Memo.builder()
                .id(4L)
                .title("JSP")
                .text("JSP is JSP")
                .description("JSP is ...")
                .color("black")
                .authorId(2L)
                .authorName("jinu2")
                .authorImagePath("jinu2-image")
                .createdDate(LocalDateTime.now())
                .likes(0L)
                .isTemporary(true)
                .build();
    }


    @Nested()
    @DisplayName("메모 조회하기")
    class MemoRead {

        List<Memo> foundOrderById = List.of(memo3, memo2, memo1);
        List<Memo> foundByDaysOrderByLikes = List.of(memo3, memo2, memo1);

        @Test
        @DisplayName("메인 페이지 메모 조회")
        void getMemosForMainPage() {
            when(memoRepository.findAllOrderById()).thenReturn(foundOrderById);
            when(memoRepository.findAllByDaysOrderByLikes(ArgumentMatchers.any())).thenReturn(foundByDaysOrderByLikes);

            memoService.getMemosForMainPage(DateType.DAY, OrderType.HOT);
        }
    }
}
