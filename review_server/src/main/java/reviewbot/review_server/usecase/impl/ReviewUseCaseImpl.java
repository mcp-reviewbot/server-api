package reviewbot.review_server.usecase.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reviewbot.review_server.serivce.DispatchService;
import reviewbot.review_server.serivce.VerifyService;
import reviewbot.review_server.usecase.ReviewUseCase;


/**
 * WebHook 구현 클래스 (github)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewUseCaseImpl implements ReviewUseCase {
    private final VerifyService verifyService;
    private final DispatchService dispatchService;

    @Override
    public void handle(String event, String deliveryId, String sig256, byte[] rawBody) {
        log.info("[{}] webhook handle 시작", deliveryId);

        verifyService.verifyHMAC(sig256, rawBody);

        if (dispatchService.isDuplicate(deliveryId)) {
            log.info("이미 처리된 웹훅번호입니다 : {}", deliveryId);
            return;
        }

        if (!"pull_request".equals(event)) {
            log.info("현재 버전은 pr 이외의 버전을 지원하지 않습니다. 현재 버전 : {} ", event);
            return;
        }

        dispatchService.dispatchPullRequest(deliveryId, rawBody);
    }
}
