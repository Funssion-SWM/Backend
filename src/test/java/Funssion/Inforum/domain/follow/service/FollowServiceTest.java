package Funssion.Inforum.domain.follow.service;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.follow.domain.Follow;
import Funssion.Inforum.domain.follow.repository.FollowRepository;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    FollowRepository followRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    FollowService followService;

    MockedStatic<SecurityContextUtils> securityContextUtilsMockedStatic;

    Long userId1 = 1L;
    Long userId2 = 2L;

    Follow follow = Follow.builder()
            .id(1L)
            .userId(userId1)
            .followedUserId(userId2)
            .created(LocalDateTime.now())
            .build();

    @BeforeEach
    void init() {
        securityContextUtilsMockedStatic = Mockito.mockStatic(SecurityContextUtils.class);
    }

    @AfterEach
    void destroy() {
        securityContextUtilsMockedStatic.close();
    }

    @Nested
    @DisplayName("팔로우 하기")
    class follow {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            given(SecurityContextUtils.getAuthorizedUserId())
                    .willReturn(userId1);

            followService.follow(userId2);
        }

        @Test
        @DisplayName("이미 팔로우 한 유저를 다시 팔로우 하는 케이스")
        void followAlreadyFollowed() {
            given(SecurityContextUtils.getAuthorizedUserId())
                    .willReturn(userId1);
            given(followRepository.findByUserIdAndFollowId(any(), any()))
                    .willReturn(Optional.of(follow));

            Assertions.assertThatThrownBy(() -> followService.follow(userId2))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    @DisplayName("팔로우 취소하기")
    class unfollow {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            given(SecurityContextUtils.getAuthorizedUserId())
                    .willReturn(userId1);
            given(followRepository.findByUserIdAndFollowId(any(), any()))
                    .willReturn(Optional.of(follow));

            followService.unfollow(userId2);
        }

        @Test
        @DisplayName("팔로우를 아직 하지 않고 팔로우를 취소하는 케이스")
        void unfollowWithoutFollowing() {
            given(SecurityContextUtils.getAuthorizedUserId())
                    .willReturn(userId1);

            Assertions.assertThatThrownBy(() -> followService.unfollow(userId2))
                    .isInstanceOf(BadRequestException.class);
        }
    }
}