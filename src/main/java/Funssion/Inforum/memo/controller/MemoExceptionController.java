package Funssion.Inforum.memo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice("Funssion.Inforum.memo")
public class MemoExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidParameterException.class)
    public String handleInvalidParamEx(InvalidParameterException e) {
        log.warn("error message={}",e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleMemoNotFoundEx(NoSuchElementException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationFailEx(MethodArgumentNotValidException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getMessage();
    }
}
