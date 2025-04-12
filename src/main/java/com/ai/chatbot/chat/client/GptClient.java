package com.ai.chatbot.chat.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GptClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();

    @Value("${gpt.api.key}")
    private String apiKey;

    public String getAnswerFromGpt(List<Map<String, String>> messages, String model) {
        Map<String, Object> request = Map.of(
                "model", model,
                "messages", messages
        );

        return webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                })
                .block();
    }
}
