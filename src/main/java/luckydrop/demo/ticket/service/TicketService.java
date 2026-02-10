package luckydrop.demo.ticket.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.ticket.dto.response.LedgerItemResDto;
import luckydrop.demo.ticket.dto.response.WalletResDto;
import luckydrop.demo.ticket.entity.TicketLedger;
import luckydrop.demo.ticket.entity.TicketWallet;
import luckydrop.demo.ticket.repository.TicketLedgerRepository;
import luckydrop.demo.ticket.repository.TicketWalletRepository;
import luckydrop.demo.user.entity.User;
import luckydrop.demo.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketWalletRepository ticketWalletRepository;
    private final TicketLedgerRepository ticketLedgerRepository;

    private TicketWallet getWallet(Long userId) {
        return ticketWalletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("wallet not found for userId: " + userId));
    }

    // 잔액 조회
    public WalletResDto getBalance(Long userId) {
        TicketWallet wallet = getWallet(userId);
        return WalletResDto.builder()
                .userId(userId)
                .balance(wallet.getBalance())
                .build();
    }

    // 내역 조회
    public List<LedgerItemResDto> getLedger(Long userId) {
        List<TicketLedger> ledgers =
                ticketLedgerRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return ledgers.stream()
                .map(l -> LedgerItemResDto.builder()
                        .id(l.getId())
                        .type(l.getType())
                        .amount(l.getAmount())
                        .reason(l.getReason())
                        .refType(l.getRefType())
                        .refId(l.getRefId())
                        .idempotencyKey(l.getIdempotencyKey())
                        .createdAt(l.getCreatedAt())
                        .build()
                )
                .toList();
    }

}
