package reviewbot.review_server.clients.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "client")
public class WebClientProperties {
    /**
     * open AI 연결설정
     */
    private String openAiBaseUrl;
    private String openAiApiKey;
    private int openAiTimeoutMs;

    /**
     * gitHub 연결설정
     */

}
