package reviewbot.review_server.clients.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reviewbot.review_server.clients.properties.WebClientProperties;

@Configuration
@EnableConfigurationProperties(WebClientProperties.class)
public class PropertiesConfig {
}