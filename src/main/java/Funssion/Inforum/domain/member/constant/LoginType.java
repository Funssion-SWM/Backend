package Funssion.Inforum.domain.member.constant;

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

    public static LoginType fromValue(int value) {
        for (LoginType type : LoginType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid LoginType value: " + value);
    }
}