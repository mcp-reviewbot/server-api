package reviewbot.review_server.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reviewbot.review_server.common.client.properties.GitHubProperties;

/**
 * common.client 웹클라이언트 통신 설정 클래스
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient gitHubApiClient(GitHubProperties props) {
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getToken())
                .defaultHeader(HttpHeaders.ACCEPT, props.getDataType())
                .defaultHeader(props.getVersion(), props.getVersionDate())
                .build();
    }
}
