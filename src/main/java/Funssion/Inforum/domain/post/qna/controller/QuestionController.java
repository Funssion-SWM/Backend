package Funssion.Inforum.domain.post.qna.controller;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.domain.post.qna.dto.response.UploadedQuestionDto;
import Funssion.Inforum.domain.post.qna.dto.request.QuestionSaveDto;
import Funssion.Inforum.domain.post.qna.service.QuestionService;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadedQuestionDto createQuestion(@RequestBody @Validated QuestionSaveDto questionSaveDto){
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        return questionService.createQuestion(questionSaveDto,authorId);
    }

//    private void checkAuthorization(CRUDType crudType, Long commentId, boolean isReComment) {
//        Long userId = AuthUtils.getUserId(crudType);
//        if (!userId.equals(commentService.getAuthorIdOfComment(commentId,isReComment))) {
//            throw new UnAuthorizedException("Permission denied to "+crudType.toString());
//        }
//    }
}
