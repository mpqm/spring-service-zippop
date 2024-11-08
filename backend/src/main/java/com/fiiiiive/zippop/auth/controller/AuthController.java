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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "auth-api", description = "Auth")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<PostSignupRes>> signup(
    @RequestBody PostSignupReq dto) throws Exception {
        PostSignupRes response = authService.signup(dto);
        String uuid = authService.sendEmail(response);
        authService.save(dto.getEmail(), uuid);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_REGISTER_SUCCESS, response));
    }

    @GetMapping("/verify")
    public ResponseEntity<BaseResponse> verify(
        String email, String role, String uuid) throws Exception, BaseException {
        if(authService.isExist(email, uuid)){
            authService.activeMember(email, role);
            return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EMAIL_VERIFY_SUCCESS));
        } else {
            return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL));
        }
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
        SearchProfileRes profile = authService.getProfile(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_PROFILE_SUCCESS,profile));
    }
}