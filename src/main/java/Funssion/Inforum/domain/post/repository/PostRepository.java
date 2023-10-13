package Funssion.Inforum.domain.post.repository;

import Funssion.Inforum.common.constant.PostType;

public interface PostRepository {
    Long findAuthorId(PostType postType, Long postId);
}
