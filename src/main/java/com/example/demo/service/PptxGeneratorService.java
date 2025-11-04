package com.example.demo.service;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PptxGeneratorService {

    public byte[] generatePptxFromTemplate(String title, String chartTitle, String[] boxTexts) throws IOException {
        // Load template from resources
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream("templates/presentation-template.pptx")) {
            if (templateStream == null) {
                throw new IOException("Template file not found");
            }

            XMLSlideShow ppt = new XMLSlideShow(templateStream);
            
            // Process slide 1 - Cover slide with title placeholder
            if (ppt.getSlides().size() > 0) {
                XSLFSlide slide1 = ppt.getSlides().get(0);
                for (XSLFShape shape : slide1.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String text = textShape.getText();
                        if (text != null && text.contains("{TITLE}")) {
                            textShape.clearText();
                            XSLFTextParagraph paragraph = textShape.addNewTextParagraph();
                            XSLFTextRun run = paragraph.addNewTextRun();
                            run.setText(title);
                            run.setFontSize(44.0);
                            run.setBold(true);
                        }
                    }
                }
            }

            // Process slide 2 - Content slide with title, boxes, and chart
            if (ppt.getSlides().size() > 1) {
                XSLFSlide slide2 = ppt.getSlides().get(1);
                int boxIndex = 0;
                for (XSLFShape shape : slide2.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String text = textShape.getText();
                        if (text != null) {
                            if (text.contains("{CHART_TITLE}")) {
                                textShape.clearText();
                                XSLFTextParagraph paragraph = textShape.addNewTextParagraph();
                                XSLFTextRun run = paragraph.addNewTextRun();
                                run.setText(chartTitle);
                                run.setFontSize(32.0);
                                run.setBold(true);
                            } else if (text.contains("{BOX") && boxIndex < boxTexts.length) {
                                textShape.clearText();
                                XSLFTextParagraph paragraph = textShape.addNewTextParagraph();
                                XSLFTextRun run = paragraph.addNewTextRun();
                                run.setText(boxTexts[boxIndex]);
                                run.setFontSize(18.0);
                                boxIndex++;
                            }
                        }
                    }
                }
            }

            // Slide 3 is static and doesn't need modification

            // Write to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ppt.write(out);
            ppt.close();
            return out.toByteArray();
        }
    }
}
