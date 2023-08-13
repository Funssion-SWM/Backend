package Funssion.Inforum.domain.like.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.like.domain.Like;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.like.exception.LikeNotFoundException;
import Funssion.Inforum.domain.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemoRepository memoRepository;

    public LikeResponseDto getLikeInfo(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();

        try {
            likeRepository.findByUserIdAndPostInfo(userId, postType, postId);
            return new LikeResponseDto(true, memoRepository.findById(postId).getLikes());

        } catch (LikeNotFoundException e) {
            return new LikeResponseDto(false, memoRepository.findById(postId).getLikes());
        }
    }

    @Transactional
    public void likePost(PostType postType, Long postId) {

        updateLikesInPost(postType, postId, Sign.PLUS);

        Long userId = SecurityContextUtils.getUserId();

        likeRepository.save(new Like(userId, postType, postId));
    }

    @Transactional
    public void unlikePost(PostType postType, Long postId) {

        updateLikesInPost(postType, postId, Sign.MINUS);

        Long userId = SecurityContextUtils.getUserId();

        likeRepository.delete(userId, postType, postId);
    }


    private void updateLikesInPost(PostType postType, Long postId, Sign sign) {
        switch (postType) {
            case MEMO -> {
                Memo memo = memoRepository.findById(postId);
                memo.updateLikes(sign);
                memoRepository.update(memo, postId);
            }
            case QNA, BLOG -> throw new BadRequestException("not yet implement");
            default -> throw new BadRequestException("undefined post type");
        }
    }
}
