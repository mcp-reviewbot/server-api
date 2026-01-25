package reviewbot.review_server.common.client.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "client.open-ai")
public class OpenAIProperties {
    /**
     * open AI 연결설정
     */
    private String baseUrl;
    private String apiKey;
    private String model;
    private int maxToken;
}
