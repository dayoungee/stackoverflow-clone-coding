package com.rainbow.sof.domain.user.auth.handler.authError;

import com.rainbow.sof.domain.user.auth.util.Responder.AuthenticationErrorResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//인증 에러 관리
@Slf4j
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Exception exception = (Exception) request.getAttribute("exception");
        AuthenticationErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
        if (exception==null){
            log.warn("Unauthorized error happened: {}", HttpStatus.UNAUTHORIZED.value());
        }
        else {
            log.warn("Unauthorized error happened: {}", exception.getMessage());
        }
    }
}
