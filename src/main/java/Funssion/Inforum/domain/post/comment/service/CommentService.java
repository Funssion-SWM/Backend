package Funssion.Inforum.domain.post.comment.service;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.domain.member.entity.MemberProfileEntity;
import Funssion.Inforum.domain.mypage.repository.MyRepository;
import Funssion.Inforum.domain.post.comment.domain.Comment;
import Funssion.Inforum.domain.post.comment.dto.request.CommentSaveDto;
import Funssion.Inforum.domain.post.comment.dto.request.CommentUpdateDto;
import Funssion.Inforum.domain.post.comment.dto.response.CommentListDto;
import Funssion.Inforum.domain.post.comment.dto.response.IsSuccessResponseDto;
import Funssion.Inforum.domain.post.comment.repository.CommentRepository;
import Funssion.Inforum.domain.post.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MyRepository myRepository;

    public IsSuccessResponseDto createComment(CommentSaveDto commentSaveDto){
        Long authorId = AuthUtils.getUserId(CRUDType.CREATE);
        MemberProfileEntity authorProfile = myRepository.findProfileByUserId(authorId);
        commentRepository.createComment(new Comment(
            authorId,authorProfile, Date.valueOf(now()),null,commentSaveDto)
        );
        return new IsSuccessResponseDto(true,"댓글 저장에 성공하였습니다.");
    }

    public IsSuccessResponseDto updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        return commentRepository.updateComment(commentUpdateDto,commentId);
    }

    public IsSuccessResponseDto deleteComment(Long commentId) {
        return commentRepository.deleteComment(commentId);
    }

    public List<CommentListDto> getCommentsAtPost(PostType postType, Long postId){
        return commentRepository.getCommentsAtPost(postType, postId);
    }
}
