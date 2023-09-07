package Funssion.Inforum.common.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SecurityContextUtils {
    public static final Long ANONYMOUS_USER_ID = 0L;

    public static final Long getUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals("anonymousUser")) return ANONYMOUS_USER_ID;
        return Long.valueOf(userId);
    }
}
