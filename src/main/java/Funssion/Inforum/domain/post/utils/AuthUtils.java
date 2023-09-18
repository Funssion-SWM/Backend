package Funssion.Inforum.domain.post.utils;

import Funssion.Inforum.common.constant.CRUDType;
import Funssion.Inforum.common.utils.SecurityContextUtils;
import Funssion.Inforum.domain.post.memo.exception.NeedAuthenticationException;

import static Funssion.Inforum.common.constant.CRUDType.READ;

public abstract class AuthUtils {
    public static Long getUserId(CRUDType type) {

        Long userId = SecurityContextUtils.getUserId();

        if (!userId.equals(SecurityContextUtils.ANONYMOUS_USER_ID) || type == READ) return userId;

        throw new NeedAuthenticationException(type.toString().toLowerCase() + " fail");
    }
}
