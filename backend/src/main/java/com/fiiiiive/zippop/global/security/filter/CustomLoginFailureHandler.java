package com.fiiiiive.zippop.global.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public CustomLoginFailureHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.handlerExceptionResolver = resolver;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        handlerExceptionResolver.resolveException(request, response, null, exception);
    }
}