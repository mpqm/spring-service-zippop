package com.fiiiiive.zippop.goods.controller;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.global.upload.S3FileUpload;
import com.fiiiiive.zippop.goods.model.dto.*;
import com.fiiiiive.zippop.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final S3FileUpload s3FileUpload;

    // 굿즈 등록
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<GoodsDto.CreateGoodsRes>> registerGoods(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx,
        @RequestPart("files") MultipartFile[] files,
        @Valid @RequestPart("dto") GoodsDto.CreateGoodsReq dto) throws BaseException {

        List<String> urls = s3FileUpload.multipleUpload(files);
        GoodsDto.CreateGoodsRes response = goodsService.registerGoods(customUserDetails, storeIdx, urls, dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.GOODS_REGISTER_SUCCESS, response));
    }

    // 굿즈 수정
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse<GoodsDto.UpdateGoodsRes>> updateGoods(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long goodsIdx,
        @RequestPart(name = "files") MultipartFile[] files,
        @Valid @RequestPart(name = "dto") GoodsDto.UpdateGoodsReq dto) throws BaseException {

        List<String> urls = s3FileUpload.multipleUpload(files);
        GoodsDto.UpdateGoodsRes response = goodsService.updateGoods(customUserDetails, goodsIdx, urls, dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.GOODS_UPDATE_SUCCESS,response));
    }

    // 굿즈 조회
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<GoodsDto.SearchGoodsRes>> searchGoods(
        @RequestParam Long goodsIdx) throws Exception {

        GoodsDto.SearchGoodsRes response = goodsService.searchGoods(goodsIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.GOODS_SEARCH_SUCCESS, response));
    }

    // 굿즈 목록 조회
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<GoodsDto.SearchGoodsRes>>> searchAllGoods(
        @RequestParam(required = false) Long storeIdx,
        @RequestParam(required = false) String keyword,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {

        Page<GoodsDto.SearchGoodsRes> response = goodsService.searchAllGoods(storeIdx, keyword, page, size);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.GOODS_SEARCH_ALL_SUCCESS, response));
    }



    // 굿즈 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<Void>> deleteGoods(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long goodsIdx) throws BaseException {

        goodsService.deleteGoods(customUserDetails, goodsIdx);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.GOODS_DELETE_SUCCESS));
    }

}
