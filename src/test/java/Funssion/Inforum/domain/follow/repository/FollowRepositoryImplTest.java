package Funssion.Inforum.domain.follow.repository;

import Funssion.Inforum.domain.follow.domain.Follow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FollowRepositoryImplTest {

    @Autowired
    FollowRepository repository;

    Long userId1 = 1L;
    Long followId1 = 3L;

    @Test
    @DisplayName("팔로우 정보 저장하기")
    void save() {
        Follow follow = Follow.builder()
                .userId(userId1)
                .followId(followId1)
                .build();

        repository.save(follow);
    }
}