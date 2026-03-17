package com.dsj.aiagent.tool;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongsijun
 * @date 2026/3/5  10:43
 */
@Configuration
public class ToolRegistration {
    @Value("${search-api.api-key}")
    private String apiKey;

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(apiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        TerminateTool terminateTool = new TerminateTool();
        return ToolCallbacks.from(
                fileOperationTool,
                pdfGenerationTool,
                resourceDownloadTool,
                terminalOperationTool,
                webSearchTool,
                webScrapingTool,
                terminateTool
        );

    }
}
