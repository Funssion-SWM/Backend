package Funssion.Inforum.common.utils;

import java.util.Arrays;
import java.util.List;

public abstract class CustomStringUtils {
    private CustomStringUtils() {}
    public static List<String> getSearchStringList(String searchString) {
        return Arrays.stream(searchString.trim().split(" "))
                .map(str -> "%" + str + "%")
                .toList();
    }
}
