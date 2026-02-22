package reviewbot.review_server.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reviewbot.review_server.common.client.properties.GitHubProperties;
import reviewbot.review_server.common.client.properties.OpenAIProperties;
import reviewbot.review_server.common.client.properties.PromptProperties;

@Configuration
@EnableConfigurationProperties({
        GitHubProperties.class,
        OpenAIProperties.class,
        PromptProperties.class
})
public class PropertiesConfig {
}