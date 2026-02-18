package com.fiiiiive.zippop.account.controller;

import com.fiiiiive.zippop.account.application.AccountFacade;
import com.fiiiiive.zippop.account.application.EmailAuthFacade;
import com.fiiiiive.zippop.account.model.AccountDto;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.upload.FileUploadService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
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
    private final AccountFacade accountFacade;
    private final EmailAuthFacade emailAuthFacade;

    // 회원가입
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createAccount(
            @Valid @RequestPart(name = "req") AccountDto.CreateAccountReq req,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) throws BaseException {
        Boolean res = accountFacade.createAccount(req, fileUploadService.singleUpload(file));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(res ? BaseMessage.AUTH_SIGNUP_SUCCESS_IS_INACTIVE : BaseMessage.AUTH_SIGNUP_SUCCESS));
    }

    // 회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<AccountDto.GetAccountRes>> getAccount(
            @AuthenticationPrincipal CustomUserDetails user
    ) throws BaseException{
        AccountDto.GetAccountRes res = accountFacade.getAccount(user);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_GET_PROFILE_SUCCESS, res));
    }

    // 회원 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<BaseResponse<Void>> updateAccount(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart(name = "req") AccountDto.UpdateAccountReq req,
            @RequestPart(name = "file", required = false) MultipartFile file
    ) throws BaseException {
        accountFacade.updateAccount(user, req, fileUploadService.singleUpload(file));
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_EDIT_INFO_SUCCESS));
    }

    // 계정 비활성화
    @DeleteMapping("/me")
    public ResponseEntity<BaseResponse<Void>> deactivateAccount(
            @AuthenticationPrincipal CustomUserDetails user
    ) throws BaseException {
        accountFacade.deactivateAccount(user);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_INACTIVE_SUCCESS));
    }

    // 계정 활성화 요청
    @PostMapping("/me/activation")
    public ResponseEntity<BaseResponse<Void>> requestActivation(
            @Valid @RequestBody AccountDto.RequestActivationReq req
    ) throws BaseException {
        accountFacade.requestActivation(req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_ACTIVE_SUCCESS));
    }

    // 이메일 검증
    @GetMapping("/verification")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam String uuid
    ) throws BaseException {
        String redirectUrl = emailAuthFacade.verifyEmail(email, role, uuid);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    // 아이디 찾기
    @PostMapping("/id/find")
    public ResponseEntity<BaseResponse<Void>> findId(
            @Valid @RequestBody AccountDto.FindIdReq req
    ) throws BaseException {
        accountFacade.findId(req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_FIND_ID_SUCCESS));
    }

    // 비밀번호 찾기
    @PostMapping("/password/find")
    public ResponseEntity<BaseResponse<Void>> findPassword(
            @Valid @RequestBody AccountDto.FindPasswordReq req
    ) throws BaseException {
        accountFacade.findPassword(req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_FIND_PW_SUCCESS));
    }

    // 비밀번호 변경
    @PatchMapping("/password/reset")
    public ResponseEntity<BaseResponse<Void>> resetPassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody AccountDto.ResetPasswordReq req
    ) throws BaseException {
        accountFacade.resetPassword(user, req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_RESET_PW_SUCCESS));
    }

}