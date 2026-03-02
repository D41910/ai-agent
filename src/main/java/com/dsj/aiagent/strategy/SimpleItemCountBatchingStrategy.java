package com.dsj.aiagent.strategy;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.BatchingStrategy;

import java.util.ArrayList;
import java.util.List;

public class SimpleItemCountBatchingStrategy implements BatchingStrategy {

    private final int maxItemsPerBatch;

    public SimpleItemCountBatchingStrategy(int maxItemsPerBatch) {
        this.maxItemsPerBatch = maxItemsPerBatch;
    }

    @Override
    public List<List<Document>> batch(List<Document> documents) {
        List<List<Document>> batches = new ArrayList<>();
        for (int i = 0; i < documents.size(); i += maxItemsPerBatch) {
            batches.add(documents.subList(i, Math.min(i + maxItemsPerBatch, documents.size())));
        }
        return batches;
    }
}