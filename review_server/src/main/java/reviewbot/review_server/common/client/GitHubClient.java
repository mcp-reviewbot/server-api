package reviewbot.review_server.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reviewbot.review_server.dto.GitHubCommentDto;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubClient {
    private final WebClient gitHubApiClient;

    /**
     * 기본 댓글달기 (review -> comment)
     */
    public Mono<Void> comment(GitHubCommentDto.IssueCommentRequest req) {
        return gitHubApiClient.post()
                .uri("/repos/{owner}/{repo}/issues/{issue_number}/comments",
                        req.getOwner(), req.getRepo(), req.getIssueNumber())
                .bodyValue(Map.of("body", req.getBody()))
                .retrieve()
                .toBodilessEntity()
                .then()
                .doOnError(e -> log.error("PR 코멘트 요청 중 에러 발생 : {}", e.getMessage()));
    }


    /**
     * PR 정보 가져오기
     */
    public Mono<String> getPullRequestDiff(GitHubCommentDto.PRDiffRequest req) {
        return gitHubApiClient.get()
                .uri("/repos/{owner}/{repo}/pulls/{pull_number}", req.getOwner(), req.getRepo(), req.getPrNumber())
                .header("Accept", "application/vnd.github.v3.diff")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("PR 정보 가져오기 중 에러 발생 : {}", e.getMessage()));
    }

}
