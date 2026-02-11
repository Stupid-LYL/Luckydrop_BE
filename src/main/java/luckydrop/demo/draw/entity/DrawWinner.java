package luckydrop.demo.draw.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "draw_winner",
uniqueConstraints = {
        @UniqueConstraint(name = "up_draw_winner_draw_user", columnNames = {"drawId", "userId"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrawWinner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "draw_id", nullable = false)
    private Long drawId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public DrawWinner(Long drawId, Long userId) {
        this.drawId = drawId;
        this.userId = userId;
    }
}
