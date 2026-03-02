package com.dsj.aiagent.rag;

import com.dsj.aiagent.strategy.SimpleItemCountBatchingStrategy;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {
    @Bean
    public BatchingStrategy customTokenCountBatchingStrategy() {
        return new SimpleItemCountBatchingStrategy(25);
//        return new TokenCountBatchingStrategy(
//            EncodingType.CL100K_BASE,  // 指定编码类型
//            8000,                      // 设置最大输入标记计数
//            0.1                        // 设置保留百分比
//        );
    }
}
