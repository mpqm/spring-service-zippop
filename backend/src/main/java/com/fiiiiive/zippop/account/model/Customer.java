package com.fiiiiive.zippop.account.model;

import com.fiiiiive.zippop.cart.model.Cart;
import com.fiiiiive.zippop.global.base.BaseEntity;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.orders.model.Orders;
import com.fiiiiive.zippop.popup.model.PopupLike;
import com.fiiiiive.zippop.popup.model.PopupReview;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer extends BaseEntity implements Account {

    // id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    // 이메일 (필수, 유니크, 최대 100자)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 사용자 ID (필수, 유니크, 최소 5자, 최대 20자)
    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    @Column(nullable = false)
    private String password;

    // 이름 (필수, 최대 50자)
    @Column(nullable = false, length = 50)
    private String name;

    // 전화번호 (필수, 최대 15자)
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    // 주소 (필수, 최대 200자)
    @Column(nullable = false, length = 200)
    private String address;

    // 역할 (필수, ROLE_CUSTOMER로 고정)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    // 프로필 이미지 URL (선택, 최대 255자)
    @Column(length = 255)
    private String profileImageUrl;

    @Column(nullable = false)
    private Integer point; // 포인트

    @Column(nullable = false)
    private Boolean isEmailAuth;

    @Column(nullable = false)
    private Boolean isInActive;

    // OneToMany
    @OneToMany(mappedBy = "customer")
    private List<Cart> cartList;

    @OneToMany(mappedBy = "customer")
    private List<PopupReview> popupReviewList;

    @OneToMany(mappedBy = "customer")
    private List<Orders> ordersList;

    @OneToMany(mappedBy = "customer")
    private List<PopupLike> popupLikeList;

    // Update
    public void update(
            String name,
            String address,
            String phoneNumber,
            String profileImageUrl
    ) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    // Customer 생성 팩토리 메서드
    public static Customer create(
            String email,
            String userId,
            String encodedPassword,
            String name,
            String phoneNumber,
            String address,
            String profileImageUrl
    ) {
        return Customer.builder()
                .email(email)
                .userId(userId)
                .password(encodedPassword)
                .name(name)
                .point(3000) // 신규 고객 기본 포인트
                .phoneNumber(phoneNumber)
                .address(address)
                .profileImageUrl(profileImageUrl)
                .role(RoleType.ROLE_CUSTOMER) // Value Object로 직접 할당
                .isEmailAuth(false)
                .isInActive(true)
                .build();
    }

    // DTO 변환 메서드
    public AccountDto.GetAccountRes toDto(){
        return AccountDto.GetAccountRes.builder()
                .name(this.getName())
                .point(this.getPoint())
                .role(this.getRole().getName())
                .profileImageUrl(this.getProfileImageUrl())
                .email(this.getEmail())
                .phoneNumber(this.getPhoneNumber())
                .address(this.getAddress())
                .build();
    }

    // 회원가입 검증
    public void validateSignup() {
        if (!(isInActive && !isEmailAuth)) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_ALREADY_EXIST);
        }
    }

    // 활성화 검증
    public void validateActive() {
        if (!this.isInActive) {
            throw new BaseException(BaseMessage.AUTH_ACTIVE_FAIL_NOT_INACTIVE);
        }
    }

    // if: 이메일 인증한 회원
    // else if: 비활성화 회원
    // else: 이메일 인증을 하지 않은 회원(예외)
    public void validateUserIdSendable() {
        if (isEmailAuth && !isInActive) return;
        if (!isEmailAuth && isInActive) return;
        throw new BaseException(BaseMessage.AUTH_FIND_ID_FAIL_NOT_EMAIL_VERIFY);
    }

    // 임시 비밀번호 발급 조건 검증
    public void validateTempPasswordIssuable() {
        if (!isInActive && isEmailAuth) return;
        if (isInActive && !isEmailAuth) return;
        throw new BaseException(BaseMessage.AUTH_FIND_PW_FAIL_NOT_EMAIL_VERIFY);
    }

    // 복구계정확인
    public boolean isRecoveryTarget() {
        return !this.isEmailAuth && this.isInActive;
    }

    // 비인증계정
    public boolean isInactiveButNotVerified() {
        return isInActive && !isEmailAuth;
    }

    // 계정 비활성화
    public void deactivate() {
        this.isEmailAuth = false;
        this.isInActive = true;
    }

    // 계정 활성화
    public void activate() {
        this.isEmailAuth = true;
        this.isInActive = false;
    }

    // 임시 비밀번호 발급
    public String issueTempPassword(PasswordEncoder encoder) {
        validateTempPasswordIssuable();
        String raw = UUID.randomUUID().toString();
        this.password = encoder.encode(raw);
        return raw;
    }

    // 패스워드 초기화
    public void resetPassword(
            String originPassword,
            String newPassword,
            PasswordEncoder encoder
    ) {
        if (!encoder.matches(originPassword, this.password)) {
            throw new BaseException(BaseMessage.AUTH_RESET_PW_FAIL_PASSWORD_NOT_MATCH);
        }
        this.password = encoder.encode(newPassword);
    }

    // 포인트 수정
    public void updatePoint(Integer point) {
        this.point = point;
    }

    // 역할 검증
    public void validateRole() {
        if (!(this.role == RoleType.ROLE_CUSTOMER)) {
            throw new BaseException(BaseMessage.AUTH_SIGNUP_FAIL_INVALID_ROLE);
        }
    }

}
