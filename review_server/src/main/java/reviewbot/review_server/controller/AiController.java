package reviewbot.review_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reviewbot.review_server.clients.OpenAiClient;
import reviewbot.review_server.dto.OpenAiDto;
import tools.jackson.databind.JsonNode;

/**
 * openAiClient 테스트용 컨트롤러
 */
@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiController {
    private final OpenAiClient openAiWebClient;

    @GetMapping("/models")
    public JsonNode models() {
        return openAiWebClient.listModels();
    }

    @PostMapping("/generate")
    public OpenAiDto.Response generate(@RequestBody OpenAiDto.Request request) {
        String model = StringUtils.hasText(request.getModel()) ? request.getModel() : "gpt-5-mini";
        int maxTokens = (request.getMaxOutputTokens() == null) ? 300 : request.getMaxOutputTokens();

        String text = openAiWebClient.generateText(model, request.getPrompt(), maxTokens);
        return OpenAiDto.Response.builder()
                .model(model)
                .text(text)
                .build();
    }
}
