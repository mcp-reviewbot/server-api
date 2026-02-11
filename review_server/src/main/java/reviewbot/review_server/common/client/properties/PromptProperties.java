package reviewbot.review_server.common.client.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client.prompt")
@Getter
@Setter
public class PromptProperties {
    /**
     * 질의에 사용되는 프롬프트
     */
    private String common;

    private String review;

    private String describe;
}
