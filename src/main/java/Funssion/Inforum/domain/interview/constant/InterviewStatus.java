package Funssion.Inforum.domain.interview.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum InterviewStatus {
    READY("면접 시작 전입니다."),
    ING_Q1("1번 문제를 푸는 중입니다."),
    ING_Q2("2번 문제를 푸는 중입니다."),
    ING_Q3("3번 문제를 푸는 중입니다."),
    DONE("면접이 완료 되었습니다.");
    private final String status;

    public static InterviewStatus nullableValueOf(String name){
        if (name == null) {
            return null;
        }
        try {
            return InterviewStatus.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null; // Enum 상수가 존재하지 않는 경우 null 반환
        }
    }
}
