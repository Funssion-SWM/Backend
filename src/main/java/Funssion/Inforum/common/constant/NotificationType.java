package Funssion.Inforum.common.constant;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {
    NEW_COMMENT("게시물에 새로운 댓글이 있습니다."),
    NEW_ANSWER("질문에 대한 새로운 답변이 있습니다."),
    NEW_QUESTION("메모에 대한 새로운 질문이 있습니다."),
    NEW_FOLLOWER("새로운 유저가 나를 팔로우했습니다."),
    NEW_POST_FOLLOWED("팔로잉한 유저가 새 컨텐츠를 업로드했습니다."),
    NEW_ACCEPTED("답변이 채택되었습니다."),
    NEW_EMPLOYER("채용자가 나를 관심 유저로 추가했습니다."),
    NEW_INTERVIEW("새로운 미니면접 요청이 있습니다."),
    NEW_INTERVIEW_COMPLETE("요청한 미니면접 진행이 완료되었습니다."),
    NEW_POST_LIKED_EMPLOYEE("관심 등록한 지원자의 새 게시물이 있습니다.");

    public static NotificationType of(String string) {
        try {
            return NotificationType.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid notification type", e);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private final String message;
}
