package com.fiiiiive.zippop.global.security.oauth2;
import com.fiiiiive.zippop.account.model.entity.Account;
import jakarta.persistence.*;
import lombok.*;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor @Entity
@Table(name = "oauth_identity", uniqueConstraints = @UniqueConstraint(name = "uk_oauth_provider_subject", columnNames = {"provider", "provider_subject"}))
public class OAuthIdentity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false, length = 30)
    private String provider;
    @Column(name = "provider_subject", nullable = false, length = 191)
    private String providerSubject;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_idx", nullable = false)
    private Account account;
}
