package com.dsj.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyKeywordEnricher {

    @Resource
    private  ChatModel dashscopeChatModel;

    MyKeywordEnricher(ChatModel chatModel) {
        this.dashscopeChatModel = chatModel;
    }

    List<Document> enrichDocuments(List<Document> documents,int keywordCount) {
        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(dashscopeChatModel)
                .keywordCount(keywordCount)
                .build();
        return enricher.apply(documents);
    }

    List<Document> enrichDocuments(List<Document> documents,int keywordCount,String language) {
        String promptTemplateStr = "请严格按照以下要求提取关键词： 1. 从文档内容中提取{keywordCount}个{language}关键词，禁止使用其他语言； 2. 关键词需贴合文档核心内容，具备代表性； 3. 仅返回关键词，用中文逗号分隔，无其他多余文字。 文档内容：{context_str}";
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateStr);
        promptTemplate.add("keywordCount", keywordCount);
        promptTemplate.add("language", language);
        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(dashscopeChatModel)
                .keywordsTemplate(promptTemplate)
                .keywordCount(keywordCount)
                .build();
        return enricher.apply(documents);
    }
}
