package Funssion.Inforum.access_handler;

import Funssion.Inforum.common.dto.IsSuccessResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class OAuthAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        makeFailureResponseBody(response);
    }
    private void makeFailureResponseBody(HttpServletResponse response) throws IOException {
        String failureResponse = convertFailureObjectToString();
        response.setStatus(response.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(failureResponse);
    }

    private String convertFailureObjectToString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        IsSuccessResponseDto isSuccessResponseDto = new IsSuccessResponseDto(false, "같은 이메일의 일반 로그인 계정이 존재합니다.");
        String successResponse = objectMapper.writeValueAsString(isSuccessResponseDto);
        return successResponse;
    }
}
