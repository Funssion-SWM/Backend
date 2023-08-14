package Funssion.Inforum.domain.like.controller;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.exception.notfound.UrlNotFoundException;
import Funssion.Inforum.domain.like.dto.response.LikeResponseDto;
import Funssion.Inforum.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{postType}/{postId}/like")
    public LikeResponseDto getLikeInfo(@PathVariable String postType, @PathVariable Long postId) {
        return likeService.getLikeInfo(parseToEnumType(postType), postId);
    }

    @PostMapping("/{postType}/{postId}/like")
    public void like(@PathVariable String postType, @PathVariable Long postId) {
        likeService.likePost(parseToEnumType(postType), postId);
    }

    @PostMapping("/{postType}/{postId}/unlike")
    public void unlike(@PathVariable String postType, @PathVariable Long postId) {
        likeService.unlikePost(parseToEnumType(postType), postId);
    }

    private PostType parseToEnumType(String postType) {
        try {
            return PostType.valueOf(postType.substring(0, postType.length() - 1).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UrlNotFoundException();
        }
    }
}
