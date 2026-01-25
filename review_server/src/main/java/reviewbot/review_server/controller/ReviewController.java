package reviewbot.review_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reviewbot.review_server.usecase.ReviewUseCase;

@RestController
@RequestMapping("/v1/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewUseCase reviewUseCase;

    @PostMapping("/webhook")
    public ResponseEntity<Void> githubWebhook(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String sig256,
            @RequestBody byte[] rawBody) {
        reviewUseCase.handle(event, deliveryId, sig256, rawBody);
        return ResponseEntity.ok().build();
    }
}
