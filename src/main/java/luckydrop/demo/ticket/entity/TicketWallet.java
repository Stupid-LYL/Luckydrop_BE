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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private int balance = 0;

}
