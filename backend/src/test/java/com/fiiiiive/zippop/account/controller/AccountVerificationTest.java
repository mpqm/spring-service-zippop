package com.fiiiiive.zippop.account.controller;

import com.fiiiiive.zippop.account.service.AccountService;
import com.fiiiiive.zippop.global.upload.FileUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountVerificationTest {

    @Test
    void verificationEndpointRedirectsToUrlReturnedByService() throws Exception {
        mvc(true).perform(get("/api/v1/accounts/verification")
                        .param("email", "member@example.test")
                        .param("uuid", "verify-id"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://shop.example.test/login?success=true"));
    }

    @Test
    void verificationEndpointPreservesErrorRedirectReturnedByService() throws Exception {
        mvc(false).perform(get("/api/v1/accounts/verification")
                        .param("email", "member@example.test")
                        .param("uuid", "verify-id"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://shop.example.test/login?error=true"));
    }

    private MockMvc mvc(boolean verified) {
        AccountService accountService = mock(AccountService.class);
        when(accountService.verifyAccount("member@example.test", "verify-id"))
                .thenReturn("https://shop.example.test/login?" + (verified ? "success" : "error") + "=true");
        return MockMvcBuilders.standaloneSetup(new AccountController(
                mock(FileUploadService.class), accountService)).build();
    }

}
