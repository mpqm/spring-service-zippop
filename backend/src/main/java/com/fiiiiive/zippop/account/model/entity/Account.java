package com.fiiiiive.zippop.account.model.entity;

import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.global.api.ServiceErrorCode;
import com.fiiiiive.zippop.global.api.ServiceException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType role;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 200)
    private String address;

    private String profileImageUrl;

    @Column(nullable = false)
    private Boolean isEmailAuth;

    @Column(nullable = false)
    private Boolean isInActive;

    @Builder.Default @Column(nullable = false)
    private long authVersion = 0;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    public boolean isEnabled() {
        return Boolean.TRUE.equals(isEmailAuth) && Boolean.FALSE.equals(isInActive);
    }

    public boolean isRecoveryTarget() { return Boolean.FALSE.equals(isEmailAuth) && Boolean.TRUE.equals(isInActive); }

    public void activate() {
        isEmailAuth = true;
        isInActive = false;
        authVersion++;
    }

    public void deactivate() {
        isEmailAuth = false;
        isInActive = true;
        authVersion++;
    }

    public void update(String name, String address, String phoneNumber, String image) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        if (image != null) this.profileImageUrl = image;
    }

    public void replacePw(String original, String replacement, PasswordEncoder encoder) {
        if (replacement == null || replacement.isBlank() || replacement.length() < 8 || replacement.length() > 72) {
            throw new ServiceException(ServiceErrorCode.AUTH_RESET_PW_FAIL);
        }
        if (!encoder.matches(original, password)) {
            throw new ServiceException(ServiceErrorCode.AUTH_RESET_PW_FAIL_PASSWORD_NOT_MATCH);
        }
        password = encoder.encode(replacement);
        authVersion++;
    }

    public void resetPw(String replacement, PasswordEncoder encoder) {
        if (replacement == null || replacement.isBlank() || replacement.length() < 8 || replacement.length() > 72) {
            throw new ServiceException(ServiceErrorCode.AUTH_RESET_PW_FAIL);
        }
        password = encoder.encode(replacement);
        authVersion++;
    }

}
