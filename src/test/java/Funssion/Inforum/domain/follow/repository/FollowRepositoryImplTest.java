package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.follow.domain.Follow;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class FollowRepositoryImplTest {

    @Autowired
    FollowRepository repository;

    Long userId1 = 1L;
    Long userId2 = 2L;

    Follow follow = Follow.builder()
            .userId(userId1)
            .followedUserId(userId2)
            .build();

    @BeforeEach
    void init() {
        repository.save(follow);
    }

    @Test
    @DisplayName("팔로우 정보 조회하기")
    void findByUserIdAndFollowId() {
        assertThat(repository.findByUserIdAndFollowedUserId(follow.getUserId(), follow.getFollowedUserId()))
                .isPresent()
                .isEqualTo(Optional.of(follow));
    }

    @Nested
    @DisplayName("팔로우 정보 삭제하기")
    class delete {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            repository.delete(follow.getUserId(), follow.getFollowedUserId());

            assertThat(repository.findByUserIdAndFollowedUserId(follow.getUserId(), follow.getFollowedUserId()))
                    .isNotPresent();

            assertThatThrownBy(() -> repository.delete(follow.getUserId(), follow.getFollowedUserId()))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("예외 케이스")
        void fail() {
            assertThatThrownBy(() -> repository.delete(follow.getFollowedUserId(), follow.getUserId()))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}