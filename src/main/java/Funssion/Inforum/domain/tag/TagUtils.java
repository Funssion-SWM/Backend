package Funssion.Inforum.domain.tag;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagUtils {
    public static List<String> createStringListFromArray(Array array)  {
        return Arrays.asList(array).stream()
                .map(arrayElement -> String.valueOf(arrayElement))
                .collect(Collectors.toList());

    }
}
