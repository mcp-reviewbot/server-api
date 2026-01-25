package reviewbot.review_server.common.client.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "client")
public class LlmProperties {
    /**
     * 공용 프롬프트
     */
    private String prompt;
}
