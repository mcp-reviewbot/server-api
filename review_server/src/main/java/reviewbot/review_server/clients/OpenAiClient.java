package reviewbot.review_server.clients;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient openAiWebClient;

    /**
     * OpenAI 정보 조회: 모델 목록 (동기 반환)
     */
    public JsonNode listModels() {
        return openAiWebClient.get()
                .uri("/v1/models")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /**
     * Responses API로 텍스트 생성 (동기 반환)
     */
    public String generateText(String model, String prompt, int maxOutputTokens) {
        ResponsesCreateRequest req = new ResponsesCreateRequest(model, prompt, maxOutputTokens);

        JsonNode root = openAiWebClient.post()
                .uri("/v1/responses")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnNext(r -> System.out.println("[openai raw] " + r.toPrettyString()))
                .block();

        if (root == null) return "";
        return extractOutputText(root);
    }


    /**
     * Responses API 요청 DTO
     */
    private record ResponsesCreateRequest(
            String model,
            String input,
            Integer max_output_tokens
    ) {
    }

    /**
     * Responses 응답에서 output_text 추출
     */
    private static String extractOutputText(JsonNode root) {
        if (root == null) return "";

        // 혹시 output_text 직빵으로 있으면 우선 사용
        JsonNode outputText = root.get("output_text");
        if (outputText != null && outputText.isTextual() && !outputText.asText().isBlank()) {
            return outputText.asText().trim();
        }

        JsonNode output = root.get("output");
        if (output == null || !output.isArray()) return "";

        List<String> parts = new ArrayList<>();

        for (JsonNode item : output) {
            String type = item.path("type").asText("");
            String role = item.path("role").asText("");
            if (!"message".equals(type) || !"assistant".equals(role)) continue;

            JsonNode content = item.get("content");
            if (content == null || !content.isArray()) continue;

            for (JsonNode c : content) {
                if ("output_text".equals(c.path("type").asText(""))) {
                    String text = c.path("text").asText("");
                    if (!text.isBlank()) parts.add(text);
                }
            }
        }

        return String.join("\n", parts).trim();
    }
}
