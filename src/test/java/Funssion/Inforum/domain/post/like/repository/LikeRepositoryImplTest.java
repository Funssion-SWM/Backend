package Funssion.Inforum.domain.post.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class LikeRepositoryImplTest {

    @Autowired
    private LikeRepository likeRepository;

    private final Like like1 = new Like(0L,9999L, PostType.BLOG, 99999L, new Timestamp(0));
    private final Like like2 = new Like(0L,9998L, PostType.MEMO, 99998L, new Timestamp(0));
    private final Like like3 = new Like(0L,9998L, PostType.MEMO, 99997L, new Timestamp(0));

    @Test
    @DisplayName("좋아요 정보 저장")
    void create() {
        Like saved = likeRepository.create(like1);

        assertThat(saved).isEqualTo(likeRepository.findById(saved.getId()));
    }

    @Test
    @DisplayName("좋아요 정보 아이디로 불러오기")
    void findById() {
        Like saved1 = likeRepository.create(like1);

        Like foundById = likeRepository.findById(saved1.getId());

        assertThat(foundById).isEqualTo(saved1);
        assertThatThrownBy(() -> likeRepository.findById(like2.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("좋아요 정보 아이디랑 게시물 정보로 불러오기")
    void findByUserIdAndPostInfo() {
        Like saved = likeRepository.create(like1);
        Like found = likeRepository.findByUserIdAndPostInfo(saved.getUserId(), saved.getPostType(), saved.getPostId()).get();

        assertThat(saved).isEqualTo(found);
        assertThat(likeRepository.findByUserIdAndPostInfo(like2.getUserId(), like2.getPostType(), like2.getPostId()).isPresent())
                .isFalse();
    }

    @Test
    @DisplayName("좋아요 정보 아이디랑 게시물 타입으로 불러오기")
    void findAllByUserIdAndPostType() {
        Like saved2 = likeRepository.create(like2);
        Like saved3 = likeRepository.create(like3);
        List<Like> foundAllByUserIdAndPostType = likeRepository.findAllByUserIdAndPostType(saved2.getUserId(), saved2.getPostType());

        assertThat(foundAllByUserIdAndPostType).contains(saved2, saved3);
    }

    @Test
    @DisplayName("좋아요 정보 삭제하기")
    void delete() {
        Like saved = likeRepository.create(like1);
        likeRepository.delete(saved.getUserId(), saved.getPostType(), saved.getPostId());

        assertThatThrownBy(() -> likeRepository.findById(saved.getId()))
                .isInstanceOf(NotFoundException.class);

        assertThatThrownBy(() -> likeRepository.delete(like2.getUserId(), like2.getPostType(), like2.getPostId()))
                .isInstanceOf(NotFoundException.class);
    }
}