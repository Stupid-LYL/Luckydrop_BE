package luckydrop.demo.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import luckydrop.demo.common.BaseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "refresh_token", nullable = false, length = 512)
    private String token;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    private RefreshToken(Long id, Member member, String token, LocalDateTime expiredAt) {
        this.id = id;
        this.member = member;
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
