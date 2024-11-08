package com.fiiiiive.zippop.comment.controller;

import com.fiiiiive.zippop.comment.service.CommentService;
import com.fiiiiive.zippop.comment.model.dto.CreateCommentReq;
import com.fiiiiive.zippop.comment.model.dto.UpdateCommentReq;
import com.fiiiiive.zippop.comment.model.dto.CreateCommentRes;
import com.fiiiiive.zippop.comment.model.dto.SearchCommentRes;
import com.fiiiiive.zippop.comment.model.dto.UpdateCommentRes;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name = "comment-api", description = "Comment")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<CreateCommentRes>> register(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long postIdx,
        @RequestBody CreateCommentReq dto) throws BaseException {
        CreateCommentRes response = commentService.register(customUserDetails, postIdx, dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.COMMENT_REGISTER_SUCCESS, response));
    }

    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse<Page<SearchCommentRes>>> searchAll(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam(required = false) Long postIdx,
        @RequestParam int page,
        @RequestParam int size) throws BaseException {
        Page<SearchCommentRes> response = null;
        if(customUserDetails != null && Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){
            response = commentService.searchAllAsCustomer(page, size, customUserDetails);
        } else {
            response = commentService.searchAllAsGuest(page, size, postIdx);
        }
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseMessage.COMMENT_SEARCH_ALL_SUCCESS, response));
    }

    @GetMapping("/like")
    public ResponseEntity<BaseResponse> like(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long commentIdx) throws BaseException {
        commentService.like(customUserDetails, commentIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.POST_LIKE_SUCCESS));
    }

    @PatchMapping("/update")
    public ResponseEntity<BaseResponse> update(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long commentIdx,
        @RequestBody UpdateCommentReq dto) throws BaseException {
        UpdateCommentRes response = commentService.update(customUserDetails, commentIdx, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.COMMENT_UPDATE_SUCCESS,response));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long commentIdx) throws BaseException {
        commentService.delete(customUserDetails, commentIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.COMMENT_DELETE_SUCCESS));
    }
}
