package luckydrop.demo.ticket.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.ticket.dto.response.LedgerItemResDto;
import luckydrop.demo.ticket.dto.response.WalletResDto;
import luckydrop.demo.ticket.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticket")
public class TicketController {


    private final TicketService ticketService;

    // 지갑 잔액 조회
    @GetMapping("/wallet/{userId}")
    public ResponseEntity<WalletResDto> getBalance(@PathVariable Long userId) {
        WalletResDto response = ticketService.getBalance(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 내역 조회
    @GetMapping("/ledger/{userId}")
    public ResponseEntity<List<LedgerItemResDto>> getLedger(@PathVariable Long userId) {
        List<LedgerItemResDto> response = ticketService.getLedger(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
