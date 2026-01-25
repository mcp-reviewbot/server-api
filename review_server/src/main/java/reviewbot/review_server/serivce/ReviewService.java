package reviewbot.review_server.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reviewbot.review_server.common.client.GitHubClient;
import reviewbot.review_server.common.client.OpenAiApiClient;
import reviewbot.review_server.dto.GitHubCommentDto;
import reviewbot.review_server.dto.PullRequestWebhookDto;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private static final String REVIEW_TARGET_LABEL = "AI-REVIEW";
    private static final String REVIEW_FORMAT = """
            [PR 제목]
            %s
            
            [PR 설명]
            %s
            
            [DIFF]
            %s
            """;
    private final ObjectMapper objectMapper;
    private final GitHubClient gitHubClient;
    private final OpenAiApiClient openAiApiClient;

    public void reviewPullRequest(byte[] rawBody) throws Exception {
        PullRequestWebhookDto dto = objectMapper.readValue(rawBody, PullRequestWebhookDto.class);

        if ("Bot".equalsIgnoreCase(dto.sender().type())) {
            return;
        }

        // 라벨만 처리
        if (!"labeled".equals(dto.action())) {
            log.info("타겟 라벨이 상태가 아닙니다. 실행을 종료합니다.");
            return;
        }

        if (dto.label() == null || !StringUtils.hasText(dto.label().name()) || !REVIEW_TARGET_LABEL.equals(dto.label().name())) {
            log.info("타겟 라벨이 아닙니다. 실행을 종료합니다.");
            return;
        }

        // owner/repo/prNumber 추출
        PullRequestWebhookDto.PullRequest.Head.Repo reqRepo = dto.pull_request().head().repo();
        String owner = reqRepo.owner().login();
        String repo = reqRepo.name();
        long prNumber = dto.number();

        GitHubCommentDto.PRDiffRequest diffRequest = GitHubCommentDto.PRDiffRequest.builder()
                .owner(owner)
                .repo(repo)
                .prNumber(prNumber)
                .build();

        String diff = gitHubClient.getPullRequestDiff(diffRequest);

        // OpenAI 입력 구성
        String input = REVIEW_FORMAT.formatted(dto.pull_request().title(),
                dto.pull_request().body(),
                diff);

        String review = openAiApiClient.ask(input);

        GitHubCommentDto.IssueCommentRequest commentRequest = GitHubCommentDto.IssueCommentRequest.builder()
                .owner(owner)
                .repo(repo)
                .issueNumber(prNumber)
                .body(review)
                .build();
        gitHubClient.comment(commentRequest);
    }
}
