package com.fiiiiive.zippop.goods.controller;


import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.file.FileUploadService;
import com.fiiiiive.zippop.goods.model.GoodsDto;
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
    public ResponseEntity<BaseResponse<GoodsDto.CreateGoodsRes>> createGoods(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("files") MultipartFile[] files,
            @Valid @RequestPart("req") GoodsDto.CreateGoodsReq req
    ) throws BaseException {
        List<String> urls = fileUploadService.multipleUpload(files);
        GoodsDto.CreateGoodsRes res = goodsService.createGoods(customUserDetails, urls, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(BaseMessage.GOODS_REGISTER_SUCCESS, res));
    }

    // 굿즈 수정
    @PatchMapping("/{goodsIdx}")
    public ResponseEntity<BaseResponse<GoodsDto.UpdateGoodsRes>> updateGoods(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long goodsIdx,
            @RequestPart(name = "files") MultipartFile[] files,
            @Valid @RequestPart(name = "req") GoodsDto.UpdateGoodsReq req
    ) throws BaseException {
        List<String> urls = fileUploadService.multipleUpload(files);
        GoodsDto.UpdateGoodsRes res = goodsService.updateGoods(customUserDetails, goodsIdx, urls, req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.GOODS_UPDATE_SUCCESS, res));
    }

    // 굿즈 상세 조회
    @GetMapping("/{goodsIdx}")
    public ResponseEntity<BaseResponse<GoodsDto.SearchGoodsRes>> getGoods(
            @PathVariable Long goodsIdx
    ) throws BaseException {
        GoodsDto.SearchGoodsRes res = goodsService.getGoods(goodsIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.GOODS_SEARCH_SUCCESS, res));
    }

    // 굿즈 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<Page<GoodsDto.SearchGoodsRes>>> getGoodsList(
            @RequestParam(required = false) Long popupIdx,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws BaseException {
        Page<GoodsDto.SearchGoodsRes> res = goodsService.getGoodsList(popupIdx, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.GOODS_SEARCH_ALL_SUCCESS, res));
    }

    // 굿즈 삭제
    @DeleteMapping("/{goodsIdx}")
    public ResponseEntity<BaseResponse<Void>> deleteGoods(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long goodsIdx
    ) throws BaseException {
        goodsService.deleteGoods(user, goodsIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.GOODS_DELETE_SUCCESS));
    }

}
