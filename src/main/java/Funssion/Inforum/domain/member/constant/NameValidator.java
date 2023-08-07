package Funssion.Inforum.domain.member.constant;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<NameValid,String> {

    @Override
    public void initialize(NameValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        int totalWeight = 0;

        for (char c : value.toCharArray()) {
            if (isHangul(c)) {
                totalWeight += 2;
            } else if (isEnglishOrNumber(c)) {
                totalWeight += 1;
            } else {
                return false;
            }
        }
        return totalWeight >= 4 && totalWeight <= 20;
    }
    private static boolean isHangul(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES;
    }

    private static boolean isEnglishOrNumber(char c) {
        return (Character.isLetter(c) && Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN)
                || Character.isDigit(c);
    }

}
