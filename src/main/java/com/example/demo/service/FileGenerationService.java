package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileGenerationService {

    private final PptxGeneratorService pptxGeneratorService;

    public FileGenerationService(PptxGeneratorService pptxGeneratorService) {
        this.pptxGeneratorService = pptxGeneratorService;
    }

    public byte[] generatePptx(String title, String chartTitle, String[] boxTexts) throws IOException {
        // Build placeholder map
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{TITLE}", title);
        placeholders.put("{CHART_TITLE}", chartTitle);
        
        // Add box placeholders
        for (int i = 0; i < boxTexts.length && i < 3; i++) {
            placeholders.put("{BOX" + (i + 1) + "}", boxTexts[i]);
        }
        
        return pptxGeneratorService.generatePptxFromTemplate(placeholders);
    }
}
