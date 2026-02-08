package com.fiiiiive.zippop.account.controller;

import com.fiiiiive.zippop.account.application.AccountFacade;
import com.fiiiiive.zippop.account.application.EmailAuthFacade;
import com.fiiiiive.zippop.account.model.AccountDto;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseResponse;
import com.fiiiiive.zippop.global.file.FileUploadService;
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

    // 회원 정보 조회 (RESTful: /me는 현재 인증된 사용자를 나타내는 관례)
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

    // 계정 비활성화 (RESTful: 리소스 중심 설계, 활성화 상태를 리소스로 취급)
    @DeleteMapping("/me/activation")
    public ResponseEntity<BaseResponse<Void>> deactivateAccount(
            @AuthenticationPrincipal CustomUserDetails user
    ) throws BaseException {
        accountFacade.inActiveAccount(user);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_INACTIVE_SUCCESS));
    }

    // 계정 활성화 요청 (이메일로 활성화 링크 전송)
    @PostMapping("/activation")
    public ResponseEntity<BaseResponse<Void>> requestActivation(
            @Valid @RequestBody AccountDto.UpdateAccountStatusReq req
    ) throws BaseException {
        accountFacade.activeAccount(req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_ACTIVE_SUCCESS));
    }

    // 이메일 검증 (RESTful: 검증 리소스)
    @GetMapping("/verification")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam String uuid
    ) throws BaseException {
        String redirectUrl = emailAuthFacade.emailAuth(email, role, uuid);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    // 아이디 찾기 (RESTful: 복구 리소스의 하위 리소스로 설계)
    @PostMapping("/recovery/username")
    public ResponseEntity<BaseResponse<Void>> recoverUsername(
            @Valid @RequestBody AccountDto.FindAccountIdReq req
    ) throws BaseException {
        accountFacade.findAccountId(req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_FIND_ID_SUCCESS));
    }

    // 비밀번호 찾기 (임시 비밀번호 발급)
    @PostMapping("/recovery/password")
    public ResponseEntity<BaseResponse<Void>> recoverPassword(
            @Valid @RequestBody AccountDto.FindAccountPwReq req
    ) throws BaseException {
        accountFacade.findAccountPw(req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_FIND_PW_SUCCESS));
    }

    // 비밀번호 변경 (RESTful: 비밀번호를 리소스로 취급)
    @PatchMapping("/me/password")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody AccountDto.ResetAccountPwReq req
    ) throws BaseException {
        accountFacade.resetAccountPw(user, req);
        return ResponseEntity.ok(new BaseResponse<>(BaseMessage.AUTH_RESET_PW_SUCCESS));
    }

}