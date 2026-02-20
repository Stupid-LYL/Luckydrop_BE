package luckydrop.demo.entry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.entry.dto.request.DrawEntryRequest;
import luckydrop.demo.entry.dto.response.DrawEntryResponse;
import luckydrop.demo.entry.service.DrawEntryService;
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
}
