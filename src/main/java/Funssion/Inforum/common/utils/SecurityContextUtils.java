package Funssion.Inforum.common.utils;

import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Objects;

public abstract class SecurityContextUtils {
    public static final Long ANONYMOUS_USER_ID = 0L;
    public static final String ANONYMOUS_USER_ID_STRING = "0";
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";

    public static Long getUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals(ANONYMOUS_USER_NAME)) return ANONYMOUS_USER_ID;
        return Long.valueOf(userId);
    }
    public static String getAuthority(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList().get(0);
        //더 큰 역할이 첫번째 원소에 옵니다.
    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (Objects.isNull(authorities)) throw new UnAuthorizedException("인증되지 않은 사용자입니다.");
        return authorities;
    }

    public static Long getAuthorizedUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals(ANONYMOUS_USER_NAME)) throw new UnAuthorizedException("인증되지 않은 사용자입니다.");
        return Long.valueOf(userId);
    }
}
