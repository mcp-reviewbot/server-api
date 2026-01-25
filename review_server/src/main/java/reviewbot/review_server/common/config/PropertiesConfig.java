package reviewbot.review_server.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reviewbot.review_server.common.client.properties.LlmProperties;
import reviewbot.review_server.common.client.properties.GitHubProperties;
import reviewbot.review_server.common.client.properties.OpenAIProperties;

@Configuration
@EnableConfigurationProperties({LlmProperties.class, GitHubProperties.class, OpenAIProperties.class})
public class PropertiesConfig {
}