package Funssion.Inforum.common.exception.handler;

import Funssion.Inforum.common.exception.badrequest.BadRequestException;
import Funssion.Inforum.common.exception.etc.DuplicateException;
import Funssion.Inforum.common.exception.etc.ImageIOException;
import Funssion.Inforum.common.exception.etc.UnAuthorizedException;
import Funssion.Inforum.common.exception.notfound.NotFoundException;
import Funssion.Inforum.common.exception.response.ErrorResult;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResult handleBadRequestEx(BadRequestException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getErrorResult();
    }


    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    public ErrorResult handleUnauthorizedRequestEx(UnAuthorizedException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getErrorResult();
    }


    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResult handleNotFoundEx(NotFoundException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getErrorResult();
    }


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, List<String>> handleValidationEx(MethodArgumentNotValidException e) {
        log.warn("error message={}", e.getMessage(), e);
        return getErrorsMap(
                e.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList())
        );
    }
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResult handleDataIntegrityViolationOfJSONB(DataIntegrityViolationException e){
        return new ErrorResult(BAD_REQUEST,e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ErrorResult handleTypeMismatchEx (TypeMismatchException e) {
        log.warn("error message={}", e.getMessage(), e);
        return new ErrorResult(BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResult handleValidationEx(ValidationException e) {
        log.warn("error message={}", e.getMessage(), e);
        return new ErrorResult(BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingRequestValueException.class)
    public ErrorResult handleMissingRequestValueEx(MissingRequestValueException e) {
        log.warn("error message={}", e.getMessage(), e);
        return new ErrorResult(BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ImageIOException.class)
    public ErrorResult handleImageIOException(ImageIOException e) {
        log.warn("error message={}", e.getMessage(), e);
        return e.getErrorResult();
    }
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public ErrorResult handleDuplicateEx(DuplicateException e){
        log.warn("error message = {}",e.getMessage());
        return e.getErrorResult();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(PSQLException.class)
    public ErrorResult handlePSQLException(PSQLException e){
        return new ErrorResult(BAD_REQUEST, "DB exception occurred");
    }
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ErrorResult handleGeneralEx(Throwable e) {
        log.error("server error occurs, message = {}", e.getMessage(), e);
        return new ErrorResult(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
