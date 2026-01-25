package reviewbot.review_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GitHubCommentDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueCommentRequest {
        private String owner;
        private String repo;
        private long issueNumber;
        private String body;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PRDiffRequest {
        private String owner;
        private String repo;
        private long prNumber;
    }
}
