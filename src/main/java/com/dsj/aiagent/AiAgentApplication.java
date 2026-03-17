package com.dsj.aiagent;

import com.dsj.aiagent.mcp.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiAgentApplication {

//    @Bean
//    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
//        return MethodToolCallbackProvider.builder()
//                .toolObjects(weatherService)
//                .build();
//    }

    public static void main(String[] args) {
        SpringApplication.run(AiAgentApplication.class, args);
    }

}
