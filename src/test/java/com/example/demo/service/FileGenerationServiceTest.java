package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileGenerationServiceTest {

    @Mock
    private PptxGeneratorService pptxGeneratorService;

    @InjectMocks
    private FileGenerationService fileGenerationService;

    @Test
    void testGeneratePptx() throws IOException {
        // Given
        String title = "Test Title";
        String chartTitle = "Test Chart";
        String[] boxTexts = {"Box 1", "Box 2", "Box 3"};
        byte[] expectedResult = new byte[]{1, 2, 3, 4, 5};

        when(pptxGeneratorService.generatePptxFromTemplate(title, chartTitle, boxTexts))
                .thenReturn(expectedResult);

        // When
        byte[] result = fileGenerationService.generatePptx(title, chartTitle, boxTexts);

        // Then
        assertNotNull(result);
        assertArrayEquals(expectedResult, result);
        verify(pptxGeneratorService, times(1)).generatePptxFromTemplate(title, chartTitle, boxTexts);
    }
}
