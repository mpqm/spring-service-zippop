package com.fiiiiive.zippop.goods.controller;


import com.fiiiiive.zippop.global.base.SuccessCode;
import com.fiiiiive.zippop.global.base.SuccessResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.upload.FileUploadService;
import com.fiiiiive.zippop.goods.model.dto.CreateGoodsReq;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import com.fiiiiive.zippop.goods.model.dto.UpdateGoodsReq;
import com.fiiiiive.zippop.goods.service.GoodsService;
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

@Tag(name = "goods-api", description = "Product Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goods")
public class GoodsController {

    private final GoodsService goodsService;
    private final FileUploadService fileUploadService;

    // 굿즈 등록
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createGoods(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("files") MultipartFile[] files,
            @Valid @RequestPart("req") CreateGoodsReq req
    ) {
        List<String> urls = fileUploadService.multipleUpload(files);
        goodsService.createGoods(customUserDetails, urls, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse<>(SuccessCode.GOODS_REGISTER_SUCCESS));
    }

    // 굿즈 수정
    @PatchMapping("/{goodsIdx}")
    public ResponseEntity<SuccessResponse<Void>> updateGoods(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long goodsIdx,
            @RequestPart(name = "files") MultipartFile[] files,
            @Valid @RequestPart(name = "req") UpdateGoodsReq req
    ) {
        List<String> urls = fileUploadService.multipleUpload(files);
        goodsService.updateGoods(customUserDetails, goodsIdx, urls, req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.GOODS_UPDATE_SUCCESS));
    }

    // 굿즈 상세 조회
    @GetMapping("/{goodsIdx}")
    public ResponseEntity<SuccessResponse<GetGoodsRes>> getGoods(
            @PathVariable Long goodsIdx
    ) {
        GetGoodsRes res = goodsService.getGoods(goodsIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.GOODS_SEARCH_SUCCESS, res));
    }

    // 굿즈 목록 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<GetGoodsRes>>> getGoodsList(
            @RequestParam(required = false) Long popupIdx,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GetGoodsRes> res = goodsService.getGoodsList(popupIdx, keyword, page, size);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.GOODS_SEARCH_ALL_SUCCESS, res));
    }

    // 굿즈 삭제
    @DeleteMapping("/{goodsIdx}")
    public ResponseEntity<SuccessResponse<Void>> deleteGoods(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long goodsIdx
    ) {
        goodsService.deleteGoods(user, goodsIdx);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.GOODS_DELETE_SUCCESS));
    }

}
