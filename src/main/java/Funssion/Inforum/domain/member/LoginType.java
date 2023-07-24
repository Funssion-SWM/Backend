package Funssion.Inforum.domain.member;

public enum LoginType {
    NON_SOCIAL(0),
    SOCIAL(1);

    private final int value;

    LoginType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}