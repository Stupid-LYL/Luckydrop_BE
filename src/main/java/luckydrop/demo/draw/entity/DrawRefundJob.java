package luckydrop.demo.draw.entity;

import jakarta.persistence.*;
import lombok.*;
import luckydrop.demo.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "draw_refund_job")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DrawRefundJob extends BaseEntity {

    @Id
    @Column(name = "draw_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    @Column(name = "cancel_reason_code", length = 50)
    private String cancelReasonCode;

    @Column(name = "cancel_reason_detail", length = 255)
    private String cancelReasonDetail;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    public static DrawRefundJob create(Draw draw, String code, String detail) {
        return DrawRefundJob.builder()
                .id(draw.getId())
                .draw(draw)
                .cancelReasonCode(code)
                .cancelReasonDetail(detail)
                .build();
    }

    public boolean isRefunded() {
        return refundedAt != null;
    }

    public void markRefunded(LocalDateTime now) {
        this.refundedAt = now;
    }

}
