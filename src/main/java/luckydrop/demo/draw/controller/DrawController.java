package luckydrop.demo.draw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.dto.request.DrawCreateRequest;
import luckydrop.demo.draw.dto.response.DrawCreateResponse;
import luckydrop.demo.draw.service.DrawService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/draws")
public class DrawController {

    private final DrawService drawService;

    @PostMapping
    public ResponseEntity<DrawCreateResponse> createDraw(@RequestBody @Valid DrawCreateRequest request) {
        Long drawId = drawService.createDraw(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DrawCreateResponse.of(drawId));
    }
}
