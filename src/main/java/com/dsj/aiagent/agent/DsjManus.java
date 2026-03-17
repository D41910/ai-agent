package com.dsj.aiagent.agent;

import com.dsj.aiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * @author dongsijun
 * @date 2026/3/18  04:00
 */
@Component
public class DsjManus extends ToolCallAgent {

    public DsjManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        setName("DsjManus");
        String SYSTEM_PROMPT = "You are DsjManus, an all-capable AI assistant, aimed at solving any task presented by the user." +
                "You have various tools at your disposal that you can call upon to efficiently complete complex requests.";
        setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = "Based on user needs, proactively select the most appropriate tool or combination of tools." +
                "For complex tasks, you can break down the problem and use different tools step by step to solve it." +
                "After using each tool, clearly explain the execution results and suggest the next steps." +
                "If you want to stop the interaction at any point, use the `terminate` tool/function call.";
        setNextSepPrompt(NEXT_STEP_PROMPT);
        setMaxSteps(20);
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        setChatClient(chatClient);
    }
}

