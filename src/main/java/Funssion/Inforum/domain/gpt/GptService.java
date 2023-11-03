package Funssion.Inforum.domain.gpt;

import Funssion.Inforum.domain.professionalprofile.repository.ProfessionalProfileRepository;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.theokanning.openai.completion.chat.ChatMessageRole.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptService {
    @Value("${chatgpt.api-key}")
    private String GPT_API_KEY;
    private OpenAiService openAiService;
    private final ProfessionalProfileRepository professionalProfileRepository;

    @Async("threadPoolTaskExecutor")
    public void getDescriptionByGPTAndUpdateDescription(Long userId, List<String> answerList) {
        openAiService = new OpenAiService(GPT_API_KEY);

        String userPrompt = getPromptMessageByIntegratingList(answerList);

        List<ChatMessage> messages = new ArrayList<>();
        setPrompt(userPrompt, messages);

        ChatCompletionRequest chatCompletionRequest = getChatCompletionRequest(messages);

        StringBuilder chatResponse = getChatGptResponse(chatCompletionRequest);

        professionalProfileRepository.updateDescription(userId, chatResponse.toString());
    }

    private static void setPrompt(String userPrompt, List<ChatMessage> messages) {
        ChatMessage systemMessage = new ChatMessage(SYSTEM.value(),
                "You are a Answer Summarizer " +
                        "Your task is summarize three answers to maximum two sentences only Korean. " +
                        "Each answer parted by '----------------'." +
                        "The questions are as follows." +
                        "Question 1 : Feel free to describe your project experience." +
                        "Question 2 : Please describe your experience of solving a difficult technical problem." +
                        "Question 3 : Please describe your experience in resolving conflicts or problems experienced during collaboration." +
                        "--------------------------------" +
                        "Your response format is as follows." +
                        "프로젝트 경험: answer1" +
                        "어려운 기술적 문제 해결 경험: answer2" +
                        "협업 중 발생한 문제 해결 경험: answer3");
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage(USER.value(), userPrompt);
        messages.add(userMessage);
    }

    private static ChatCompletionRequest getChatCompletionRequest(List<ChatMessage> messages) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .topP(1.0)
                .maxTokens(500)
                .logitBias(new HashMap<>())
                .build();
        return chatCompletionRequest;
    }

    @NotNull
    private StringBuilder getChatGptResponse(ChatCompletionRequest chatCompletionRequest) {
        StringBuilder chatResponse = new StringBuilder();

        openAiService.streamChatCompletion(chatCompletionRequest)
                .doOnError((error) -> log.error("Chat GPT API occurs error", error))
                .blockingForEach(chatCompletionChunk -> {
                    String content = chatCompletionChunk.getChoices().get(0).getMessage().getContent();
                    if (Objects.nonNull(content))
                        chatResponse.append(content);
                });
        return chatResponse;
    }

    private String getPromptMessageByIntegratingList(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringList) {
            stringBuilder.append("-------------------------------");
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
