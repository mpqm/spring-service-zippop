package com.fiiiiive.zippop.store.controller;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.store.model.dto.*;
import com.fiiiiive.zippop.store.service.StoreService;
import com.fiiiiive.zippop.global.service.S3FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final S3FileUploadService s3FileUploadService;

    // 스토어 등록
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<StoreDto.CreateStoreRes>> registerStore(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart(name = "files", required = false) MultipartFile[] files,
        @Valid @RequestPart(name = "dto") StoreDto.CreateStoreReq dto) throws BaseException {

        List<String> urls = s3FileUploadService.multipleUpload(files);
        StoreDto.CreateStoreRes response = storeService.registerStore(customUserDetails, dto, urls);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_REGISTER_SUCCESS, response));
    }

    // 스토어 수정
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse<StoreDto.UpdateStoreRes>> updateStore(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestPart(name = "files", required = false) MultipartFile[] files,
        @Valid @RequestPart(name = "dto") StoreDto.UpdateStoreReq dto) throws BaseException {

        List<String> urls = s3FileUploadService.multipleUpload(files);
        StoreDto.UpdateStoreRes response = storeService.updateStore(customUserDetails, storeIdx, dto, urls);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_UPDATE_SUCCESS,response));
    }

    // 스토어 조회
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<StoreDto.SearchStoreRes>> searchStore(
        @RequestParam Long storeIdx) throws BaseException {

        StoreDto.SearchStoreRes searchStoreRes = storeService.searchStore(storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_SEARCH_SUCCESS, searchStoreRes));
    }

    // 스토어 목록 조회
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<StoreDto.SearchStoreRes>>> searchAllStore(
        @RequestParam String status,
        @RequestParam(required = false) String keyword,
        @RequestParam int page,
        @RequestParam int size ) throws BaseException {

        Page<StoreDto.SearchStoreRes> response = storeService.searchAllStore(status, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_SEARCH_ALL_SUCCESS, response));
    }

    // 스토어 목록 조회(기업용)
    @GetMapping("/search-all/as-company")
    public ResponseEntity<BaseResponse<Page<StoreDto.SearchStoreRes>>> searchAllStoreAsCompany(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam(required = false) String keyword,
        @RequestParam int page,
        @RequestParam int size ) throws BaseException {

        Page<StoreDto.SearchStoreRes> response = storeService.searchAllStoreAsCompany(customUserDetails, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_SEARCH_ALL_SUCCESS, response));
    }

    // 스토어 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<Void>> deleteStore(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        storeService.deleteStore(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_DELETE_SUCCESS));
    }

    // 스토어 좋아요 증감
    @GetMapping("/like/register")
    public ResponseEntity<BaseResponse<Void>> like(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {

        storeService.registerStoreLike(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_LIKE_SUCCESS));
    }

    // 스토어 좋아요 목록 (고객 회원) 조회
    @GetMapping("/like/search-all")
    public ResponseEntity<BaseResponse<Page<StoreDto.SearchStoreLikeRes>>> likeSearchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<StoreDto.SearchStoreLikeRes> response = storeService.searchAllStoreLike(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_LIKE_SEARCH_ALL_SUCCESS, response));
    }

    // 스토어 리뷰 등록
    @PostMapping("/review/register")
    public ResponseEntity<BaseResponse<StoreDto.CreateStoreReviewRes>> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @Valid @RequestBody StoreDto.CreateStoreReviewReq dto) throws BaseException {

        StoreDto.CreateStoreReviewRes response = storeService.registerStoreReview(customUserDetails, storeIdx, dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_REVIEW_SUCCESS, response));
    }

    // 팝업 스토어 리뷰 목록 조회
    @GetMapping("/review/search-all")
    public ResponseEntity<BaseResponse<Page<StoreDto.SearchStoreReviewRes>>> searchStoreAsGuest(
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<StoreDto.SearchStoreReviewRes> response = storeService.searchAllStoreReview(storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_REVIEW_SEARCH_ALL_SUCCESS, response));
    }

    // 팝업 스토어 리뷰 목록 조회(고객)
    @GetMapping("/review/search-all/as-customer")
    public ResponseEntity<BaseResponse<Page<StoreDto.SearchStoreReviewRes>>> searchStoreAsCustomer(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<StoreDto.SearchStoreReviewRes> response = storeService.searchAllStoreReviewAsCustomer(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.STORE_REVIEW_SEARCH_ALL_SUCCESS, response));
    }
}
