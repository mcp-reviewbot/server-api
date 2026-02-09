package reviewbot.review_server.port.in;

import reactor.core.publisher.Mono;

/**
 * gitHub webHook 처리
 */
public interface ReviewUseCase {
    Mono<Void> handle(String event, String deliveryId, String sig256, byte[] rawBody);
}
