package luckydrop.demo.draw.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import luckydrop.demo.draw.enums.DrawStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "draw")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Draw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private Inventory inventory;

    @Column(length = 120, nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "winner_count", nullable = false)
    private Integer winnerCount;

    @Column(name = "ticket_cost_entry", nullable = false)
    private Integer ticketCostEntry;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DrawStatus status;

    //private LocalDateTime createdAt;


}
