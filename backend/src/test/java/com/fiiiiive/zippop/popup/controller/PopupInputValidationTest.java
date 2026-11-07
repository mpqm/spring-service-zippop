package com.fiiiiive.zippop.popup.controller;

import com.fiiiiive.zippop.global.base.GlobalExceptionHandler;
import com.fiiiiive.zippop.global.upload.FileUploadService;
import com.fiiiiive.zippop.popup.repository.*;
import com.fiiiiive.zippop.popup.service.PopupService;
import com.fiiiiive.zippop.account.repository.CompanyRepository;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PopupInputValidationTest {
    private final PopupService service = new PopupService(mock(PopupRepository.class), mock(PopupLikeRepository.class),
            mock(CompanyRepository.class), mock(OrdersDetailRepository.class), mock(PopupReviewRepository.class),
            mock(ReserveRepository.class));
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new PopupController(service, mock(FileUploadService.class)))
            .setControllerAdvice(new GlobalExceptionHandler()).build();

    @Test void invalidEnumIsAClientError() throws Exception {
        mvc.perform(get("/api/v1/popups").param("status", "WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test void negativePageIsAClientError() throws Exception {
        mvc.perform(get("/api/v1/popups").param("status", "POPUP_START").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test void oversizePageIsRejectedBeforeAnyQuery() throws Exception {
        mvc.perform(get("/api/v1/popups").param("status", "POPUP_START").param("size", "101"))
                .andExpect(status().isBadRequest());
    }
}
