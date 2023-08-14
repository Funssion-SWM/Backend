package Funssion.Inforum.domain.like.repository;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.domain.like.domain.Like;
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
    void save() {
        Like saved = likeRepository.save(like1);

        assertThat(saved).isEqualTo(likeRepository.findById(saved.getId()));
    }

    @Test
    void read() {
        Like saved1 = likeRepository.save(like1);
        Like saved2 = likeRepository.save(like2);
        Like saved3 = likeRepository.save(like3);

        Like foundById = likeRepository.findById(saved1.getId());

        assertThat(foundById).isEqualTo(saved1);
        assertThatThrownBy(() -> likeRepository.findById(like2.getId()))
                .isInstanceOf(NotFoundException.class);

        Like foundByUserIdAndPostInfo = likeRepository.findByUserIdAndPostInfo(saved2.getUserId(), saved2.getPostType(), saved2.getPostId());

        assertThat(foundByUserIdAndPostInfo).isEqualTo(saved2);

        List<Like> foundAllByUserIdAndPostType = likeRepository.findAllByUserIdAndPostType(saved2.getUserId(), saved2.getPostType());

        assertThat(foundAllByUserIdAndPostType).contains(saved2, saved3);
    }

    @Test
    void findByUserIdAndPostInfo() {
        Like saved = likeRepository.save(like1);
        Like found = likeRepository.findByUserIdAndPostInfo(saved.getUserId(), saved.getPostType(), saved.getPostId());

        assertThat(saved).isEqualTo(found);

        assertThatThrownBy(() -> likeRepository.findByUserIdAndPostInfo(like2.getUserId(), like2.getPostType(), like2.getPostId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete() {
        Like saved = likeRepository.save(like1);
        likeRepository.delete(saved.getUserId(), saved.getPostType(), saved.getPostId());

        assertThatThrownBy(() -> likeRepository.findById(saved.getId()))
                .isInstanceOf(NotFoundException.class);

        assertThatThrownBy(() -> likeRepository.delete(like2.getUserId(), like2.getPostType(), like2.getPostId()))
                .isInstanceOf(NotFoundException.class);
    }
}