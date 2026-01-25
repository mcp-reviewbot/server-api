package reviewbot.review_server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PullRequestWebhookDto(
        String action,
        long number,
        Label label,
        PullRequest pull_request,
        Repository repository,
        Sender sender
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Label(
            String name,
            String color,
            String description
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PullRequest(
            String title,
            String body,
            Head head,
            Base base,
            List<Label> labels
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Head(String sha, Repo repo) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Repo(String full_name, Owner owner, String name) {
                @JsonIgnoreProperties(ignoreUnknown = true)
                public record Owner(String login) {
                }
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Base(String ref) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(String full_name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sender(String login, String type) {
    }
}
