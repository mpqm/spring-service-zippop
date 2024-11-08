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

@Tag(name = "popup-goods-api", description = "PopupGoods")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup-goods")
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
            @RequestParam String productName,
            @RequestParam int page,
            @RequestParam int size) throws Exception {
        Page<GetGoodsRes> popupGoodsPage = goodsService.search(productName, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_GOODS_SEARCH_SUCCESS, popupGoodsPage));
    }

    // 팝업스토어 인덱스로 조회
    @GetMapping("/search-store")
    public ResponseEntity<BaseResponse<Page<GetGoodsRes>>> searchStore(
        @RequestParam Long storeIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<GetGoodsRes> popupGoodsPage = goodsService.searchStore(storeIdx, page, size);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_GOODS_SEARCH_SUCCESS, popupGoodsPage));
    }

    // 팝업 굿즈 수정
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse> update(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long productIdx,
        @RequestPart(name = "dto") UpdateGoodsReq dto,
        @RequestPart(name = "files") MultipartFile[] files) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        UpdateGoodsRes response = goodsService.update(customUserDetails, productIdx, fileNames, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_UPDATE_SUCCESS,response));
    }

    // 팝업 굿즈 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long productIdx) throws BaseException {
        goodsService.delete(customUserDetails, productIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POPUP_STORE_DELETE_SUCCESS));
    }
}
