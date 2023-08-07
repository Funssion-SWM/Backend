package Funssion.Inforum.domain.member.constant;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = {NameValidator.class})
public @interface NameValid {
    String message() default "닉네임은 한글 2글자 이상 10글자 미만, 영어,숫자 4글자 이상, 20글자 이하 가능합니다. \n 혼용시, 한글은 가중치 2, 영어,숫자는 가중치1 로써, 합이 20 이하여야합니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    boolean ignoreCase() default false;
}
