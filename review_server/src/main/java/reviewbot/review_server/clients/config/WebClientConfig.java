package reviewbot.review_server.clients.config;

import io.netty.channel.ChannelOption;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reviewbot.review_server.clients.properties.WebClientProperties;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient openAiWebClient(WebClientProperties props) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getOpenAiTimeoutMs())
                .responseTimeout(Duration.ofMillis(props.getOpenAiTimeoutMs()));

        return WebClient.builder()
                .baseUrl(props.getOpenAiBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getOpenAiApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

