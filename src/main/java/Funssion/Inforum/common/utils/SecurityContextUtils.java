package Funssion.Inforum.common.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SecurityContextUtils {

    public static Integer getUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == "anonymousUser") {
            return 0;
        }
        return Integer.valueOf(userId);
    }
}
