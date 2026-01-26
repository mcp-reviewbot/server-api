package reviewbot.review_server.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reviewbot.review_server.common.client.properties.PromptProperties;
import reviewbot.review_server.common.client.properties.GitHubProperties;
import reviewbot.review_server.common.client.properties.OpenAIProperties;

@Configuration
@EnableConfigurationProperties({PromptProperties.class, GitHubProperties.class, OpenAIProperties.class})
public class PropertiesConfig {
}