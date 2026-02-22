package reviewbot.review_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reviewbot.review_server.common.client.GitHubClient;
import reviewbot.review_server.common.client.OpenAiApiClient;
import reviewbot.review_server.dto.GitHubCommentDto;
import reviewbot.review_server.dto.PullRequestWebhookDto;
import reviewbot.review_server.dto.ReviewType;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubService {
    private static final String REVIEW_TARGET_LABEL = "AI-REVIEW";
    private static final String FAILURE_COMMENT = "리뷰 작업이 실패했습니다! 관리자에게 문의해주세요";
    private static final String REVIEW_FORMAT = """
            [모드] %s
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

    public Mono<Void> reviewPullRequest(byte[] rawBody) {
        return Mono.fromCallable(() -> objectMapper.readValue(rawBody, PullRequestWebhookDto.class))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::handleWebhook);
    }

    private Mono<Void> handleWebhook(PullRequestWebhookDto dto) {
        if ("Bot".equalsIgnoreCase(dto.sender().type())) {
            return Mono.empty();
        }

        // 라벨만 처리
        if (!"labeled".equals(dto.action())) {
            log.info("타겟 라벨이 상태가 아닙니다. 실행을 종료합니다.");
            return Mono.empty();
        }

        if (dto.label() == null
                || !StringUtils.hasText(dto.label().name())
                || !REVIEW_TARGET_LABEL.equals(dto.label().name())) {
            log.info("타겟 라벨이 아닙니다. 실행을 종료합니다.");
            return Mono.empty();
        }

        // owner/repo/prNumber 추출
        PullRequestWebhookDto.PullRequest.Head.Repo reqRepo = dto.pull_request().head().repo();
        String owner = reqRepo.owner().login();
        String repo = reqRepo.name();
        long prNumber = dto.number();
        ReviewContext context = new ReviewContext(
                owner, repo, prNumber,
                dto.pull_request().title(),
                dto.pull_request().body());

        GitHubCommentDto.PRDiffRequest diffRequest = GitHubCommentDto.PRDiffRequest.builder()
                .owner(owner)
                .repo(repo)
                .prNumber(prNumber)
                .build();

        return gitHubClient.getPullRequestDiff(diffRequest)
                .flatMap(diff -> {
                    String describeInput = REVIEW_FORMAT.formatted(
                            ReviewType.DESCRIBE, context.title(),
                            context.explain(), diff);
                    Mono<String> describeAskResult =
                            openAiApiClient.ask(describeInput, ReviewType.DESCRIBE);

                    String reviewInput = REVIEW_FORMAT.formatted(
                            ReviewType.REVIEW, context.title(),
                            context.explain(), diff);
                    Mono<String> reviewAskResult =
                            openAiApiClient.ask(reviewInput, ReviewType.REVIEW);

                    return Mono.zip(describeAskResult, reviewAskResult)
                            .flatMap(results -> postComment(
                                    context.owner(), context.repo(),
                                    context.prNumber(), results.getT1())
                                    .then(postComment(
                                            context.owner(), context.repo(),
                                            context.prNumber(),
                                            results.getT2())));
                })
                .onErrorResume(error -> {
                    log.error("[{}/{}/{}] 리뷰 처리 실패: {}",
                            context.owner(), context.repo(),
                            context.prNumber(), error.getMessage());
                    return postFailureComment(context)
                            .onErrorResume(inner -> {
                                log.error(
                                        "[{}/{}/{}] 실패 코멘트 등록 실패: {}",
                                        context.owner(), context.repo(),
                                        context.prNumber(),
                                        inner.getMessage());
                                return Mono.empty();
                            });
                });
    }

    private Mono<Void> postFailureComment(ReviewContext context) {
        return postComment(context.owner(), context.repo(), context.prNumber(), FAILURE_COMMENT);
    }

    private Mono<Void> postComment(String owner, String repo, long issueNumber, String body) {
        GitHubCommentDto.IssueCommentRequest commentRequest = GitHubCommentDto.IssueCommentRequest.builder()
                .owner(owner)
                .repo(repo)
                .issueNumber(issueNumber)
                .body(body)
                .build();

        return gitHubClient.comment(commentRequest);
    }

    private record ReviewContext(String owner, String repo, long prNumber, String title, String explain) {}
}
