package Funssion.Inforum.common.utils;

import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SecurityContextUtils {
    public static final Long ANONYMOUS_USER_ID = 0L;
    public static final String ANONYMOUS_USER_ID_STRING = "0";

    public static Long getUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals("anonymousUser")) return ANONYMOUS_USER_ID;
        return Long.valueOf(userId);
    }

    public static Long getAuthorizedUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals("anonymousUser")) throw new UnAuthorizedException("인증되지 않은 사용자입니다.");
        return Long.valueOf(userId);
    }
}
