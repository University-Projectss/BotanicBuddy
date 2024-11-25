package com.mops.bb_backend.service;

import com.mops.bb_backend.utils.ApplicationProperties;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CareRecommendationService {

    private final ApplicationProperties applicationProperties;
    private final static String SETUP_PROMPT =
         "You are a plant care expert. Provide practical and concise recommendations for taking care of the plant %s. Include:" +
         "- Watering frequency\n" +
         "- Optimal amount of light required\n" +
         "- Soil type and fertilizers\n" +
         "- Seasonal tips\n" +
         "- Additional information (e.g., diseases, pests)\n" +
         "The response must be in JSON format with the following structure (do not add extra keys): " +
         "{'recommendation': '...', 'watering_frequency': '...', 'soil': '...', 'light': '...', 'temperature': '...'}, where " +
         "'watering_frequency' - integer indicating how many days should pass between watering;" +
         "'light' - optimal lighting conditions, such as 'full sun', 'partial shade', or 'indirect light';" +
         "'soil' - ideal soil type (e.g., 'well-draining potting mix', 'sandy soil')" +
         "'temperature' - optimal temperature range for the plant in °C;" +
         "'recommendation' - a brief care recommendation (do not repeat information from other keys)";

    public Optional<String> detectCareRecommendations(String scientificName) {
        OpenAiService service = new OpenAiService(applicationProperties.getOpenApiKey());

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(buildModelSetupMessages(scientificName))
                .build();

        try {
            ChatCompletionResult result = service.createChatCompletion(completionRequest);
            String response = result.getChoices().getFirst().getMessage().getContent();
            response = response.replace("```json", "").replace("```", "").trim();
            return Optional.of(response);
        } catch (OpenAiHttpException e) {
            return Optional.empty();
        }
    }

    private List<ChatMessage> buildModelSetupMessages(String scientificName) {
        ArrayList<ChatMessage> systemMessages = new ArrayList<>();
        systemMessages.add(new ChatMessage(ChatMessageRole.USER.value(), String.format(SETUP_PROMPT, scientificName)));
        return systemMessages;
    }
}
