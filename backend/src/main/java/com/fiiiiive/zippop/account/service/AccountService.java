package com.fiiiiive.zippop.account.service;

import com.fiiiiive.zippop.account.model.AccountDto;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;

// Account 도메인의 공통 서비스 인터페이스 - Company와 Customer 서비스의 공통 계약을 정의
public interface AccountService {

    // 회원가입
    Boolean createAccount(AccountDto.CreateAccountReq req, String url) throws BaseException;

    // 회원 정보 조회
    AccountDto.GetAccountRes getAccount(CustomUserDetails user) throws BaseException;

    // 회원 정보 수정
    void updateAccount(CustomUserDetails user, AccountDto.UpdateAccountReq req, String url) throws BaseException;

    // 계정 비활성화
    void deactivateAccount(CustomUserDetails user) throws BaseException;

    // 계정 활성화 요청
    void requestActivation(AccountDto.RequestActivationReq req) throws BaseException;

    // 이메일 검증 후 계정 활성화
    void activateAccount(String email) throws BaseException;

    // 아이디 찾기
    void recoverId(AccountDto.RecoverIdReq dto) throws BaseException;

    // 비밀번호 찾기 (임시 비밀번호 발급)
    void recoverPassword(AccountDto.RecoverPasswordReq dto) throws BaseException;

    // 비밀번호 변경
    void changePassword(CustomUserDetails user, AccountDto.ChangePasswordReq req) throws BaseException;

}
