package reviewbot.review_server.common.client.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "client.prompt")
public class PromptProperties {
    /**
     * 질의에 사용되는 프롬프트
     */
    private String common;

    private String review;

    private String describe;
}
