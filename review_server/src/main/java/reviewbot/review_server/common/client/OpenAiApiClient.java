package reviewbot.review_server.common.client;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reviewbot.review_server.common.client.properties.OpenAIProperties;
import reviewbot.review_server.common.client.properties.PromptProperties;
import reviewbot.review_server.dto.ReviewType;

@Component
@RequiredArgsConstructor
public class OpenAiApiClient {
    private final OpenAIClient openAIClient;
    private final OpenAIProperties openAIProps;
    private final PromptProperties promptProps;

    /**
     * ai 서버에 질의 날리기
     * - model : LLM 모델 ex) gpt-5-mini
     * - instructions : 지시사항 (프롬프트)
     * - maxOutputTokens : 응답 길이 제한
     */
    public Mono<String> ask(String input) {
        return Mono.fromCallable(() -> {
                    ResponseCreateParams.Builder params = ResponseCreateParams.builder()
                            .input(input)
                            .model(openAIProps.getModel())
                            .instructions(promptProps.getCommon())
                            .maxOutputTokens(openAIProps.getMaxToken());

                    Response response = openAIClient.responses().create(params.build());

                    return extractOutputText(response)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Open AI 응답값 분리 도중 오류가 발생하였습니다."));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> ask(String input, ReviewType reviewType) {
        return Mono.fromCallable(() -> {
                    String prompt = promptProps.getCommon();
                    prompt += reviewType == ReviewType.REVIEW ? promptProps.getReview() : promptProps.getDescribe();

                    ResponseCreateParams.Builder params = ResponseCreateParams.builder()
                            .input(input)
                            .model(openAIProps.getModel())
                            .instructions(prompt)
                            .maxOutputTokens(openAIProps.getMaxToken());

                    Response response = openAIClient.responses().create(params.build());

                    return extractOutputText(response)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Open AI 응답값 분리 도중 오류가 발생하였습니다."));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * LLM 호출 값 중 최종 텍스트(응답 값)만 추출
     */
    private Optional<String> extractOutputText(Response response) {
        String text = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(ResponseOutputText::text)
                .collect(Collectors.joining());

        if (!StringUtils.hasText(text)) {
            return Optional.empty();
        }
        return Optional.of(text);
    }
}
