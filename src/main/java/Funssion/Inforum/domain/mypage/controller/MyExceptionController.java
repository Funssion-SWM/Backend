package Funssion.Inforum.domain.mypage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice("Funssion.Inforum.domain.mypage")
public class MyExceptionController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleInvalidParamEx(NoSuchElementException e) {
        log.warn("error message={}",e.getMessage(), e);
        return e.getMessage();
    }
}
