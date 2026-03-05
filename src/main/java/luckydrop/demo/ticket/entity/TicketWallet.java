package luckydrop.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import luckydrop.demo.common.BaseEntity;
import luckydrop.demo.user.entity.User;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TicketWallet extends BaseEntity {

    @Id
    private Long id;   // user_id와 같은 값 (PK이자 FK)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId                    // User의 id를 이 엔티티의 PK로도 사용
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private int balance = 0;
}
