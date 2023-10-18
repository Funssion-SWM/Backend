package Funssion.Inforum.domain.score;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;
    @GetMapping("/{id}")
    public String getRankOfUser(@PathVariable Long id){
        return Rank.getRankByScore(scoreService.getScore(id)).toString();
    }
}
