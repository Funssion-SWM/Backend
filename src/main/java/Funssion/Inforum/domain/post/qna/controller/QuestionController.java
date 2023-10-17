package Funssion.Inforum.domain.post.qna.controller;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.OrderType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.memo.dto.response.MemoListDto;
import Funssion.Inforum.domain.post.qna.Constant;
import Funssion.Inforum.domain.post.qna.domain.Question;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.QuestionDto;
import Funssion.Inforum.domain.post.qna.service.QuestionService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.s3.dto.response.ImageDto;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        Long loginId = AuthUtils.getUserId(CRUDType.READ);
        return questionService.getQuestions(loginId,orderBy);
    }

    @GetMapping("/{id}")
    public QuestionDto getQuestion(@PathVariable Long id){
        Long loginId = AuthUtils.getUserId(CRUDType.READ);
        return questionService.getOneQuestion(loginId,id);
    }

    @GetMapping("/memo")
    public List<Question> getQuestionsOfMemo(@RequestParam Long memoId){
        Long loginId = AuthUtils.getUserId(CRUDType.READ);
        return questionService.getQuestionsOfMemo(loginId,memoId);
    }

    @GetMapping("/search")
    public List<Question> getSearchedQuestions(
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false, defaultValue =  SecurityContextUtils.ANONYMOUS_USER_ID_STRING) @Min(0) Long userId,
            @RequestParam OrderType orderBy,
            @RequestParam Boolean isTag
    ) {
        if (Objects.isNull(searchString) || searchString.isBlank()) {
            return new ArrayList<>();
        }

        return questionService.searchQuestionsBy(searchString, userId, orderBy, isTag);
    }

    @PostMapping("/image")
    public ImageDto saveImageAndGetImageURL(
            @RequestPart MultipartFile image
    ) {
        return questionService.saveImageAndGetImageURL(image);
    }

    private Long checkAuthorization(CRUDType crudType,Long questionId) {
        Long authorId = AuthUtils.getUserId(crudType);
        if (!authorId.equals(questionService.getAuthorId(questionId))) {
            throw new UnAuthorizedException("Permission denied to "+crudType.toString());
        }
        return authorId;
    }
}
