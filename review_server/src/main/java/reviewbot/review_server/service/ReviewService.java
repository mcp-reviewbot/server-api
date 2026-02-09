package reviewbot.review_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reviewbot.review_server.port.in.ReviewUseCase;
import reactor.core.publisher.Mono;


/**
 * WebHook 구현 클래스 (github)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewUseCase {
    private final VerifyService verifyService;
    private final DispatchService dispatchService;

    @Override
    public Mono<Void> handle(String event, String deliveryId, String sig256, byte[] rawBody) {
        return Mono.fromRunnable(() -> log.info("[{}] webhook handle 시작", deliveryId))
                .then(Mono.fromRunnable(() -> verifyService.verifyHMAC(sig256, rawBody)))
                .then(Mono.defer(() -> {
                    if (dispatchService.isDuplicate(deliveryId)) {
                        log.info("이미 처리된 웹훅번호입니다 : {}", deliveryId);
                        return Mono.empty();
                    }

                    if (!"pull_request".equals(event)) {
                        log.info("현재 버전은 pr 이외의 버전을 지원하지 않습니다. 현재 버전 : {} ", event);
                        return Mono.empty();
                    }

                    return dispatchService.dispatchPullRequest(deliveryId, rawBody);
                }));
    }


}
