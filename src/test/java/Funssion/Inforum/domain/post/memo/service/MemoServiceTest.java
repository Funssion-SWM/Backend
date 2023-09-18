package Funssion.Inforum.domain.post.memo.service;

import Funssion.Inforum.common.constant.DateType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.domain.tag.repository.TagRepository;
import Funssion.Inforum.s3.S3Repository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemoServiceTest {

    @Mock MemoRepository memoRepository;
    @Mock TagRepository tagRepository;
    @Mock MyRepository myRepository;
    @Mock S3Repository s3Repository;
    @Mock AuthUtils authUtils;
    @InjectMocks MemoService memoService;


    static Memo memo1;
    static Memo memo2;
    static Memo memo3;
    static Memo memo4;
    static MemoListDto memoListDto1;
    static MemoListDto memoListDto2;
    static MemoListDto memoListDto3;
    static MemoListDto memoListDto4;

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
                .createdDate(LocalDateTime.now().minusDays(1))
                .likes(0L)
                .isTemporary(false)
                .memoTags(List.of("Java", "JPA"))
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
                .likes(10000L)
                .isTemporary(false)
                .memoTags(List.of("Java", "Spring", "JDK"))
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
                .memoTags(List.of("Java", "Spring-Security", "JWT"))
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
                .memoTags(List.of("JSP"))
                .build();
        memoListDto1 = new MemoListDto(memo1);
        memoListDto2 = new MemoListDto(memo2);
        memoListDto3 = new MemoListDto(memo3);
        memoListDto4 = new MemoListDto(memo4);
    }


    @Nested()
    @DisplayName("메모 조회하기")
    class MemoRead {

        @Test
        @DisplayName("메인 페이지 메모 조회")
        void getMemosForMainPage() {
            when(memoRepository.findAllOrderById())
                    .thenReturn(List.of(memo3, memo2, memo1));
            when(memoRepository.findAllByDaysOrderByLikes(ArgumentMatchers.any()))
                    .thenReturn(List.of(memo2, memo3, memo1));
            when(memoRepository.findAllByDaysOrderByLikes(ArgumentMatchers.eq(1)))
                    .thenReturn(List.of(memo2, memo3));

            List<MemoListDto> memosForMainPageOrderByDays = memoService.getMemosForMainPage(DateType.WEEK, OrderType.NEW, 2);
            List<MemoListDto> memosForMainPageOrderByLikesByMonth = memoService.getMemosForMainPage(DateType.MONTH, OrderType.HOT, 2);
            List<MemoListDto> memosForMainPageOrderByLikesByDay = memoService.getMemosForMainPage(DateType.DAY, OrderType.HOT, 2);


            assertThat(memosForMainPageOrderByDays).containsExactly(memoListDto3, memoListDto2, memoListDto1);
            assertThat(memosForMainPageOrderByLikesByMonth).containsExactly(memoListDto2, memoListDto3, memoListDto1);
            assertThat(memosForMainPageOrderByLikesByDay).containsExactly(memoListDto2, memoListDto3);
        }


        @Nested
        @DisplayName("임시 메모 조회")
        class getDraftMemos {
            SecurityContextUtils securityContextUtils = Mockito.mock(SecurityContextUtils.class, CALLS_REAL_METHODS);;
            AuthUtils authUtils;

            @BeforeAll
            void init() {
                securityContextUtils = Mockito.mock(SecurityContextUtils.class, CALLS_REAL_METHODS);
                authUtils = mock(AuthUtils.class, CALLS_REAL_METHODS);
            }

//            @Test
//            @DisplayName("로그인한 유저 케이스")
//            void getDraftsWithLogin() {
//                when(securityContextUtils.)
//            }
        }
    }
}
