package com.yachiyo.service.Impl;

import com.yachiyo.result.Result;
import com.yachiyo.service.RAGResourceService;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class RAGResourceServiceImpl implements RAGResourceService {

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Autowired
    private VectorStore vectorStore;

    @Override
    public Result<Boolean> uploadResource(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            Resource resource = file.getResource();
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            List<Document> documents = tikaDocumentReader.read();
            List<Document> result = getDocuments(documents);
            List<Document> texts = tokenTextSplitter.apply(result);
            vectorStore.add(texts);
        }
        return Result.success(true);
    }

    private static @NonNull List<Document> getDocuments(List<Document> documents) {
        List<Document> result = new ArrayList<>();

        for (Document document : documents) {
            String text = document.getText();
            // 1. 按中文句号预分割 或者 连续多个空行
            String[] sentences = null;
            if (text != null) {
                sentences = text.split("(?<=[。！？])|\\n{2,}");
            }

            // 2. 对每个句子单独处理（或合并后由 TokenTextSplitter 处理）
            for (String sentence : sentences) {
                if (!sentence.trim().isEmpty()) {
                    Document sentenceDoc = new Document(sentence, document.getMetadata());
                    result.add(sentenceDoc);
                }
            }
        }
        return result;
    }
}
