package com.fiiiiive.zippop.auth.controller;

import com.fiiiiive.zippop.auth.model.dto.AuthDto;
import com.fiiiiive.zippop.auth.service.AuthService;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.service.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Tag(name = "auth-api", description = "Auth")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUpload;
    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> signup(
        @RequestPart(name = "dto") AuthDto.SignupAuthReq dto,
        @RequestPart(name = "file", required = false) MultipartFile file) throws Exception {

        String url = fileUpload.upload(file);
        Boolean response = authService.signup(dto, url);
        return ResponseEntity.ok(new BaseResponse<>(response ? BaseMessage.AUTH_SIGNUP_SUCCESS_IS_INACTIVE : BaseMessage.AUTH_SIGNUP_SUCCESS));
    }

    // 이메일 검증
    @GetMapping("/verify")
    public ResponseEntity<Void> verify(
        @RequestParam String email,
        @RequestParam String role,
        @RequestParam String uuid) throws BaseException {

        String response = authService.verify(email, role, uuid);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(response)).build();
    }

    // 계정 비활성화
    @GetMapping("/inactive")
    public ResponseEntity<BaseResponse<Void>> inactive(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException {

        authService.inactive(customUserDetails);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_INACTIVE_SUCCESS));
    }

    // 계정 ID 찾기
    @PostMapping("/find-id")
    public ResponseEntity<BaseResponse<Void>> findId(
        @RequestBody AuthDto.FindUserIdReq dto) throws BaseException {

        authService.findId(dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_FIND_ID_SUCCESS));
    }

    // 계정 PW 찾기
    @PostMapping("find-password")
    public ResponseEntity<BaseResponse<Void>> findPassword(
        @RequestBody AuthDto.FindPasswordReq dto) throws BaseException {

        authService.findPassword(dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_FIND_PASSWORD_SUCCESS));
    }

    // 회원 정보 수정
    @PatchMapping("/edit-info")
    public ResponseEntity<BaseResponse<Void>> editInfo(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart(name = "dto") AuthDto.EditInfoReq dto,
        @RequestPart(name = "file", required = false) MultipartFile file) throws BaseException {

        // 파일이 있으면 업로드하고, 없으면 DTO의 profileImageUrl 사용
        String url = fileUpload.upload(file);
        authService.editInfo(customUserDetails, dto, url);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_EDIT_INFO_SUCCESS));
    }

    // 회원 비밀번호 수정
    @PatchMapping("/edit-password")
    public ResponseEntity<BaseResponse<Void>> editPassword(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody AuthDto.EditPasswordReq dto) throws BaseException {

        authService.editPassword(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_EDIT_PASSWORD_SUCCESS));
    }

    // 회원 정보 조회
    @GetMapping("/get-info")
    public ResponseEntity<BaseResponse<AuthDto.GetInfoRes>> getInfo(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException{

        AuthDto.GetInfoRes response = authService.getInfo(customUserDetails);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_GET_PROFILE_SUCCESS, response));
    }

}