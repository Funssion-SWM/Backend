package Funssion.Inforum.domain.mypage.service;

import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.domain.member.repository.MemberRepository;
import Funssion.Inforum.domain.mypage.dto.MyRankScoreDto;
import Funssion.Inforum.domain.mypage.dto.MyRecordNumDto;
import Funssion.Inforum.domain.mypage.dto.MyUserInfoDto;
import Funssion.Inforum.domain.mypage.dto.addPercentageOfScoreDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.repository.QuestionRepository;
import Funssion.Inforum.domain.post.repository.PostRepository;
import Funssion.Inforum.domain.post.series.dto.response.SeriesListDto;
import Funssion.Inforum.domain.post.series.repository.SeriesRepository;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyService {

    private final MyRepository myRepository;
    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final SeriesRepository seriesRepository;
    private final ScoreRepository scoreRepository;
    private final PostRepository postRepository;
    public MyUserInfoDto getUserInfo(Long userId) {
        return MyUserInfoDto.builder()
                .userName(memberRepository.findNameById(userId))
                .build();
    }

    public List<MyRecordNumDto> getHistory(Long userId, Integer year, Integer month) {
        return myRepository.findMonthlyHistoryByUserId(userId, year, month)
                .stream()
                .map(MyRecordNumDto::new)
                .toList();
    }

    public List<MemoListDto> getMyMemos(Long userId) {
        return memoRepository.findAllByUserIdOrderById(userId).stream()
                .map(MemoListDto::new)
                .toList();
    }

    public List<MemoListDto> getMyLikedMemos(Long userId) {
        return memoRepository.findAllLikedMemosByUserId(userId)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }

    public List<MemoListDto> getMyDraftMemos(Long userId) {
        return memoRepository.findAllDraftMemosByUserId(userId)
                .stream()
                .map(MemoListDto::new)
                .toList();
    }

    public List<Question> getMyQuestions(Long userId) {
        return questionRepository.getMyQuestions(userId, OrderType.NEW);
    }

    public List<Question> getMyLikedQuestions(Long userId) {
        return questionRepository.getMyLikedQuestions(userId);
    }

    public List<Question> getQuestionsOfMyAnswer(Long userId){
        return questionRepository.getQuestionsOfMyAnswer(userId);
    }

    public List<Question> getQuestionsOfMyLikedAnswer(Long userId) {
        return questionRepository.getQuestionsOfMyLikedAnswer(userId);
    }

    public List<SeriesListDto> getMySeries(Long userId, Long pageNum, Long resultCntPerPage) {
        return seriesRepository.findAllBy(userId, pageNum, resultCntPerPage).stream()
                .map(series -> {
                    SeriesListDto seriesListDto = SeriesListDto.valueOf(series);
                    seriesListDto.setTopThreeColors(memoRepository.findTop3ColorsBySeriesId(series.getId()));
                    return seriesListDto;
                }).toList();
    }

    public List<SeriesListDto> getMyLikedSeries(Long userId, Long pageNum, Long resultCntPerPage) {
        return seriesRepository.findLikedBy(userId, pageNum, resultCntPerPage).stream()
                .map(series -> {
                    SeriesListDto seriesListDto = SeriesListDto.valueOf(series);
                    seriesListDto.setTopThreeColors(memoRepository.findTop3ColorsBySeriesId(series.getId()));
                    return seriesListDto;
                }).toList();
    }

    public MyRankScoreDto getRankAndScoreOf(Long userId) {
        Long userScore = scoreRepository.getScoreAndRank(userId).getScore();
        Rank rankByScore = Rank.getRankByScore(userScore);
        return MyRankScoreDto.builder()
                .myRank(rankByScore.toString())
                .myScore(userScore)
                .rankMaxScore(rankByScore.getMax())
                .rankInterval(rankByScore.getInterval())
                .build();
    }
    public addPercentageOfScoreDto getActivityStatsOf(Long userId){
        return new addPercentageOfScoreDto(postRepository.getAllPostScoreAndCount(userId));

    }


}
