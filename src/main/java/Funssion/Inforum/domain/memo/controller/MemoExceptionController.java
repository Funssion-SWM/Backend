package Funssion.Inforum.domain.memo.controller;

import Funssion.Inforum.domain.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice("Funssion.Inforum.domain.memo")
public class MemoExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleInvalidParamEx(IllegalArgumentException e) {
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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public String handleDBFailEx(MethodArgumentNotValidException e) {
        log.error("error message={}", e.getMessage(), e);
        return e.getMessage();
    }
}
