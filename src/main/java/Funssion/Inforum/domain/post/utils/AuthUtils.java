package Funssion.Inforum.domain.post.utils;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.memo.exception.NeedAuthenticationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import static Funssion.Inforum.common.constant.CRUDType.READ;

public abstract class AuthUtils {
    public static Long getUserId(CRUDType type) {

        Long userId = SecurityContextUtils.getUserId();

        if (!userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID) || type == READ) return userId;

        throw new NeedAuthenticationException(type.toString().toLowerCase() + " fail");
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ResponseCookie invalidateAccessCookie = ResponseCookie.from("accessToken", "none").maxAge(0).path("/").domain(".inforum24.com").sameSite("none").httpOnly(true).secure(true).build();
        ResponseCookie invalidateRefreshCookie = ResponseCookie.from("refreshToken", "none").maxAge(0).path("/").domain(".inforum24.com").sameSite("none").httpOnly(true).secure(true).build();
        response.addHeader("Set-Cookie", invalidateAccessCookie.toString());
        response.addHeader("Set-Cookie",invalidateRefreshCookie.toString());
    }
}
