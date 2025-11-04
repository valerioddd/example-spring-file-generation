package com.example.demo.controller;

import com.example.demo.service.FileGenerationService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class PptxController {

    private final FileGenerationService fileGenerationService;

    public PptxController(FileGenerationService fileGenerationService) {
        this.fileGenerationService = fileGenerationService;
    }

    @PostMapping("/generate-pptx")
    public ResponseEntity<byte[]> generatePptx(
            @RequestParam(defaultValue = "Presentazione di Esempio") String title,
            @RequestParam(defaultValue = "Dati e Grafici") String chartTitle,
            @RequestParam(defaultValue = "Box 1,Box 2,Box 3") String boxTexts) {
        
        try {
            String[] boxes = boxTexts.split(",");
            byte[] pptxData = fileGenerationService.generatePptx(title, chartTitle, boxes);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("presentation.pptx").build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pptxData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
