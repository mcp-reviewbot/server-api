package reviewbot.review_server.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reviewbot.review_server.port.in.ReviewUseCase;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewUseCase reviewUseCase;

    @PostMapping("/webhook")
    public Mono<ResponseEntity<Void>> githubWebhook(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String sig256,
            @RequestBody byte[] rawBody) {
        return reviewUseCase.handle(event, deliveryId, sig256, rawBody)
                .thenReturn(ResponseEntity.accepted().build());
    }
}
