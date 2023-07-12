package Funssion.Inforum.domain.exception.exceptionHandler;

import Funssion.Inforum.domain.exception.ErrorResult;
import Funssion.Inforum.domain.exception.NotYetImplementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
