package com.fiiiiive.zippop.member.controller;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.member.service.MemberService;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.dto.EditInfoReq;
import com.fiiiiive.zippop.member.model.dto.EditPasswordReq;
import com.fiiiiive.zippop.member.model.dto.PostSignupReq;
import com.fiiiiive.zippop.member.model.dto.GetProfileRes;
import com.fiiiiive.zippop.member.model.dto.PostSignupRes;
import com.fiiiiive.zippop.member.service.EmailVerifyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "member-api", description = "Member")
@Slf4j
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailVerifyService emailVerifyService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<PostSignupRes>> signup(
    @RequestBody PostSignupReq dto) throws Exception {
        PostSignupRes response = memberService.signup(dto);
        String uuid = memberService.sendEmail(response);
        emailVerifyService.save(dto.getEmail(), uuid);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_REGISTER_SUCCESS, response));
    }

    @GetMapping("/verify")
    public ResponseEntity<BaseResponse> verify(
        String email, String role, String uuid) throws Exception, BaseException {
        if(emailVerifyService.isExist(email, uuid)){
            memberService.activeMember(email, role);
            return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EMAIL_VERIFY_SUCCESS));
        } else {
            return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EMAIL_VERIFY_FAIL));
        }
    }

    @GetMapping("/inactive")
    public ResponseEntity<BaseResponse> inactive(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception, BaseException {
        memberService.inActiveMenber(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_INACTIVE_SUCCESS));
    }

    @PatchMapping("/edit-info")
    public ResponseEntity<BaseResponse> editInfo(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody EditInfoReq dto) throws BaseException {
        memberService.editInfo(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EDIT_INFO_SUCCESS));
    }

    @PatchMapping("/edit-password")
    public ResponseEntity<BaseResponse> editPassword(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody EditPasswordReq dto) throws BaseException {
        memberService.editPassword(customUserDetails, dto);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_EDIT_PASSWORD_SUCCESS));
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<GetProfileRes>> getProfile(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws BaseException{
        GetProfileRes profile = memberService.getProfile(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.MEMBER_PROFILE_SUCCESS,profile));
    }
}