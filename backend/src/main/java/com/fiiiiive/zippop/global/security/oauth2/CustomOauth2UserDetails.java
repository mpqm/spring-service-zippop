package com.fiiiiive.zippop.global.security.oauth2;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.*;
@Getter
public class CustomOauth2UserDetails implements OAuth2User {
    private final CustomUserDetails account;
    private final Map<String, Object> attributes;
    public CustomOauth2UserDetails(CustomUserDetails account, Map<String, Object> attributes) {
        this.account = account; this.attributes = attributes;
    }
    @Override public String getName() { return account.getEmail(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return account.getAuthorities(); }
}
