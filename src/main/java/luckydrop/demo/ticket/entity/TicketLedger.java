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
@Builder
@Getter
public class TicketLedger extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String reason;

    @Column(name = "ref_type", nullable = false)
    private String refType;

    @Column(name = "ref_id", nullable = true)
    private String refId;

    @Column(name = "idempotency_key",nullable = false)
    private String idempotencyKey;

}
