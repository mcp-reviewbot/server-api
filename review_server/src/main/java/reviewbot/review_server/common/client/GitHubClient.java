package reviewbot.review_server.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
    public void comment(GitHubCommentDto.IssueCommentRequest req) {
        try {
            gitHubApiClient.post()
                    .uri("/repos/{owner}/{repo}/issues/{issue_number}/comments",
                            req.getOwner(), req.getRepo(), req.getIssueNumber())
                    .bodyValue(Map.of("body", req.getBody()))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("PR 코멘트 요청 중 에러 발생 : {}", e.getMessage());
            throw e;
        }
    }


    /**
     * PR 정보 가져오기
     */
    public String
    getPullRequestDiff(GitHubCommentDto.PRDiffRequest req) {
        try {
            return gitHubApiClient.get()
                    .uri("/repos/{owner}/{repo}/pulls/{pull_number}", req.getOwner(), req.getRepo(), req.getPrNumber())
                    .header("Accept", "application/vnd.github.v3.diff")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("PR 정보 가져오기 중 에러 발생 : {}", e.getMessage());
            throw e;
        }
    }

}
