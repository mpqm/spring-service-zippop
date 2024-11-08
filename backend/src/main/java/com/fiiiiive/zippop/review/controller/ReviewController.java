package com.fiiiiive.zippop.review.controller;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.review.service.ReviewService;
import com.fiiiiive.zippop.review.model.dto.CreateReviewReq;
import com.fiiiiive.zippop.review.model.dto.CreateReviewRes;
import com.fiiiiive.zippop.review.model.dto.GetReviewRes;
import com.fiiiiive.zippop.global.utils.CloudFileUpload;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "popup-review-api", description = "Review")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final CloudFileUpload cloudFileUpload;

    // 리뷰 등록
    @PostMapping(value = "/register")
    public ResponseEntity<BaseResponse> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestPart("files") MultipartFile[] files,
        @RequestPart("dto") CreateReviewReq dto) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        CreateReviewRes response = reviewService.register(customUserDetails, storeIdx, fileNames, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_REGISTER_SUCCESS, response));
    }

    // 스토어의 인덱스 번호로 등록된 리뷰 조회
    @GetMapping(value = "/search-store")
    public ResponseEntity<BaseResponse<Page<GetReviewRes>>> searchStore(
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<GetReviewRes> response = reviewService.searchStore(storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, response));
    }

    // 자신이 쓴 리뷰 조회
    @GetMapping(value = "/search-customer")
    public ResponseEntity<BaseResponse<Page<GetReviewRes>>> searchCustomer(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<GetReviewRes> response = reviewService.searchCustomer(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, response));
    }
}