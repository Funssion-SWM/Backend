package Funssion.Inforum.domain.post.qna.controller;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.domain.post.qna.domain.Answer;
import Funssion.Inforum.domain.post.qna.dto.request.AnswerSaveDto;
import Funssion.Inforum.domain.post.qna.dto.response.AnswerDto;
import Funssion.Inforum.domain.post.qna.service.AnswerService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import Funssion.Inforum.s3.dto.response.ImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/{answerId}")
    public AnswerDto getAnswerBy(@PathVariable Long answerId){
        Long loginId = AuthUtils.getUserId(CRUDType.READ);
        Answer answer = answerService.getAnswerBy(answerId);
        return new AnswerDto(answer,loginId);
    }

    @PatchMapping("/{answerId}")
    public IsSuccessResponseDto updateAnswer(@RequestBody @Validated AnswerSaveDto answerSaveDto, @PathVariable Long answerId){
        checkAuthorization(CRUDType.UPDATE, answerId);
        answerService.updateAnswer(answerSaveDto, answerId);
        return new IsSuccessResponseDto(true, "성공적으로 답변이 수정되었습니다.");
    }

    @DeleteMapping("/{answerId}")
    public IsSuccessResponseDto deleteAnswer(@PathVariable Long answerId){
        Long loginId = checkAuthorization(CRUDType.DELETE, answerId);
        answerService.deleteAnswer(answerId, loginId);
        return new IsSuccessResponseDto(true, "성공적으로 답변이 삭제되었습니다.");
    }

    @PostMapping("/image")
    public ImageDto saveImageAndGetImageURL(
            @RequestPart MultipartFile image
    ) {
        return answerService.saveImageAndGetImageURL(image);
    }

    private Long checkAuthorization(CRUDType crudType,Long answerId) {
        Long authorId = AuthUtils.getUserId(crudType);
        if (!authorId.equals(answerService.getAuthorId(answerId))) {
            throw new UnAuthorizedException("Permission denied to "+crudType.toString());
        }
        return authorId;
    }
}
