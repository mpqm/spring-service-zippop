package com.fiiiiive.zippop.global.security.oauth2;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface OAuthIdentityRepository extends JpaRepository<OAuthIdentity, Long> {
    Optional<OAuthIdentity> findByProviderAndProviderSubject(String provider, String subject);
}
