package com.fiiiiive.zippop.global.base;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/company/popups");

    @Test
    void unauthenticatedRequestUsesStableErrorContract() {
        var response = handler.handleAuthentication(
                new InsufficientAuthenticationException("Full authentication is required"),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo("AUTHENTICATION_REQUIRED");
        assertThat(response.getBody().getMessage()).isEqualTo("로그인이 필요합니다.");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/company/popups");
    }

    @Test
    void badCredentialsStayOnTheLoginForm() {
        var response = handler.handleAuthentication(new BadCredentialsException("wrong password"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("BAD_CREDENTIALS");
        assertThat(response.getBody().getCode()).isEqualTo(ServerErrorCode.BAD_CREDENTIALS.getCode());
    }

    @Test
    void forbiddenRequestIsDifferentFromMissingAuthentication() {
        var response = handler.handleAccessDenied(new AccessDeniedException("denied"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("ACCESS_DENIED");
    }

    @Test
    void businessExceptionKeepsItsExistingBusinessCode() {
        var response = handler.handleServiceException(
                new ServiceException(ServiceErrorCode.CART_REGISTER_FAIL_ITEM_EXIST),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("CART_REGISTER_FAIL_ITEM_EXIST");
        assertThat(response.getBody().getCode()).isEqualTo(ServiceErrorCode.CART_REGISTER_FAIL_ITEM_EXIST.getCode());
    }

    @Test
    void serverExceptionUsesServerErrorCode() {
        var response = handler.handleServerException(
                new ServerException(ServerErrorCode.FILE_UPLOAD_ERROR, new RuntimeException("disk full")),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("FILE_UPLOAD_ERROR");
        assertThat(response.getBody().getCode()).isEqualTo(309);
        assertThat(response.getBody().getMessage()).doesNotContain("disk full");
    }

    @Test
    void unexpectedExceptionDoesNotExposeInternalDetails() {
        var response = handler.handleUnexpected(new RuntimeException("database password leaked"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().getMessage()).doesNotContain("database password leaked");
    }

    @Test
    void missingRouteIsReportedAsNotFoundInsteadOfServerFailure() {
        var response = handler.handleNoResourceFound(
                new NoResourceFoundException(HttpMethod.GET, "/api/v1/goods/"),
                new MockHttpServletRequest("GET", "/api/v1/goods/")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/goods/");
    }
}
