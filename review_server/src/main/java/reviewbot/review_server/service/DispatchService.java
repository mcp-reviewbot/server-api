package reviewbot.review_server.service;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispatchService {
    private static final int QUEUE_CAPACITY = 256;
    private static final int CONCURRENCY = 4;

    private final GithubService githubService;
    private final Set<String> processed = ConcurrentHashMap.newKeySet();
    private final AtomicInteger queued = new AtomicInteger();
    private final Sinks.Many<WorkItem> sink = Sinks.many().unicast().onBackpressureBuffer();

    private record WorkItem(String deliveryId, byte[] rawBody) {}

    /**
     * 추후 Redis나 DB로 교체 필요함 (실행한 deliveryId인지 확인 필요하기 때문)
     */
    public boolean isDuplicate(String deliveryId) {
        return !processed.add(deliveryId);
    }

    @PostConstruct
    void startWorker() {
        sink.asFlux()
                .flatMap(this::processItem, CONCURRENCY)
                .subscribe();
    }

    public Mono<Void> dispatchPullRequest(String deliveryId, byte[] rawBody) {
        int current = queued.incrementAndGet();
        if (current > QUEUE_CAPACITY) {
            queued.decrementAndGet();
            return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "요청이 많아 잠시 후 다시 시도해주세요."));
        }

        Sinks.EmitResult result = sink.tryEmitNext(new WorkItem(deliveryId, rawBody));
        if (result.isFailure()) {
            queued.decrementAndGet();
            return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "작업 큐가 가득 찼습니다."));
        }

        return Mono.empty();
    }

    private Mono<Void> processItem(WorkItem item) {
        return githubService.reviewPullRequest(item.rawBody())
                .doOnSuccess(ignored -> log.info("[{}] 성공적으로 처리되었습니다.", item.deliveryId()))
                .doOnError(e -> {
                    log.error("[{}] 처리 중 오류가 발생하였습니다.", item.deliveryId());
                    log.error("에러 메시지 : {}", e.getMessage());
                })
                .onErrorResume(ignored -> Mono.empty())
                .doFinally(ignored -> queued.decrementAndGet());
    }
}
