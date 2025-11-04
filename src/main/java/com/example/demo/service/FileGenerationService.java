package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FileGenerationService {

    private final PptxGeneratorService pptxGeneratorService;

    public FileGenerationService(PptxGeneratorService pptxGeneratorService) {
        this.pptxGeneratorService = pptxGeneratorService;
    }

    public byte[] generatePptx(String title, String chartTitle, String[] boxTexts) throws IOException {
        return pptxGeneratorService.generatePptxFromTemplate(title, chartTitle, boxTexts);
    }
}
