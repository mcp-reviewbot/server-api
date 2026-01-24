package reviewbot.review_server.common.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reviewbot.review_server.dto.GitHubCommentDto;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GitHubClient {
    private final WebClient gitHubApiClient;

    /**
     * 기본 댓글달기 (review -> comment)
     */
    public void comment(GitHubCommentDto.IssueCommentRequest req) {
        gitHubApiClient.post()
                .uri("/repos/{owner}/{repo}/issues/{issue_number}/comments",
                        req.getOwner(), req.getRepo(), req.getIssueNumber())
                .bodyValue(Map.of("body", req.getBody()))
                .retrieve()
                .toBodilessEntity()
                .block();
    }


    /**
     * PR 정보 가져오기
     */
    public String getPullRequestDiff(GitHubCommentDto.PRDiffRequest req) {
        return gitHubApiClient.get()
                .uri("/repos/{owner}/{repo}/pulls/{pull_number}", req.getOwner(), req.getRepo(), req.getPrNumber())
                .header("Accept", "application/vnd.github.v3.diff")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
