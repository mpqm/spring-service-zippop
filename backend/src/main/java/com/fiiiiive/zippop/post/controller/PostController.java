package com.fiiiiive.zippop.post.controller;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.post.model.dto.*;
import com.fiiiiive.zippop.post.service.PostService;
import com.fiiiiive.zippop.global.utils.CloudFileUpload;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Tag(name = "post-api", description = "Post")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;
    private final CloudFileUpload cloudFileUpload;

    // 게시글 생성
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart(name = "dto") CreatePostReq dto,
        @RequestPart(name = "files") MultipartFile[] files) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        CreatePostRes response = postService.register(customUserDetails, fileNames, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POST_REGISTER_SUCCESS, response));
    }

    // 게시글 단일 조회
    // @ExeTimer
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<SearchPostRes>> search(
        @RequestParam Long postIdx) throws BaseException {
        SearchPostRes response = postService.search(postIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POST_SEARCH_BY_IDX_SUCCESS, response));
    }

    // 게시글 목록 조건 조회
    // @ExeTimer
    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<SearchPostRes>>> searchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam String keyword,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<SearchPostRes> response = null;
        if(customUserDetails != null && Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            response = postService.searchAllAsCustomer(customUserDetails, page, size);
        } else if (customUserDetails == null){
            response = postService.searchAllAsGuest(keyword, page, size);
        }
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POST_SEARCH_ALL_SUCCESS, response));
    }

    // 게시글 좋아요
    @GetMapping("/like")
    public ResponseEntity<BaseResponse> like(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long postIdx) throws BaseException {
        postService.like(customUserDetails, postIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POST_LIKE_SUCCESS));
    }

    // 게시글 수정
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse> update(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long postIdx,
        @RequestPart(name = "dto") UpdatePostReq dto,
        @RequestPart(name = "files") MultipartFile[] files) throws BaseException {
        List<String> fileNames = cloudFileUpload.multipleUpload(files);
        UpdatePostRes response = postService.update(customUserDetails, postIdx, dto, fileNames);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POST_UPDATE_SUCCESS,response));
    }

    // 게시글 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long postIdx) throws BaseException {
        postService.delete(customUserDetails, postIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POST_DELETE_SUCCESS));
    }
}
