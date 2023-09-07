package Funssion.Inforum.common.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SecurityContextUtils {

    public static Long getUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals("anonymousUser")) return 0L;
        return Long.valueOf(userId);
    }
}
