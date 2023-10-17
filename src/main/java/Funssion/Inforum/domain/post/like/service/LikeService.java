package Funssion.Inforum.domain.post.like.service;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.constant.ScoreType;
import Funssion.Inforum.common.constant.Sign;
import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.like.domain.DisLike;
import Funssion.Inforum.domain.post.like.domain.Like;
import Funssion.Inforum.domain.post.like.dto.response.DisLikeResponseDto;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.like.repository.LikeRepository;
import Funssion.Inforum.domain.post.memo.domain.Memo;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.AnswerRepository;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.post.repository.PostRepository;
import Funssion.Inforum.domain.post.series.repository.SeriesRepository;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Funssion.Inforum.domain.score.Score.calculateAddingScore;
import static Funssion.Inforum.domain.score.Score.calculateDailyScore;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    public static final int LIMIT_LIKES_OF_SCORE = 50;
    private final LikeRepository likeRepository;
    private final MemoRepository memoRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ScoreRepository scoreRepository;
    private final PostRepository postRepository;
    private final SeriesRepository seriesRepository;

    @Transactional(readOnly = true)
    public LikeResponseDto getLikeInfo(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();

        return likeRepository.findByUserIdAndPostInfo(userId, postType, postId)
                .map(like -> new LikeResponseDto(true, getLikesOfPost(postType, postId)))
                .orElse(new LikeResponseDto(false, getLikesOfPost(postType, postId)));
    }
    @Transactional(readOnly = true)
    public DisLikeResponseDto getDisLikeInfo(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();

        return likeRepository.findByUserIdAndPostInfoOfDisLike(userId, postType, postId)
                .map(like -> new DisLikeResponseDto(true, getDisLikesOfPost(postType, postId)))
                .orElse(new DisLikeResponseDto(false, getDisLikesOfPost(postType, postId)));
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
            case SERIES -> {
                return seriesRepository.findById(postId)
                        .map(series -> series.getLikes())
                        .orElseThrow(() -> new NotFoundException("해당 게시물을 찾을 수 없습니다."));
            }
            default ->{
                throw new BadRequestException("유효하지 않은 게시물 타입입니다.");
            }
        }
    }
    private Long getDisLikesOfPost(PostType postType, Long postId){
        switch (postType){
            case ANSWER -> {
                return answerRepository.getAnswerById(postId).getDislikes();
            }
            default -> throw new BadRequestException("해당 타입의 게시글들은 비추천할 수 없습니다.");
        }
    }

    @Transactional
    public void likePost(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();
        likeRepository.findByUserIdAndPostInfoOfDisLike(userId,postType,postId)
                .ifPresent(like ->{
                    throw new BadRequestException("싫어요와 좋아요를 동시에 할 수 없습니다.");
                });

        likeRepository.findByUserIdAndPostInfo(userId, postType, postId)
                .ifPresent(like -> {
                    throw new BadRequestException("이미 좋아요한 게시물입니다.");
                });

        updateUserOfPostScore(userId,postType, postId);
        updateLikesInPost(postType, postId, Sign.PLUS);

        likeRepository.create(new Like(userId, postType, postId));
    }

    private void updateUserOfPostScore(Long likerId,PostType postType, Long postId) {
        Long authorId = postRepository.findAuthorId(postType, postId);
        Long userDailyScore = scoreRepository.getUserDailyScore(authorId);
        // Like의 경우에는 점수를 받는 사람이 행동의 당사자가 아닌, 포스트 작성자 이므로, service를 통해 처리하지 않고 직접 score repository 객체에서 로직을 작성합니다.
        if(likeRepository.howManyLikesInPost(postType,postId) < LIMIT_LIKES_OF_SCORE) {
            Long addedScore = calculateAddingScore(userDailyScore, ScoreType.LIKE);
            Long updateDailyScore = calculateDailyScore(userDailyScore, ScoreType.LIKE);
            Long resultUserScore = scoreRepository.updateUserScoreAtDay(authorId, addedScore, updateDailyScore);
            scoreRepository.saveScoreHistory(likerId,ScoreType.LIKE,addedScore,postId); //DB에는 좋아요를 한 사람의 정보가 좋아요 테이블에 들어갑니다.
            Rank beforeRank = Rank.valueOf(scoreRepository.getRank(authorId));
            if(resultUserScore >= beforeRank.getMax()){
                updateRank(authorId,beforeRank,true);
            }
        }
    }

    private Rank updateRank(Long userId, Rank beforeRank, boolean isLevelUp) {
        List<Rank> ranks = List.of(Rank.values());
        int currentRankIndex = ranks.indexOf(beforeRank);
        int updatedRankIndex = isLevelUp? currentRankIndex + 1: currentRankIndex - 1;
        Rank beUpdateRank = ranks.get(updatedRankIndex);
        return scoreRepository.updateRank(beUpdateRank, userId);
    }

    @Transactional
    public void unlikePost(PostType postType, Long postId) {
        Long userId = SecurityContextUtils.getUserId();

        likeRepository.findByUserIdAndPostInfo(userId, postType, postId)
                .orElseThrow(() -> new BadRequestException("아직 좋아요하지 않은 게시물입니다."));

        updateLikesInPost(postType, postId, Sign.MINUS);
        likeRepository.deleteLike(userId, postType, postId);

        scoreRepository.findScoreHistoryInfoById(userId, ScoreType.LIKE, postId).ifPresent((score)-> {
            scoreRepository.deleteScoreHistory(score);
            // like는 daily score에 제한이 없으므로, 당일날 삭제해도 하루의 시간이 지난 메서드를 사용합니다.
            Long authorId = postRepository.findAuthorId(postType, postId);
            Long resultScore = scoreRepository.updateUserScoreAtOtherDay(authorId, -score.getScore());
            Rank beforeRank = Rank.valueOf(scoreRepository.getRank(authorId));
            if(resultScore < beforeRank.getMax() - beforeRank.getInterval()){
                updateRank(authorId,beforeRank,false);
            }
        });

    }

    @Transactional
    public void dislikePost(PostType postType, Long postId){
        Long userId = SecurityContextUtils.getUserId();

        updateDisLikesInPost(postType, postId, Sign.PLUS);

        likeRepository.findByUserIdAndPostInfo(userId,postType,postId)
                        .ifPresent(like ->{
                            throw new BadRequestException("싫어요와 좋아요를 동시에 할 수 없습니다.");
                        });

        likeRepository.findByUserIdAndPostInfoOfDisLike(userId, postType, postId)
                .ifPresent(dislike -> {
                    throw new BadRequestException("이미 싫어요한 게시물입니다.");
                });

        likeRepository.createDisLike(new DisLike(userId,postType,postId));
    }
    @Transactional
    public void unDislikePost(PostType postType, Long postId){
        Long userId = SecurityContextUtils.getUserId();

        updateDisLikesInPost(postType, postId, Sign.MINUS);

        likeRepository.findByUserIdAndPostInfoOfDisLike(userId, postType, postId)
                .orElseThrow(() -> new BadRequestException("아직 싫어요하지 않은 게시물입니다."));

        likeRepository.deleteDisLike(userId, postType, postId);
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
            case SERIES -> seriesRepository.updateLikes(postId, sign);

            default -> throw new BadRequestException("정의되지 않은 게시물 타입입니다.");
        }
    }
    private void updateDisLikesInPost(PostType postType, Long postId, Sign sign){
        switch(postType){
            case ANSWER -> {
                Answer answer = answerRepository.getAnswerById(postId);
                Long updatedDisLikes = answer.updateDisLikes(sign);
                answerRepository.updateDisLikesInAnswer(updatedDisLikes,postId);
            }
            default -> throw new BadRequestException("해당 타입의 게시글들은 비추천할 수 없습니다.");
        }
    }
}
