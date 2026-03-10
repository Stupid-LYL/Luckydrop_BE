package luckydrop.demo.ticket.entity;

import luckydrop.demo.user.entity.User;

public class TicketLedgerFactory {

    private TicketLedgerFactory() {}

    public static TicketLedger refundByDrawForceCancel(User user, Long drawId, int amount, String reasonCode) {
        if (user == null) throw new IllegalArgumentException("user is null");
        if (drawId == null) throw new IllegalArgumentException("drawId is null");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");

        String idempotencyKey = "DRAW_CANCEL_REFUND" + drawId + ":" + user.getId();

        return TicketLedger.builder()
                .user(user)
                .type("REFUND")
                .amount(amount)
                .reason(reasonCode != null ? reasonCode : "DRAW_CANCEL REFUND")
                .refType("DRAW")
                .refId(drawId)
                .idempotencyKey(idempotencyKey)
                .build();
    }
}
