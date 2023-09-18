package Funssion.Inforum.domain.post.like.service;

import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock LikeRepository likeRepository;
    @Mock MemoRepository memoRepository;
    @InjectMocks LikeService likeService;

    MockedStatic<SecurityContextUtils> mockSecurityUtils;

    Long userID1 = 1L;

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
        @DisplayName("요청자가 좋아요를 누른 경우")
        void getLikeInfoWithLikes() {
            given(SecurityContextUtils.getUserId())
                    .willReturn(userID1);
        }
    }

    @Test
    void likePost() {
    }

    @Test
    void unlikePost() {
    }
}