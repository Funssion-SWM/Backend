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
        String fileName = "inforum-bucket/profiles/fc308805-dba8-4c51-8782-f5419f72ef29-77";
        System.out.println("fileName = " + fileName.substring(15));
    }
}
