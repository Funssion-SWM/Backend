package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostType {
    MEMO("memo"),
    QUESTION("question"),
    ANSWER("answer"),
    COMMENT("comment"),
    RECOMMENT("recomment");

    private final String value;
    public static PostType of(String postType) {
        try {
            return PostType.valueOf(postType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid post type", e);
        } catch (NullPointerException e) {
            return null;
        }
    }
}


