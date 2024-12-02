package com.fiiiiive.zippop.global.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long idx;
    @Getter
    private final String userId;
    private final String email;
    private String role;
    @Getter
    private final String name;
    private final String password;
    private final Boolean isEmailAuth;

    // 직렬화/역직렬화 시 사용될 생성자
    @JsonCreator
    public CustomUserDetails(
            @JsonProperty("idx") Long idx,
            @JsonProperty("email") String email,
            @JsonProperty("role") String role,
            @JsonProperty("userId") String userId) {
        this.idx = idx;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.name = null;
        this.password = null;
        this.isEmailAuth = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEmailAuth;
    }
}
