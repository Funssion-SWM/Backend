package Funssion.Inforum.common.exception.controller;

import Funssion.Inforum.common.exception.*;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResult handleBadRequestEx(BadRequestException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getErrorResult();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    public ErrorResult handleUnauthorizedRequestEx(UnAuthorizedException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getErrorResult();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResult handleNotFoundEx(NotFoundException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getErrorResult();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, List<String>> handleValidationErrors(MethodArgumentNotValidException e) {
        log.warn("error message={}", e.getMessage(), e);
        return getErrorsMap(
                e.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList())
        );
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public ErrorResult handleDuplicateEx(DuplicateException e){
        log.warn("error message = {}",e.getMessage());
        return e.getErrorResult();
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
