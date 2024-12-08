package com.mops.bb_backend.websocket;

import com.mops.bb_backend.utils.ApplicationProperties;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ApplicationProperties applicationProperties;

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
        String userMessage = message.getPayload();

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(
            "system", "You are a helpful and concise assistant for plant care. Keep you response within 100 tokens."
        ));
        messages.add(new ChatMessage("user", userMessage));

        ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(messages)
                .stream(true)
                .build();

        OpenAiService service = new OpenAiService(applicationProperties.getOpenApiKey());
        service.streamChatCompletion(chatRequest)
                .blockingForEach(chunk -> {
                    String content = chunk.getChoices().getFirst().getMessage().getContent();
                    if (content == null)
                        return;
                    content = content.replace("**", "");

                    session.sendMessage(new TextMessage(content));
                });
    }
}
