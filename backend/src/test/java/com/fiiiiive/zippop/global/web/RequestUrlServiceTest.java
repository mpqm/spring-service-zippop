package com.fiiiiive.zippop.global.web;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUrlServiceTest {

    @Test
    void usesTheServletHostAndNonDefaultPortForVerificationAndBothRedirects() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("dev.example.test");
        request.setServerPort(8480);
        RequestUrlService urls = new RequestUrlService(request);

        assertThat(urls.emailVerificationUrl("member@example.test", "ROLE_CUSTOMER", "verify-id"))
                .isEqualTo("http://dev.example.test:8480/api/v1/accounts/verification"
                        + "?email=member%40example.test&role=ROLE_CUSTOMER&uuid=verify-id");
        assertThat(urls.loginResultUrl(true)).isEqualTo("http://dev.example.test:8480/login?success=true");
        assertThat(urls.loginResultUrl(false)).isEqualTo("http://dev.example.test:8480/login?error=true");
    }

    @Test
    void preservesTheServletContextAndEncodesQueryValues() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("shop.example.test");
        request.setServerPort(443);
        request.setContextPath("/zippop");
        RequestUrlService urls = new RequestUrlService(request);

        assertThat(urls.emailVerificationUrl("member+tag@example.test", "ROLE_COMPANY", "id&next=x"))
                .isEqualTo("https://shop.example.test/zippop/api/v1/accounts/verification"
                        + "?email=member%2Btag%40example.test&role=ROLE_COMPANY&uuid=id%26next%3Dx");
        assertThat(urls.loginResultUrl(true)).isEqualTo("https://shop.example.test/zippop/login?success=true");
    }

    @Test
    void usesThePublicHttpsAddressAfterForwardedHeaderProcessing() throws Exception {
        MockHttpServletRequest request = proxyRequest();
        request.addHeader("X-Forwarded-Proto", "https");
        request.addHeader("X-Forwarded-Host", "public.example.test");
        request.addHeader("X-Forwarded-Prefix", "/zippop");
        AtomicReference<String> url = new AtomicReference<>();

        new ForwardedHeaderFilter().doFilter(request, new MockHttpServletResponse(), (forwarded, response) ->
                url.set(new RequestUrlService((HttpServletRequest) forwarded).loginResultUrl(false)));

        assertThat(url.get()).isEqualTo("https://public.example.test/zippop/login?error=true");
    }

    @Test
    void preservesTheDockerPublishedPortFromTheForwardedHost() throws Exception {
        MockHttpServletRequest request = proxyRequest();
        request.addHeader("X-Forwarded-Proto", "http");
        request.addHeader("X-Forwarded-Host", "localhost:8081");
        AtomicReference<String> url = new AtomicReference<>();

        new ForwardedHeaderFilter().doFilter(request, new MockHttpServletResponse(), (forwarded, response) ->
                url.set(new RequestUrlService((HttpServletRequest) forwarded)
                        .emailVerificationUrl("member@example.test", "ROLE_CUSTOMER", "verify-id")));

        assertThat(url.get()).startsWith("http://localhost:8081/api/v1/accounts/verification?");
    }

    private MockHttpServletRequest proxyRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/accounts");
        request.setScheme("http");
        request.setServerName("backend");
        request.setServerPort(8080);
        return request;
    }
}
