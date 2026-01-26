package reviewbot.review_server.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {
    private final GithubService githubService;
    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    /**
     * 추후 Redis나 DB로 교체 필요함 (실행한 deliveryId인지 확인 필요하기 때문)
     */
    public boolean isDuplicate(String deliveryId) {
        return !processed.add(deliveryId);
    }

    @Async
    public void dispatchPullRequest(String deliveryId, byte[] rawBody) {
        try {
            githubService.reviewPullRequest(rawBody);
            log.info("[{}] 성공적으로 처리되었습니다.", deliveryId);
        } catch (Exception e) {
            log.error("[{}] 처리 중 오류가 발생하였습니다.", deliveryId);
            log.error("에러 메시지 : {}", e.getMessage());
            // 여기서 재시도 전략(큐/DLQ/스케줄러)로 확장 가능
        }
    }
}
