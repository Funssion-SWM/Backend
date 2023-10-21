package Funssion.Inforum.domain.mypage.service;

import Funssion.Inforum.domain.mypage.dto.MyRankScoreDto;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.memo.repository.MemoRepository;
import Funssion.Inforum.domain.score.Rank;
import Funssion.Inforum.domain.score.dto.ScoreRank;
import Funssion.Inforum.domain.score.repository.ScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyServiceTest {

    @Mock MyRepository myRepository;
    @Mock MemoRepository memoRepository;
    @Mock ScoreRepository scoreRepository;
    @InjectMocks MyService myService;

    @Test
    void getUserInfo() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void getMyMemos() {
    }

    @Test
    void getMyLikedMemos() {
    }

    @Test
    void getMyDraftMemos() {
    }

    @Test
    void getRankAndScoreDto(){
        Long userId = 10L;
        Long gold_5_score = 1500L;
        when(scoreRepository.getScoreAndRank(userId)).thenReturn(new ScoreRank(gold_5_score,Rank.GOLD_5,0L));
        assertThat(myService.getRankAndScoreOf(userId)).isEqualTo(
                MyRankScoreDto.builder()
                        .rankMaxScore(Rank.GOLD_5.getMax())
                        .myScore(gold_5_score)
                        .rankInterval(Rank.GOLD_5.getInterval())
                        .myRank(Rank.GOLD_5.toString())
                        .build());

    }
}