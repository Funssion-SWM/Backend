package Funssion.Inforum.domain.score.contoller;

import Funssion.Inforum.domain.score.dto.ScoreRank;
import Funssion.Inforum.domain.score.dto.UserInfoWithScoreRank;
import Funssion.Inforum.domain.score.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;
    @GetMapping("/{id}")
    public ScoreRank getRankOfUser(@PathVariable Long id){
        return scoreService.getScoreAndRank(id);
    }

    @GetMapping("/rank")
    public List<UserInfoWithScoreRank> getTopTenUsers(){
        return scoreService.getTopTenUsers();
    }
}
