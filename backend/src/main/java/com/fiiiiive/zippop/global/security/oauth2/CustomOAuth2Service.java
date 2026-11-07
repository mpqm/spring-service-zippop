package com.fiiiiive.zippop.global.security.oauth2;

import com.fiiiiive.zippop.account.model.entity.*;
import com.fiiiiive.zippop.account.repository.*;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService {
    private final AccountRepository accounts;
    private final CustomerRepository customers;
    private final OAuthIdentityRepository identities;
    private final CustomUserDetailService users;
    private final PasswordEncoder encoder;

    @Override @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User remote = super.loadUser(request);
        String provider = request.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(provider)) throw failure("지원하지 않는 소셜 로그인입니다.");
        OAuth2UserInfo info = new KakaoUserDetails(remote.getAttributes());
        Account account = identities.findByProviderAndProviderSubject(provider, info.getProviderId())
                .map(OAuthIdentity::getAccount).orElse(null);
        if (account == null) {
            if (info.getEmail() == null || info.getEmail().isBlank()) throw failure("이메일 제공 동의가 필요합니다.");
            if (accounts.findByEmail(info.getEmail()).isPresent())
                throw failure("이미 가입된 이메일입니다. 기존 계정으로 로그인해주세요.");
            account = accounts.save(Account.builder().userId("oauth_" + UUID.randomUUID().toString().replace("-", "").substring(0,14))
                    .email(info.getEmail()).password(encoder.encode(UUID.randomUUID().toString()))
                    .role(RoleType.ROLE_CUSTOMER).name(info.getName() == null ? "고객" : info.getName())
                    .phoneNumber("").address("").isEmailAuth(true).isInActive(false).build());
            customers.saveAndFlush(Customer.builder().account(account).point(3000).build());
            identities.save(OAuthIdentity.builder().provider(provider).providerSubject(info.getProviderId()).account(account).build());
        }
        if (!account.isEnabled()) throw failure("비활성화된 계정입니다.");
        return new CustomOauth2UserDetails(users.loadByAccountId(account.getIdx()), remote.getAttributes());
    }
    private OAuth2AuthenticationException failure(String message) {
        return new OAuth2AuthenticationException(new OAuth2Error("invalid_account"), message);
    }
}
