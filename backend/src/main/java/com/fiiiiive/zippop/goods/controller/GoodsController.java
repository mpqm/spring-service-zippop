package com.fiiiiive.zippop.goods.controller;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.service.GoodsService;
import com.fiiiiive.zippop.goods.model.dto.*;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import com.fiiiiive.zippop.global.utils.CloudFileUpload;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Tag(name = "goods-api", description = "Goods")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goods")
public class GoodsController {
    private final GoodsService goodsService;
    private final CloudFileUpload cloudFileUpload;

    // 팝업 굿즈 수정 등록
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestPart("files") MultipartFile[] files,
        @RequestPart("dto") CreateGoodsReq dto) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        CreateGoodsRes response = goodsService.register(customUserDetails, storeIdx, fileNames, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_GOODS_REGISTER_SUCCESS, response));
    }

    // 상품 이름으로 조회
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<GetGoodsRes>>> search(
        @RequestParam Long goodsIdx) throws Exception {
        GetGoodsRes popupGoodsPage = goodsService.search(goodsIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_GOODS_SEARCH_SUCCESS, popupGoodsPage));
    }

    // 팝업스토어 인덱스로 목록 조회
    @GetMapping("/search-store")
    public ResponseEntity<BaseResponse<Page<GetGoodsRes>>> searchAll(
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<GetGoodsRes> popupGoodsPage = goodsService.searchAll(storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_GOODS_SEARCH_SUCCESS, popupGoodsPage));
    }

    // 팝업 굿즈 수정
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse> update(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long goodsIdx,
        @RequestPart(name = "dto") UpdateGoodsReq dto,
        @RequestPart(name = "files") MultipartFile[] files) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        UpdateGoodsRes response = goodsService.update(customUserDetails, goodsIdx, fileNames, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_UPDATE_SUCCESS,response));
    }

    // 팝업 굿즈 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long goodsIdx) throws BaseException {
        goodsService.delete(customUserDetails, goodsIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_DELETE_SUCCESS));
    }
}
