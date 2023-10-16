package Funssion.Inforum.common.utils;

import Funssion.Inforum.common.exception.etc.StringParseException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class CustomListUtils {
    private CustomListUtils() {};

    public static List<Long> toLongList(String string) {
        return Arrays.stream(string.substring(1, string.length() - 1).split(","))
                .map(Long::valueOf)
                .toList();
    }
}
