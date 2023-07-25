package Funssion.Inforum.domain.member.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("Funssion.Inforum.domain.member")
@Slf4j
public class MemberExceptionController{

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(NotYetImplementException.class)
    public ErrorResult handleNotImplementHandle(NotYetImplementException e){
        log.error("[NotImplementHandlerException] ex ", e);
        return new ErrorResult("NotImplement",e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResult handleDuplication(IllegalStateException e){
        log.error("[DuplicationHandlerException] ex ", e);
        return new ErrorResult("Duplication", e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error("[NotValidParameter] ex ", e);
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        return new ErrorResult("NotValidParameter", builder.toString());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResult handleValidationExceptions1(HttpMessageNotReadableException e) {
        log.error("[NotValidParameter] ex ", e);
        return new ErrorResult("NotValidParameter", "LoginType 형식을 확인해주세요.");
    }

}
