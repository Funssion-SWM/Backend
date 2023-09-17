package Funssion.Inforum.domain.post.like.controller;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.notfound.UrlNotFoundException;
import Funssion.Inforum.domain.post.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.post.like.service.LikeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{postType}/{postId}/like")
    public LikeResponseDto getLikeInfo(
            @PathVariable PostType postType,
            @PathVariable @Min(1) Long postId
    ) {
        return likeService.getLikeInfo(postType, postId);
    }

    @PostMapping("/{postType}/{postId}/like")
    public void like(@PathVariable PostType postType, @PathVariable @Min(1) Long postId) {
        likeService.likePost(postType, postId);
    }

    @PostMapping("/{postType}/{postId}/unlike")
    public void unlike(@PathVariable PostType postType, @PathVariable @Min(1) Long postId) {
        likeService.unlikePost(postType, postId);
    }
}
