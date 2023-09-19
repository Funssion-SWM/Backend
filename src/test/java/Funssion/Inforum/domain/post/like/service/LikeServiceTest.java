package Funssion.Inforum.domain.post.like.service;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.like.exception.LikeNotFoundException;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static Funssion.Inforum.common.constant.PostType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock LikeRepository likeRepository;
    @Mock MemoRepository memoRepository;
    @InjectMocks LikeService likeService;

    MockedStatic<SecurityContextUtils> mockSecurityUtils;

    Long userID1 = 1L;
    Long likeID1 = 1L;
    Long memoID1 = 1L;
    Memo memo1 = Memo.builder()
            .id(memoID1)
            .title("JPA")
            .text("JPA is JPA")
            .description("JPA is ...")
            .color("yellow")
            .authorId(userID1)
            .authorName("jinu")
            .authorImagePath("jinu-image")
            .createdDate(LocalDateTime.now().minusDays(1))
            .likes(1L)
            .isTemporary(false)
            .memoTags(List.of("Java", "JPA"))
            .build();
    Like like1 = Like.builder()
            .id(likeID1)
            .postId(memoID1)
            .postType(MEMO)
            .userId(userID1)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    @BeforeEach
    void beforeEach() {
        mockSecurityUtils = mockStatic(SecurityContextUtils.class);
    }

    @AfterEach
    void afterEach() {
        mockSecurityUtils.close();
    }

    @Nested
    @DisplayName("좋아요 정보 조회하기")
    class getLikeInfo {

        @Test
        @DisplayName("요청자가 로그인 하지 않은 경우")
        void getLikeInfoWithoutLogin() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(SecurityContextUtils.ANONYMOUS_USER_ID);
            given(likeRepository.findByUserIdAndPostInfo(any(),any(),any()))
                    .willThrow(LikeNotFoundException.class);
            given(memoRepository.findById(memoID1))
                    .willReturn(memo1);

            LikeResponseDto likeInfo = likeService.getLikeInfo(MEMO, memoID1);

            assertThat(likeInfo.getIsLike()).isFalse();
            assertThat(likeInfo.getLikes()).isEqualTo(memo1.getLikes());
        }

        @Test
        @DisplayName("요청자가 좋아요를 누른 경우")
        void getLikeInfoWithLikes() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(userID1);
            given(memoRepository.findById(memoID1))
                    .willReturn(memo1);

            LikeResponseDto likeInfo = likeService.getLikeInfo(MEMO, likeID1);

            assertThat(likeInfo.getLikes()).isEqualTo(memo1.getLikes());
            assertThat(likeInfo.getIsLike()).isTrue();
        }

        @Test
        @DisplayName("요청자가 좋아요를 누르지 않은 경우")
        void getLikeInfoWithoutLikes() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(userID1);
            given(likeRepository.findByUserIdAndPostInfo(any(),any(),any()))
                    .willThrow(LikeNotFoundException.class);
            given(memoRepository.findById(memoID1))
                    .willReturn(memo1);

            LikeResponseDto likeInfo = likeService.getLikeInfo(MEMO, memoID1);

            assertThat(likeInfo.getIsLike()).isFalse();
            assertThat(likeInfo.getLikes()).isEqualTo(memo1.getLikes());
        }
    }

    @Test
    @DisplayName("게시물 좋아요하기")
    void likePost() {
        given(SecurityContextUtils.getUserId())
                .willReturn(userID1);
        given(memoRepository.findById(memoID1))
                .willReturn(memo1);

        likeService.likePost(MEMO, memoID1);
    }

    @Test
    @DisplayName("게시물 좋아요 취소하기")
    void unlikePost() {
        given(SecurityContextUtils.getUserId())
                .willReturn(userID1);
        given(memoRepository.findById(memoID1))
                .willReturn(memo1);

        likeService.unlikePost(MEMO, memoID1);
    }
}