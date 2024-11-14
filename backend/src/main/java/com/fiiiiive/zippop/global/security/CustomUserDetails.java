package com.fiiiiive.zippop.global.security;

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
    @Getter
    private final String name;
    private final String password;
    private final String role;
    private final Boolean isEmailAuth;

    public CustomUserDetails(Long idx, String email, String role, String userId) {
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
        collection.add(new GrantedAuthority(){
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
