package luckydrop.demo.entry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.entry.dto.request.DrawEntryRequest;
import luckydrop.demo.entry.dto.request.MyEntryListRequest;
import luckydrop.demo.entry.dto.response.DrawEntryResponse;
import luckydrop.demo.entry.dto.response.MyEntryResponse;
import luckydrop.demo.entry.dto.response.MyEntryStatsResponse;
import luckydrop.demo.entry.service.DrawEntryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
public class EntryController {

    private final DrawEntryService drawEntryService;

    @PostMapping("/{drawId}/entry")
    public ResponseEntity<DrawEntryResponse> enter(
            @PathVariable Long drawId,
            @Valid @RequestBody DrawEntryRequest req,
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,
            @AuthenticationPrincipal CustomUserPrincipal principal
            ) {
        DrawEntryResponse res = drawEntryService.enter(
                drawId,
                principal.getUser().getId(),
                req.getCount(),
                idempotencyKey
        );
        return ResponseEntity.ok(res);
    }

    @GetMapping("/my-entries")
    public ResponseEntity<Page<MyEntryResponse>> getMyEntries(
            MyEntryListRequest req,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Page<MyEntryResponse> entries = drawEntryService.getMyEntries(principal.getUser().getId(), req);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/my-entries/stats")
    public ResponseEntity<MyEntryStatsResponse> getMyEntryStats(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        MyEntryStatsResponse stats = drawEntryService.getMyEntryStats(principal.getUser().getId());
        return ResponseEntity.ok(stats);
    }
}
