package com.example.demo.controller;

import com.example.demo.service.FileGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PptxController.class)
class PptxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileGenerationService fileGenerationService;

    @Test
    void testGeneratePptxWithDefaultParameters() throws Exception {
        // Given
        byte[] mockPptxData = new byte[]{1, 2, 3, 4, 5};
        when(fileGenerationService.generatePptx(any(), any(), any())).thenReturn(mockPptxData);

        // When & Then
        mockMvc.perform(post("/api/files/generate-pptx"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"presentation.pptx\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(mockPptxData));
    }

    @Test
    void testGeneratePptxWithCustomParameters() throws Exception {
        // Given
        String title = "Custom Title";
        String chartTitle = "Custom Chart";
        String boxTexts = "Custom Box 1,Custom Box 2,Custom Box 3";
        byte[] mockPptxData = new byte[]{5, 4, 3, 2, 1};
        
        when(fileGenerationService.generatePptx(eq(title), eq(chartTitle), any())).thenReturn(mockPptxData);

        // When & Then
        mockMvc.perform(post("/api/files/generate-pptx")
                        .param("title", title)
                        .param("chartTitle", chartTitle)
                        .param("boxTexts", boxTexts))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(mockPptxData));
    }

    @Test
    void testGeneratePptxHandlesException() throws Exception {
        // Given
        when(fileGenerationService.generatePptx(any(), any(), any()))
                .thenThrow(new RuntimeException("Test exception"));

        // When & Then
        mockMvc.perform(post("/api/files/generate-pptx"))
                .andExpect(status().isInternalServerError());
    }
}
