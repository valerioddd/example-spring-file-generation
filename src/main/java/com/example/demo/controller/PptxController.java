package com.example.demo.controller;

import com.example.demo.repository.HtmlRepository;
import com.example.demo.service.FileGenerationService;
import com.example.demo.service.HtmlToPptxGeneratorService;
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
    private final HtmlToPptxGeneratorService htmlToPptxGeneratorService;
    private final HtmlRepository htmlRepository;

    public PptxController(FileGenerationService fileGenerationService, 
                         HtmlToPptxGeneratorService htmlToPptxGeneratorService,
                         HtmlRepository htmlRepository) {
        this.fileGenerationService = fileGenerationService;
        this.htmlToPptxGeneratorService = htmlToPptxGeneratorService;
        this.htmlRepository = htmlRepository;
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

    @PostMapping(value = "/generate-pptx-from-html", consumes = "application/json")
    public ResponseEntity<byte[]> generatePptxFromHtml(@RequestBody HtmlRequest request) {
        try {
            // Get template ID (default to "default" if not provided)
            String templateId = request.getTemplateId();
            if (templateId == null || templateId.isBlank()) {
                templateId = "default";
            }

            // Get HTML template from repository
            String htmlTemplate = htmlRepository.getTemplate(templateId);
            if (htmlTemplate == null) {
                return ResponseEntity.notFound().build();
            }

            // Generate PPTX with placeholders
            byte[] pptxData = htmlToPptxGeneratorService.generatePptxFromHtml(
                htmlTemplate, 
                request.getPlaceholders()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("presentation-from-html.pptx").build());
            headers.setContentLength(pptxData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pptxData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
