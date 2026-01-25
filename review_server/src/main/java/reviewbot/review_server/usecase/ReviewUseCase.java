package reviewbot.review_server.usecase;

/**
 * gitHub webHook 처리
 */
public interface ReviewUseCase {
    void handle(String event, String deliveryId, String sig256, byte[] rawBody);
}