package com.fiiiiive.zippop.store.controller;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.store.service.StoreLikeService;
import com.fiiiiive.zippop.store.service.StoreReviewService;
import com.fiiiiive.zippop.store.service.StoreService;
import com.fiiiiive.zippop.store.model.dto.*;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.global.utils.CloudFileUpload;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "store-api", description = "Store")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
public class StoreController {

    private final StoreService storeService;
    private final StoreLikeService storeLikeService;
    private final StoreReviewService storeReviewService;
    private final CloudFileUpload cloudFileUpload;

    // 팝업 스토어 등록
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<CreateStoreRes>> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart(name = "files", required = false) MultipartFile[] files,
        @RequestPart(name = "dto") CreateStoreReq dto) throws BaseException {

        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        CreateStoreRes response = storeService.register(customUserDetails, dto, fileNames);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_REGISTER_SUCCESS, response));
    }

    // 팝업 스토어 단일 조회
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchStoreRes>> search(
        @RequestParam Long storeIdx ) throws BaseException {

        SearchStoreRes searchStoreRes = storeService.search(storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_SEARCH_SUCCESS, searchStoreRes));
    }
    
    // 팝업 스토어 목록 + 조건 조회(전체)
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<SearchStoreRes>>> searchAll(
        @RequestParam Boolean flag,
        @RequestParam(required = false) String keyword,
        @RequestParam int page,
        @RequestParam int size ) throws BaseException {

        Page<SearchStoreRes> response = storeService.searchAll(flag, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_SEARCH_ALL_SUCCESS, response));
    }

    // 팝업 스토어 목록 + 조건 조회(기업)
    @GetMapping("/search-all/as-company")
    public ResponseEntity<BaseResponse<Page<SearchStoreRes>>> searchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam(required = false) String keyword,
        @RequestParam int page,
        @RequestParam int size ) throws BaseException {

        Page<SearchStoreRes> response = storeService.searchAllAsCompany(customUserDetails, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_SEARCH_ALL_SUCCESS, response));
    }

    // 팝업 스토어 수정
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse<UpdateStoreRes>> update(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestPart(name = "dto") UpdateStoreReq dto,
        @RequestPart(name = "files", required = false) MultipartFile[] files) throws BaseException {

        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        UpdateStoreRes response = storeService.update(customUserDetails, storeIdx, dto, fileNames);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_UPDATE_SUCCESS,response));
    }

    // 팝업 스토어 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<Void>> delete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        storeService.delete(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_DELETE_SUCCESS));
    }

    // 팝업 스토어 좋아요
    @GetMapping("/like/register")
    public ResponseEntity<BaseResponse<Void>> like(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        storeLikeService.like(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_LIKE_SUCCESS));
    }

    // 팝업 스토어 좋아요 목록 조회
    @GetMapping("/like/search-all")
    public ResponseEntity<BaseResponse<Page<SearchStoreLikeRes>>> likeSearchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<SearchStoreLikeRes> response = storeLikeService.searchAll(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_LIKE_SEARCH_ALL_SUCCESS, response));
    }

    // 팝업 스토어 리뷰 등록
    @PostMapping("/review/register")
    public ResponseEntity<BaseResponse<CreateStoreReviewRes>> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestBody CreateStoreReviewReq dto) throws BaseException {

        CreateStoreReviewRes response = storeReviewService.register(customUserDetails, storeIdx, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.STORE_REVIEW_SUCCESS, response));
    }

    // 팝업 스토어 리뷰 목록 조회
    @GetMapping("/review/search-all")
    public ResponseEntity<BaseResponse<Page<SearchStoreReviewRes>>> searchStoreAsGuest(
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<SearchStoreReviewRes> response = storeReviewService.searchAll(storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.STORE_REVIEW_SEARCH_ALL_SUCCESS, response));
    }

    // 팝업 스토어 리뷰 목록 조회(고객)
    @GetMapping("/review/search-all/as-customer")
    public ResponseEntity<BaseResponse<Page<SearchStoreReviewRes>>> searchStoreAsCustomer(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<SearchStoreReviewRes> response = storeReviewService.searchAllAsCustomer(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.STORE_REVIEW_SEARCH_ALL_SUCCESS, response));
    }
}
