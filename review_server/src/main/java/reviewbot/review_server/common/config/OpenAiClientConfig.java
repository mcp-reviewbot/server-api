package reviewbot.review_server.common.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reviewbot.review_server.common.client.properties.OpenAIProperties;

@Configuration
public class OpenAiClientConfig {

    @Bean
    public OpenAIClient openAIClient(OpenAIProperties props) {
        return OpenAIOkHttpClient.builder()
                .baseUrl(props.getBaseUrl())
                .apiKey(props.getApiKey())
                .build();
    }
}
