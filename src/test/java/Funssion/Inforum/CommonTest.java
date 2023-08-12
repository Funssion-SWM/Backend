package Funssion.Inforum;

import Funssion.Inforum.common.constant.PostType;
import Funssion.Inforum.common.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Slf4j
public class CommonTest {

    @Test
    public void test() {
        ErrorResult errorResult = new ErrorResult(HttpStatus.NOT_FOUND, "Hi");
        log.info("error: {}", LocalDate.now().getYear());
    }
}
