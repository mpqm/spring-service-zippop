package com.fiiiiive.zippop.store.controller;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Tag(name = "store-api", description = "Store")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
public class StoreController {
    private final StoreService storeService;
    private final CloudFileUpload cloudFileUpload;

    // 팝업 스토어 등록
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart("files") MultipartFile[] files,
        @RequestPart("dto") CreateStoreReq dto) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        CreateStoreRes response = storeService.register(customUserDetails, dto, fileNames);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_REGISTER_SUCCESS, response));
    }

    // 팝업 스토어 단일 조회
    // @ExeTimer
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchStoreRes>> search(
        @RequestParam(required = false) Long storeIdx,
        @RequestParam(required = false) String storeName) throws BaseException {
        SearchStoreRes searchStoreRes = storeService.search(storeIdx, storeName);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, searchStoreRes));
    }
    
    // 팝업 스토어 목록 조건 조회
    @GetMapping("/search-all/as-guest")
    public ResponseEntity<BaseResponse<Page<SearchStoreRes>>> searchAll(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size ) throws BaseException {
        Page<SearchStoreRes> response = storeService.searchAllAsGuest(keyword, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, response));
    }

    // 팝업 스토어 목록 조건 조회(기업)
    @GetMapping("/search-all/as-company")
    public ResponseEntity<BaseResponse<Page<SearchStoreRes>>> searchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "10") int size ) throws BaseException {
        Page<SearchStoreRes> response = storeService.searchAllAsCompany(customUserDetails, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, response));
    }

    // 팝업 스토어 수정
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse> update(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestPart(name = "dto") UpdateStoreReq dto,
        @RequestPart(name = "files") MultipartFile[] files) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        UpdateStoreRes response = storeService.update(customUserDetails, storeIdx, dto, fileNames);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_UPDATE_SUCCESS,response));
    }

    // 팝업 스토어 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {
        storeService.delete(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_DELETE_SUCCESS));
    }

    // 팝업 스토어 좋아요
    @GetMapping("/like")
    public ResponseEntity<BaseResponse> like(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws BaseException {
        storeService.like(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_LIKE_SUCCESS));
    }
}
