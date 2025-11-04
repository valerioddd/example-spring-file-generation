package com.example.demo.controller;

import com.example.demo.service.FileGenerationService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class PptxController {

    private final FileGenerationService fileGenerationService;

    public PptxController(FileGenerationService fileGenerationService) {
        this.fileGenerationService = fileGenerationService;
    }

    @PostMapping(value = "/generate-pptx", consumes = "application/json")
    public ResponseEntity<byte[]> generatePptx(@RequestBody PptxRequest request) {
        try {
            // apply defaults when request fields are null
            String title = (request.getTitle() == null || request.getTitle().isBlank()) ? "Presentazione di Esempio" : request.getTitle();
            String chartTitle = (request.getChartTitle() == null || request.getChartTitle().isBlank()) ? "Dati e Grafici" : request.getChartTitle();
            List<String> boxesList = request.getBoxTexts();
            String[] boxes;
            if (boxesList == null || boxesList.isEmpty()) {
                boxes = new String[]{"Box 1", "Box 2", "Box 3"};
            } else {
                boxes = boxesList.toArray(new String[0]);
            }

            byte[] pptxData = fileGenerationService.generatePptx(title, chartTitle, boxes);

            HttpHeaders headers = new HttpHeaders();
            // Use generic binary stream; client will download because of attachment disposition
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("presentation.pptx").build());
            headers.setContentLength(pptxData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pptxData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
