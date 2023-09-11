package Funssion.Inforum.domain.tag;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagUtils {
    public static List<String> createStringListFromArray(Array array)  {
        try {
            Object[] arrayElements = (Object[]) array.getArray();
            return Arrays.stream(arrayElements)
                    .map(arrayElement -> String.valueOf(arrayElement))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Array createSqlArray(JdbcTemplate template, List<String> tags) throws SQLException {
        try {
            return template.getDataSource().getConnection().createArrayOf("varchar", tags.toArray());
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
