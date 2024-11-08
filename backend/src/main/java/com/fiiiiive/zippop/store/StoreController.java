package com.fiiiiive.zippop.store;

import com.fiiiiive.zippop.common.exception.BaseException;
import com.fiiiiive.zippop.common.responses.BaseResponse;
import com.fiiiiive.zippop.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.member.model.CustomUserDetails;
import com.fiiiiive.zippop.store.model.dto.*;
import com.fiiiiive.zippop.store.model.dto.GetStoreRes;
import com.fiiiiive.zippop.utils.CloudFileUpload;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Tag(name = "popup-store-api", description = "Store")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup-store")
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
    public ResponseEntity<BaseResponse<GetStoreRes>> search(
        @RequestParam Long storeIdx) throws BaseException {
        GetStoreRes getStoreRes = storeService.search(storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, getStoreRes));
    }

    // 팝업 스토어 전체 조회
    // @ExeTimer
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<GetStoreRes>>> searchAll (
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<GetStoreRes> popupStoreList = storeService.searchAll(page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, popupStoreList));
    }


    // 기업이 등록한 팝업 스토어 조회
    // @ExeTimer
    @GetMapping("/search-company")
    public ResponseEntity<BaseResponse<Page<GetStoreRes>>> searchCompany(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<GetStoreRes> popupStoreResList = storeService.searchCompany(customUserDetails, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, popupStoreResList));
    }

    // 팝업스토어 키워드 기반 검색
    // @ExeTimer
    @GetMapping("/search-keyword")
    public ResponseEntity<BaseResponse<Page<GetStoreRes>>> searchKeyword(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size) throws BaseException {
        Page<GetStoreRes> popupStoreResList = storeService.searchKeyword(keyword, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, popupStoreResList));
    }

    // 팝업스토어 날짜 범위 기반 검색
    @GetMapping("/search-daterange")
    public ResponseEntity<BaseResponse<Page<GetStoreRes>>> searchStoreDate(
            @RequestParam LocalDateTime storeStartDate,
            @RequestParam LocalDateTime storeEndDate,
            @RequestParam int page,
            @RequestParam int size) throws BaseException {
        Page<GetStoreRes> popupStoreResList = storeService.searchDateRange(storeStartDate, storeEndDate, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, popupStoreResList));
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

    @GetMapping("/search-category")
    public ResponseEntity<BaseResponse<Page<GetStoreRes>>> searchCategory(
            @RequestParam String category,
            @RequestParam int page,
            @RequestParam int size) throws BaseException {
        Page<GetStoreRes> popupStoreResList = storeService.searchCategory(category, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, popupStoreResList));
    }

    @GetMapping("/search-store")
    public ResponseEntity<BaseResponse<GetStoreRes>> searchStore(
        @RequestParam String storeName) throws BaseException {
        GetStoreRes getStoreRes = storeService.searchStore(storeName);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, getStoreRes));
    }

    @GetMapping("/search-address")
    public ResponseEntity<BaseResponse<Page<GetStoreRes>>> searchAddress(
        @RequestParam String storeAddress,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<GetStoreRes> popupStoreResPage = storeService.searchAddress(storeAddress, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.POPUP_STORE_SEARCH_SUCCESS, popupStoreResPage));
    }
}
