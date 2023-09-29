package Funssion.Inforum.domain.post.qna.controller;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.service.QuestionService;
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
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IsSuccessResponseDto createQuestion(@RequestBody @Validated QuestionSaveDto questionSaveDto,@RequestParam(required = false, defaultValue=Constant.NONE_MEMO_QUESTION) Long memoId){
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        questionService.createQuestion(questionSaveDto, authorId, memoId);
        return new IsSuccessResponseDto(true,"성공적으로 질문이 등록되었습니다.");
    }
    @PutMapping("/{id}")
    public IsSuccessResponseDto updateQuestion(@PathVariable(value ="id") Long questionId,@RequestBody @Validated QuestionSaveDto questionSaveDto){
        Long authorId = checkAuthorization(CRUDType.UPDATE, questionId);
        questionService.updateQuestion(questionSaveDto, questionId, authorId);
        return new IsSuccessResponseDto(true,"성공적으로 질문이 수정되었습니다.");
    }
    @DeleteMapping("/{id}")
    public IsSuccessResponseDto deleteQuestion(@PathVariable(value="id") Long questionId){
        Long authorId = checkAuthorization(CRUDType.DELETE, questionId);
        return questionService.deleteQuestion(questionId,authorId);
    }


    @GetMapping
    public List<Question> getQuestions(@RequestParam (required = false, defaultValue = "NEW") OrderType orderBy){
        return questionService.getQuestions(orderBy);
    }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable Long id){
        return questionService.getQuestion(id);
    }

    @GetMapping("/memo")
    public List<Question> getQuestionsOfMemo(@RequestParam Long memoId){
        return questionService.getQuestionsOfMemo(memoId);
    }

    private Long checkAuthorization(CRUDType crudType,Long questionId) {
        Long authorId = AuthUtils.getUserId(crudType);
        if (!authorId.equals(questionService.getAuthorId(questionId))) {
            throw new UnAuthorizedException("Permission denied to "+crudType.toString());
        }
        return authorId;
    }
}
