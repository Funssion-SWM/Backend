package Funssion.Inforum.domain.post.like.service;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.repository.PostRepository;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static Funssion.Inforum.common.constant.PostType.MEMO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock LikeRepository likeRepository;
    @Mock MemoRepository memoRepository;
    @Mock PostRepository postRepository;
    @Mock ScoreRepository scoreRepository;
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
            .rank(Rank.BRONZE_5.toString())
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
            given(likeRepository.findByUserIdAndPostInfo(SecurityContextUtils.ANONYMOUS_USER_ID, MEMO, memoID1))
                    .willReturn(Optional.empty());
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
            given(likeRepository.findByUserIdAndPostInfo(userID1, MEMO, memoID1))
                    .willReturn(Optional.of(like1));
            given(memoRepository.findById(memoID1))
                    .willReturn(memo1);

            LikeResponseDto likeInfo = likeService.getLikeInfo(MEMO, memoID1);

            assertThat(likeInfo.getLikes()).isEqualTo(memo1.getLikes());
            assertThat(likeInfo.getIsLike()).isTrue();
        }

        @Test
        @DisplayName("요청자가 좋아요를 누르지 않은 경우")
        void getLikeInfoWithoutLikes() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(userID1);
            given(likeRepository.findByUserIdAndPostInfo(userID1, MEMO, memoID1))
                    .willReturn(Optional.empty());
            given(memoRepository.findById(memoID1))
                    .willReturn(memo1);

            LikeResponseDto likeInfo = likeService.getLikeInfo(MEMO, memoID1);

            assertThat(likeInfo.getIsLike()).isFalse();
            assertThat(likeInfo.getLikes()).isEqualTo(memo1.getLikes());
        }
    }

    @Nested
    @DisplayName("게시물 좋아요하기")
    class likePost {
//        @Test
//        @DisplayName("정상 케이스")
//        void success() {
//            given(SecurityContextUtils.getUserId())
//                    .willReturn(userID1);
//            given(likeRepository.findByUserIdAndPostInfo(any(), any(), any()))
//                    .willReturn(Optional.empty());
//            given(memoRepository.findById(memoID1))
//                    .willReturn(memo1);
//
//            given(scoreRepository.getRank(memo1.getAuthorId())).willReturn(Rank.BRONZE_4.toString());
//            likeService.likePost(MEMO, memoID1);
//        }

        @Test
        @DisplayName("좋아요를 이미 누르고 다시 좋아요하는 케이스")
        void likePostAlreadyLiked() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(userID1);
            given(likeRepository.findByUserIdAndPostInfo(any(), any(), any()))
                    .willReturn(Optional.of(like1));

            assertThatThrownBy(() ->likeService.likePost(MEMO, memoID1))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    @DisplayName("게시물 좋아요 취소하기")
    class unlikePost {
        @Test
        @DisplayName("정상 케이스")
        void success() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(userID1);
            given(likeRepository.findByUserIdAndPostInfo(any(), any(), any()))
                    .willReturn(Optional.of(like1));


            likeService.unlikePost(MEMO, memoID1);
        }

        @Test
        @DisplayName("좋아요를 하지 않고 좋아요를 취소하는 케이스")
        void unlikePostLikedNotYet() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(userID1);
            given(likeRepository.findByUserIdAndPostInfo(any(), any(), any()))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.unlikePost(MEMO, memoID1))
                    .isInstanceOf(BadRequestException.class);
        }
    }
}