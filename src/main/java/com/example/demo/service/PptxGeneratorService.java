package com.example.demo.service;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class PptxGeneratorService {

    public byte[] generatePptxFromTemplate(String title, String chartTitle, String[] boxTexts) throws IOException {
        // Build placeholder map
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{TITLE}", title);
        placeholders.put("{CHART_TITLE}", chartTitle);
        
        // Add box placeholders
        for (int i = 0; i < boxTexts.length && i < 3; i++) {
            placeholders.put("{BOX" + (i + 1) + "}", boxTexts[i]);
        }
        
        return generatePptxFromTemplate(placeholders);
    }

    public byte[] generatePptxFromTemplate(Map<String, String> placeholders) throws IOException {
        // Load template from resources
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream("templates/presentation-template.pptx")) {
            if (templateStream == null) {
                throw new IOException("Template file not found");
            }

            XMLSlideShow ppt = new XMLSlideShow(templateStream);
            
            // Process all slides and replace placeholders dynamically
            for (XSLFSlide slide : ppt.getSlides()) {
                replacePlaceholdersInSlide(slide, placeholders);
            }

            // Write to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ppt.write(out);
            ppt.close();
            return out.toByteArray();
        }
    }

    private void replacePlaceholdersInSlide(XSLFSlide slide, Map<String, String> placeholders) {
        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                replacePlaceholdersInTextShape((XSLFTextShape) shape, placeholders);
            } else if (shape instanceof XSLFGroupShape) {
                // Handle grouped shapes recursively
                replacePlaceholdersInGroup((XSLFGroupShape) shape, placeholders);
            }
        }
    }

    private void replacePlaceholdersInGroup(XSLFGroupShape group, Map<String, String> placeholders) {
        for (XSLFShape shape : group.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                replacePlaceholdersInTextShape((XSLFTextShape) shape, placeholders);
            } else if (shape instanceof XSLFGroupShape) {
                replacePlaceholdersInGroup((XSLFGroupShape) shape, placeholders);
            }
        }
    }

    private void replacePlaceholdersInTextShape(XSLFTextShape textShape, Map<String, String> placeholders) {
        // Iterate through all paragraphs in the text shape
        for (XSLFTextParagraph paragraph : textShape.getTextParagraphs()) {
            // Iterate through all text runs in the paragraph
            for (XSLFTextRun run : paragraph.getTextRuns()) {
                String text = run.getRawText();
                if (text != null) {
                    // Replace all placeholders in this text run
                    String replacedText = text;
                    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                        replacedText = replacedText.replace(entry.getKey(), entry.getValue());
                    }
                    // Only update if text changed, preserving formatting
                    if (!text.equals(replacedText)) {
                        run.setText(replacedText);
                    }
                }
            }
        }
    }
}
