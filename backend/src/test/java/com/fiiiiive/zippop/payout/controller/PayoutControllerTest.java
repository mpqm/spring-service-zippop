package com.fiiiiive.zippop.payout.controller;

import com.fiiiiive.zippop.payout.service.PayoutService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PayoutControllerTest {

    @Test
    void getsPayoutsFromThePayoutApiRoute() throws Exception {
        PayoutService payoutService = mock(PayoutService.class);
        when(payoutService.getPayouts(isNull(), org.mockito.ArgumentMatchers.eq(7L),
                org.mockito.ArgumentMatchers.eq(0), org.mockito.ArgumentMatchers.eq(10)))
                .thenReturn(Page.empty());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PayoutController(payoutService)).build();

        mvc.perform(get("/api/v1/payouts/7"))
                .andExpect(status().isOk());

        verify(payoutService).getPayouts(isNull(), org.mockito.ArgumentMatchers.eq(7L),
                org.mockito.ArgumentMatchers.eq(0), org.mockito.ArgumentMatchers.eq(10));
    }
}
