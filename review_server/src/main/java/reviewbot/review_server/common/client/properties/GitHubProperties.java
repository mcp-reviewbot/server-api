package reviewbot.review_server.common.client.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client.github")
@Getter
@Setter
public class GitHubProperties {
    /**
     * gitHub 설정
     */
    private String baseUrl;
    private String token;
    private String dataType;
    private String version;
    private String versionDate;

    private String webHookSecret;
}
