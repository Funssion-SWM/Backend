package Funssion.Inforum.common.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class CustomStringUtils {
    private CustomStringUtils() {}
    public static List<String> getSearchStringList(String searchString) {
        return Arrays.stream(searchString.trim().split(" "))
                .map(str -> "%" + str + "%")
                .toList();
    }

    public static Long parseNullableStringtoLong(String str) {
        if (Objects.isNull(str)) return null;
        return Long.valueOf(str);
    }
}
