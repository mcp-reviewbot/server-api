package reviewbot.review_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class OpenAiDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Request {
        private String model;
        private String prompt;
        private Integer maxOutputTokens;
        private String verbosity;
        private Double temperature;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private String model;
        private String text;
    }
}
