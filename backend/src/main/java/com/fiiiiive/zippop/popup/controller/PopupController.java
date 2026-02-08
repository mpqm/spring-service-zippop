package com.fiiiiive.zippop.popup.controller;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.file.FileUploadService;
import com.fiiiiive.zippop.payout.model.PayoutDto;
import com.fiiiiive.zippop.payout.service.PayoutService;
import com.fiiiiive.zippop.popup.model.PopupDto;
import com.fiiiiive.zippop.popup.service.PopupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "popup-api", description = "Popup Store Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/popups")
public class PopupController {

    private final PopupService popupService;
    private final FileUploadService fileUploadService;

    // 팝업 생성
    @PostMapping
    public ResponseEntity<BaseResponse<PopupDto.CreatePopupRes>> createPopup(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart(name = "files", required = false) MultipartFile[] files,
            @Valid @RequestPart(name = "req") PopupDto.CreatePopupReq req
    ) throws BaseException {
        List<String> urls = fileUploadService.multipleUpload(files);
        PopupDto.CreatePopupRes res = popupService.createPopup(user, req, urls);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(BaseMessage.STORE_REGISTER_SUCCESS, res));
    }

    // 팝업 수정
    @PatchMapping("/{popupIdx}")
    public ResponseEntity<BaseResponse<PopupDto.UpdatePopupRes>> updatePopup(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestPart(name = "files", required = false) MultipartFile[] files,
            @Valid @RequestPart(name = "req") PopupDto.UpdatePopupReq req
    ) throws BaseException {

        List<String> urls = fileUploadService.multipleUpload(files);
        PopupDto.UpdatePopupRes res = popupService.updatePopup(user, popupIdx, req, urls);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_UPDATE_SUCCESS, res));
    }

    // 팝업 상세 조회
    @GetMapping("/{popupIdx}")
    public ResponseEntity<BaseResponse<PopupDto.SearchPopupRes>> getPopup(
            @PathVariable Long popupIdx
    ) throws BaseException {
        PopupDto.SearchPopupRes searchPopupRes = popupService.getPopup(popupIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_SEARCH_SUCCESS, searchPopupRes));
    }

    // 팝업 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<Page<PopupDto.SearchPopupRes>>> getPopups(
            @RequestParam String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws BaseException {
        Page<PopupDto.SearchPopupRes> res = popupService.getPopups(status, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_SEARCH_ALL_SUCCESS, res));
    }

    // 내 팝업 목록 조회 (기업용)
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<Page<PopupDto.SearchPopupRes>>> getMyPopups(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) throws BaseException {

        Page<PopupDto.SearchPopupRes> res = popupService.getMyPopups(user, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_SEARCH_ALL_SUCCESS, res));
    }

    // 팝업 삭제
    @DeleteMapping("/{popupIdx}")
    public ResponseEntity<BaseResponse<Void>> deletePopup(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long popupIdx) throws BaseException {

        popupService.deletePopup(user, popupIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_DELETE_SUCCESS));
    }

    // 팝업 좋아요 토글
    @PostMapping("/{popupIdx}/likes")
    public ResponseEntity<BaseResponse<Void>> togglePopupLike(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long popupIdx) throws BaseException {

        popupService.togglePopupLike(user, popupIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_LIKE_SUCCESS));
    }

    // 내가 좋아요한 팝업 목록 조회
    @GetMapping("/likes/me")
    public ResponseEntity<BaseResponse<Page<PopupDto.SearchPopupLikeRes>>> getMyLikedPopups(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) throws BaseException {

        Page<PopupDto.SearchPopupLikeRes> res = popupService.getMyLikedPopups(user, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_LIKE_SEARCH_ALL_SUCCESS, res));
    }

    // 팝업 리뷰 등록
    @PostMapping("/{popupIdx}/reviews")
    public ResponseEntity<BaseResponse<PopupDto.CreatePopupReviewRes>> createPopupReview(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long popupIdx,
        @Valid @RequestBody PopupDto.CreatePopupReviewReq req) throws BaseException {

        PopupDto.CreatePopupReviewRes res = popupService.createPopupReview(user, popupIdx, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(BaseMessage.STORE_REVIEW_SUCCESS, res));
    }

    // 팝업 리뷰 목록 조회
    @GetMapping("/{popupIdx}/reviews")
    public ResponseEntity<BaseResponse<Page<PopupDto.SearchPopupReviewRes>>> getPopupReviews(
        @PathVariable Long popupIdx,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) throws BaseException {

        Page<PopupDto.SearchPopupReviewRes> res = popupService.getPopupReviews(popupIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_REVIEW_SEARCH_ALL_SUCCESS, res));
    }

    // 내가 작성한 리뷰 목록 조회
    @GetMapping("/reviews/me")
    public ResponseEntity<BaseResponse<Page<PopupDto.SearchPopupReviewRes>>> getMyReviews(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) throws BaseException {
        Page<PopupDto.SearchPopupReviewRes> res = popupService.getMyReviews(user, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_REVIEW_SEARCH_ALL_SUCCESS, res));
    }

    // 팝업의 정산 목록 조회
    @GetMapping("/{popupIdx}/payouts")
    public ResponseEntity<BaseResponse<Page<PayoutDto.SearchPayoutRes>>> getPopupPayouts(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long popupIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws BaseException {
        Page<PayoutDto.SearchPayoutRes> res = popupService.getPopupPayouts(user, popupIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.PAYOUT_SEARCH_SUCCESS, res));
    }

}
