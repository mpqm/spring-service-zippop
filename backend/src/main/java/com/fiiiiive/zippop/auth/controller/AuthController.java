package com.fiiiiive.zippop.auth.controller;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.dto.EditInfoReq;
import com.fiiiiive.zippop.auth.model.dto.EditPasswordReq;
import com.fiiiiive.zippop.auth.model.dto.PostSignupReq;
import com.fiiiiive.zippop.auth.model.dto.SearchProfileRes;
import com.fiiiiive.zippop.auth.model.dto.PostSignupRes;
import com.fiiiiive.zippop.auth.service.AuthService;
import com.fiiiiive.zippop.global.utils.CloudFileUpload;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "auth-api", description = "Auth")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CloudFileUpload cloudFileUpload;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<PostSignupRes>> signup(
        @RequestPart(name = "dto") PostSignupReq dto,
        @RequestPart(name = "file") MultipartFile file) throws Exception {
        String url = cloudFileUpload.upload(file);
        PostSignupRes response = authService.signup(dto, url);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_REGISTER_SUCCESS, response));
    }

    @GetMapping("/verify")
    public ResponseEntity<BaseResponse> verify(
        @RequestParam String email,
        @RequestParam String role,
        @RequestParam String uuid) throws Exception, BaseException {
        authService.activeMember(email, role, uuid);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EMAIL_VERIFY_SUCCESS));
    }

    @GetMapping("/inactive")
    public ResponseEntity<BaseResponse> inactive(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception, BaseException {
        authService.inActiveMember(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_INACTIVE_SUCCESS));
    }

    @PatchMapping("/edit-info")
    public ResponseEntity<BaseResponse> editInfo(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody EditInfoReq dto) throws BaseException {
        authService.editInfo(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EDIT_INFO_SUCCESS));
    }

    @PatchMapping("/edit-password")
    public ResponseEntity<BaseResponse> editPassword(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody EditPasswordReq dto) throws BaseException {
        authService.editPassword(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EDIT_PASSWORD_SUCCESS));
    }

    @GetMapping("/search-profile")
    public ResponseEntity<BaseResponse<SearchProfileRes>> getProfile(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException{
        SearchProfileRes response = authService.getProfile(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_PROFILE_SUCCESS, response));
    }
}