package com.kevin.springai.rag.observability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranslationRequest {
    private String text;
    private String sourceLanguage;
    private String targetLanguage;
}

