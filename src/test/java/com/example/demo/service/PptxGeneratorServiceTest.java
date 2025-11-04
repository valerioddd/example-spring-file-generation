package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PptxGeneratorServiceTest {

    @Autowired
    private PptxGeneratorService pptxGeneratorService;

    @Test
    void testGeneratePptxFromTemplate() throws IOException {
        // Given
        String title = "Test Presentation";
        String chartTitle = "Test Chart";
        String[] boxTexts = {"Box 1 Content", "Box 2 Content", "Box 3 Content"};

        // When
        byte[] result = pptxGeneratorService.generatePptxFromTemplate(title, chartTitle, boxTexts);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        // PPTX files start with PK (ZIP signature)
        assertEquals('P', (char) result[0]);
        assertEquals('K', (char) result[1]);
    }

    @Test
    void testGeneratePptxWithDifferentContent() throws IOException {
        // Given
        String title = "Another Title";
        String chartTitle = "Sales Data";
        String[] boxTexts = {"Q1", "Q2", "Q3"};

        // When
        byte[] result = pptxGeneratorService.generatePptxFromTemplate(title, chartTitle, boxTexts);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGeneratePptxWithPlaceholderMap() throws IOException {
        // Given
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{TITLE}", "Dynamic Title");
        placeholders.put("{CHART_TITLE}", "Dynamic Chart");
        placeholders.put("{BOX1}", "First Box");
        placeholders.put("{BOX2}", "Second Box");
        placeholders.put("{BOX3}", "Third Box");

        // When
        byte[] result = pptxGeneratorService.generatePptxFromTemplate(placeholders);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals('P', (char) result[0]);
        assertEquals('K', (char) result[1]);
    }

    @Test
    void testGeneratePptxWithSamePlaceholderMultipleTimes() throws IOException {
        // Given - same placeholder can appear multiple times in template
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{TITLE}", "Repeated Title");
        placeholders.put("{CHART_TITLE}", "Chart Info");
        placeholders.put("{BOX1}", "Data 1");
        placeholders.put("{BOX2}", "Data 2");
        placeholders.put("{BOX3}", "Data 3");

        // When
        byte[] result = pptxGeneratorService.generatePptxFromTemplate(placeholders);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
