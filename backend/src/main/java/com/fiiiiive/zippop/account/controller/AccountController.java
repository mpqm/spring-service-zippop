package com.fiiiiive.zippop.account.controller;

import com.fiiiiive.zippop.account.model.dto.*;
import com.fiiiiive.zippop.account.service.AccountService;
import com.fiiiiive.zippop.global.base.SuccessCode;
import com.fiiiiive.zippop.global.base.SuccessResponse;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.global.upload.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Tag(name = "account-api", description = "Account Management")
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final FileUploadService fileUploadService;
    private final AccountService accountService;

    // 회원가입
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createAccount(
            @Valid @RequestPart(name = "req") CreateAccountReq req,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        CreateAccountRes res = accountService.createAccount(req, fileUploadService.singleUpload(file));
        return ResponseEntity.ok(new SuccessResponse<>(res.getIsExistUser() ? SuccessCode.AUTH_SIGNUP_SUCCESS_IS_INACTIVE : SuccessCode.AUTH_SIGNUP_SUCCESS));
    }

    // 회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<GetAccountRes>> getAccount(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        GetAccountRes res = accountService.getAccount(user.getAccountId());
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_GET_PROFILE_SUCCESS, res));
    }

    // 회원 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<SuccessResponse<Void>> updateAccount(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestPart(name = "req") UpdateAccountReq req,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) {
        String image = fileUploadService.singleUpload(file);
        accountService.updateAccount(user.getAccountId(), req, image);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_EDIT_INFO_SUCCESS));
    }

    // 계정 비활성화
    @DeleteMapping("/de-activation")
    public ResponseEntity<SuccessResponse<Void>> deactivateAccount(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        accountService.deactivateAccount(user.getAccountId());
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_INACTIVE_SUCCESS));
    }

    // 계정 활성화 요청
    @PostMapping("/activation")
    public ResponseEntity<SuccessResponse<Void>> activateAccount(
            @Valid @RequestBody ActivateAccountReq req
    ) {
        accountService.activateAccount(req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_ACTIVE_SUCCESS));
    }

    // 이메일 검증
    @GetMapping("/verification")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam String email,
            @RequestParam String uuid
    ) {
        String url = accountService.verifyAccount(email, uuid);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }

    // 아이디 찾기
    @PostMapping("/id/find")
    public ResponseEntity<SuccessResponse<Void>> findId(
            @Valid @RequestBody FindIdReq req
    ) {
        accountService.findId(req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_FIND_ID_SUCCESS));
    }

    // 비밀번호 찾기
    @PostMapping("/pw/find")
    public ResponseEntity<SuccessResponse<Void>> findPw(
            @Valid @RequestBody FindPwReq req
    ) {
        accountService.findPw(req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_FIND_PW_SUCCESS));
    }

    // 비밀번호 찾기 후 패스워드 리셋
    @PatchMapping("/pw/reset")
    public ResponseEntity<SuccessResponse<Void>> resetPw(
            @Valid @RequestBody ResetPwReq req
    ) {
        accountService.resetPw(req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_RESET_PW_SUCCESS));
    }

    // 인증 상태에서 비밀번호 변경
    @PatchMapping("/pw/replace")
    public ResponseEntity<SuccessResponse<Void>> replacePw(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ReplacePwReq req
    ) {
        accountService.replacePw(user, req);
        return ResponseEntity.ok(new SuccessResponse<>(SuccessCode.AUTH_RESET_PW_SUCCESS));
    }

}
