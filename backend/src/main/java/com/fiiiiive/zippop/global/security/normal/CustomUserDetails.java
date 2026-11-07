package com.fiiiiive.zippop.global.security.normal;

import com.fiiiiive.zippop.account.model.entity.Account;
import lombok.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter @Builder @AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final Long accountId;
    private final Long customerId;
    private final Long companyId;
    private final String userId;
    private final String email;
    private final String role;
    private final String name;
    private final String password;
    private final boolean enabled;
    private final long authVersion;

    public static CustomUserDetails from(Account account, Long customerId, Long companyId) {
        return CustomUserDetails.builder().accountId(account.getIdx()).customerId(customerId).companyId(companyId)
                .userId(account.getUserId()).email(account.getEmail()).role(account.getRole().name())
                .name(account.getName()).password(account.getPassword()).enabled(account.isEnabled())
                .authVersion(account.getAuthVersion()).build();
    }
    public Long getCustomerId() {
        if (!"ROLE_CUSTOMER".equals(role) || customerId == null) throw new AccessDeniedException("고객 계정이 필요합니다.");
        return customerId;
    }
    public Long getCompanyId() {
        if (!"ROLE_COMPANY".equals(role) || companyId == null) throw new AccessDeniedException("기업 계정이 필요합니다.");
        return companyId;
    }
    @Override public String getUsername() { return email; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> role);
    }
    @Override public boolean isEnabled() { return enabled; }
}
