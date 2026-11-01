package com.fiiiiive.zippop.account.controller;

import com.fiiiiive.zippop.account.application.AccountFacade;
import com.fiiiiive.zippop.account.application.EmailAuthFacade;
import com.fiiiiive.zippop.global.upload.FileUploadService;
import com.fiiiiive.zippop.global.web.RequestUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountVerificationTest {

    @Test
    void correctVerificationEndpointRedirectsSuccessfulVerificationToTheCurrentServer() throws Exception {
        mvc(true).perform(get("/api/v1/accounts/verification")
                        .param("email", "member@example.test")
                        .param("role", "ROLE_CUSTOMER").param("uuid", "verify-id"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://shop.example.test/login?success=true"));
    }

    @Test
    void invalidVerificationRedirectsToTheCurrentServersErrorPage() throws Exception {
        mvc(false).perform(get("/api/v1/accounts/verification")
                        .param("email", "member@example.test")
                        .param("role", "ROLE_CUSTOMER").param("uuid", "verify-id"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://shop.example.test/login?error=true"));
    }

    private MockMvc mvc(boolean verified) {
        EmailAuthFacade verification = mock(EmailAuthFacade.class);
        when(verification.verifyEmail("member@example.test", "ROLE_CUSTOMER", "verify-id"))
                .thenReturn(verified);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("shop.example.test");
        request.setServerPort(443);
        return MockMvcBuilders.standaloneSetup(new AccountController(
                mock(FileUploadService.class), mock(AccountFacade.class), verification,
                new RequestUrlService(request))).build();
    }
}
