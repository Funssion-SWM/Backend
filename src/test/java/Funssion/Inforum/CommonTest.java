package Funssion.Inforum;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public class CommonTest {

    @Test
    public void test() {
        System.out.println(LocalDateTime.now());
    }
}
