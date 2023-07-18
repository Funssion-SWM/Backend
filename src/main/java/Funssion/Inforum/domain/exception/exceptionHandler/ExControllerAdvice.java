package Funssion.Inforum.domain.exception.exceptionHandler;

import Funssion.Inforum.domain.exception.ErrorResult;
import Funssion.Inforum.domain.exception.NotYetImplementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(NotYetImplementException.class)
    public ErrorResult NotImplementHandle(NotYetImplementException e){
        log.error("[NotImplementHandlerException] ex ", e);
        return new ErrorResult("NotImplement",e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResult DuplicationHandle(IllegalStateException e){
        log.error("[DuplicationHandlerException] ex ", e);
        return new ErrorResult("Duplication", e.getMessage());
    }
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<String> AccessDeniedHandle(AccessDeniedException e){
//        log.error("[AccessDeniedException] ex ", e);
//        return new ResponseEntity(e.getMessage(),HttpStatus.UNAUTHORIZED);
//    }
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<String> AuthenticationExcpetionHandle(AuthenticationException e){
//        log.error("[AuthenticationException] ex ", e);
//        return new ResponseEntity(e.getMessage(),HttpStatus.UNAUTHORIZED);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(c -> errors.put(((FieldError)c).getField() , c.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
