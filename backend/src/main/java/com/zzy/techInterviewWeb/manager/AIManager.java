package com.zzy.techInterviewWeb.manager;


import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
public class AIManager {

    private final ChatModel chatModel;

    @Autowired
    public AIManager(ChatModel chatModel) {
       this.chatModel=chatModel;
    }

    public String ask(String SystemPrompt, String UserPrompt) {
        Message userMessage = new UserMessage(UserPrompt);

        Message systemMessage = new SystemMessage(SystemPrompt);
        Prompt prompt = new Prompt(Lists.newArrayList(userMessage, systemMessage));
        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}