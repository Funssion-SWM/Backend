package Funssion.Inforum.domain.post.qna.controller;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.AnswerDto;
import Funssion.Inforum.domain.post.qna.service.AnswerService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/answers")
public class AnswerController {
    private final AnswerService answerService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IsSuccessResponseDto createAnswerOfQuestion(@RequestBody @Validated AnswerSaveDto answerSaveDto, @RequestParam Long questionId){
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        answerService.createAnswerOfQuestion(answerSaveDto,questionId,authorId);
        return new IsSuccessResponseDto(true,"성공적으로 답변이 등록되었습니다.");
    }
    @GetMapping
    public List<AnswerDto> getAnswersOfQuestion(@RequestParam Long questionId){
        Long loginId = AuthUtils.getUserId(CRUDType.READ);
        List<Answer> answers = answerService.getAnswersOfQuestion(questionId);
        return answers.stream().map(answer->new AnswerDto(answer,loginId)).toList();
    }
}
