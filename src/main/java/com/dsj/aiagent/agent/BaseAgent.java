package com.dsj.aiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.dsj.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongsijun
 * @date 2026/3/17  14:15
 * 抽闲基础代理类,用于管理代理状态和执行流程
 * <p>
 * 提供状态转移、内存管理和基于步骤的执行循环的基础功能
 * 子类必须实现step方法
 */

@Data
@Slf4j
public abstract class BaseAgent {

    /**
     * 核心属性
     */
    private String name;

    /**
     * 提示
     */
    private String systemPrompt;
    private String nextSepPrompt;

    /**
     * 状态
     */
    private AgentState state = AgentState.IDLE;

    /**
     * 执行控制
     */
    private int maxSteps = 10;
    private int currentStep = 0;

    /**
     * LLM
     */
    private ChatClient chatClient;

    /**
     * Memory
     */
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        if (state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state:" + state);
        }
        if (StrUtil.isEmpty(userPrompt)) {
            throw new RuntimeException("UserPrompt cannot be empty");
        }
        //更改状态
        state = AgentState.RUNNING;
        //记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        //保存结果列表
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxSteps);
                //单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ":" + stepResult;
                results.add(result);
            }
            //检查是否超出步骤限制
            if (currentStep == maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated:Reached max steps(" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent ", e);
            return "执行错误" + e.getMessage();
        } finally {
            //清理资源
            this.cleanup();
        }
    }

    /**
     * 执行单个步骤
     *
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
    }
}
