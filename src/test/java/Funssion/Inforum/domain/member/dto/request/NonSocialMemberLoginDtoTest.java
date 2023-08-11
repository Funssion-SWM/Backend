package Funssion.Inforum.domain.member.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class NonSocialMemberLoginDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @AfterAll
    public static void close() {
        factory.close();
    }

    @Test
    @DisplayName("유효하지 않은 로그인 이메일 검사")
    public void checkNonValidLogin() {
        List<NonSocialMemberLoginDto> nonValidNonSocialMemberLoginDtos = new ArrayList<>();
        nonValidNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test@gmail", "notCheckPwdInUnitTest"));
        nonValidNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test", "notCheckPwdInUnitTest"));
        nonValidNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test@gmailcom", "notCheckPwdInUnitTest"));
        nonValidNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test@gmail.c", "notCheckPwdInUnitTest"));

        for (NonSocialMemberLoginDto nonValidNonSocialMemberLoginDto : nonValidNonSocialMemberLoginDtos) {
            Set<ConstraintViolation<NonSocialMemberLoginDto>> violations = validator.validate(nonValidNonSocialMemberLoginDto); // 유효하지 않은 경우 violations 값을 가지고 있다.
            Assertions.assertThat(violations.isEmpty()).isFalse();
        }
    }

    @Test
    @DisplayName("유효한 로그인 이메일 검사")
    public void checkValidLogin() {
        List<NonSocialMemberLoginDto> validNonSocialMemberLoginDtos = new ArrayList<>();
        validNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test@gmail.com", "notCheckPwdInUnitTest"));
        validNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test@naver.com", "notCheckPwdInUnitTest"));
        validNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test@korea.co.kr", "notCheckPwdInUnitTest"));
        validNonSocialMemberLoginDtos.add(new NonSocialMemberLoginDto("test@skku.edu", "notCheckPwdInUnitTest"));


        for (NonSocialMemberLoginDto validNonSocialMemberLoginDto : validNonSocialMemberLoginDtos) {
            Set<ConstraintViolation<NonSocialMemberLoginDto>> violations = validator.validate(validNonSocialMemberLoginDto); // 유효하지 않은 경우 violations 값을 가지고 있다.
            Assertions.assertThat(violations.isEmpty()).isTrue();
        }
    }

}