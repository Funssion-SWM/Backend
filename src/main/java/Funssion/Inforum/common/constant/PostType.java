package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;

public enum PostType {
    MEMO,
    BLOG,
    QUESTION;

    public static PostType of(String postType) {
        try {
            return PostType.valueOf(postType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid post type", e);
        }
    }
}


