package Funssion.Inforum.domain.post.like.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemoRepository memoRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public LikeResponseDto getLikeInfo(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();

        return likeRepository.findByUserIdAndPostInfo(userId, postType, postId)
                .map(like -> new LikeResponseDto(true, getLikesOfPost(postType, postId)))
                .orElse(new LikeResponseDto(false, getLikesOfPost(postType, postId)));
    }

    private Long getLikesOfPost(PostType postType, Long postId){
        switch (postType){
            case MEMO -> {
                return memoRepository.findById(postId).getLikes();
            }
            case QUESTION -> {
                return questionRepository.getOneQuestion(postId).getLikes();
            }
            case ANSWER -> {
                return answerRepository.getAnswerById(postId).getLikes();
            }
            default ->{
                throw new BadRequestException("유효하지 않은 postType 입니다.");
            }
        }
    }

    @Transactional
    public void likePost(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();

        likeRepository.findByUserIdAndPostInfo(userId, postType, postId)
                .ifPresent(like -> {
                    throw new BadRequestException("you have already liked this post");
                });

        updateLikesInPost(postType, postId, Sign.PLUS);
        likeRepository.create(new Like(userId, postType, postId));
    }

    @Transactional
    public void unlikePost(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();

        likeRepository.findByUserIdAndPostInfo(userId, postType, postId)
                .orElseThrow(() -> new BadRequestException("you haven't liked this post"));

        updateLikesInPost(postType, postId, Sign.MINUS);
        likeRepository.delete(userId, postType, postId);
    }

    @Transactional
    public void dislikePost(PostType postType, Long postId){
        Long userId = SecurityContextUtils.getUserId();
    }



    private void updateLikesInPost(PostType postType, Long postId, Sign sign) {
        switch (postType) {
            case MEMO -> {
                Memo memo = memoRepository.findById(postId);
                Long updatedLikes = memo.updateLikes(sign);
                memoRepository.updateLikesInMemo(updatedLikes, postId);
            }
            case QUESTION ->{
                Question question = questionRepository.getOneQuestion(postId);
                Long updatedLikes = question.updateLikes(sign);
                questionRepository.updateLikesInQuestion(updatedLikes,postId);
            }
            case ANSWER ->{
                Answer answer = answerRepository.getAnswerById(postId);
                Long updatedLikes = answer.updateLikes(sign);
                answerRepository.updateLikesInAnswer(updatedLikes,postId);
            }
            case BLOG -> throw new BadRequestException("not yet implement");
            default -> throw new BadRequestException("undefined post type");
        }
    }
}
