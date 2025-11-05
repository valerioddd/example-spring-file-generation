package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileGenerationService {

    private final PptxByPptxTemplateGeneratorService pptxByPptxTemplateGeneratorService;

    public FileGenerationService(PptxByPptxTemplateGeneratorService pptxByPptxTemplateGeneratorService) {
        this.pptxByPptxTemplateGeneratorService = pptxByPptxTemplateGeneratorService;
    }

    public byte[] generatePptx(String title, String chartTitle, String[] boxTexts) throws IOException {
        return generatePptx(title, chartTitle, boxTexts, null, null, null);
    }

    public byte[] generatePptx(String title, String chartTitle, String[] boxTexts,
                              Integer numberOfColumns, List<Double> barChartValues, 
                              List<Double> lineChartValues) throws IOException {
        // Build placeholder map
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{TITLE}", title);
        placeholders.put("{CHART_TITLE}", chartTitle);

        // Add box placeholders
        for (int i = 0; i < boxTexts.length && i < 3; i++) {
            placeholders.put("{BOX" + (i + 1) + "}", boxTexts[i]);
        }

        return pptxByPptxTemplateGeneratorService.generatePptxFromTemplate(
            placeholders, numberOfColumns, barChartValues, lineChartValues);
    }
}
