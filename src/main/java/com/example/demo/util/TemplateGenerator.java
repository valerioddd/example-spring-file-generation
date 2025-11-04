package com.example.demo.util;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class TemplateGenerator {

    public static void main(String[] args) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();
        ppt.setPageSize(new Dimension(720, 540));

        // Slide 1: Cover slide with image placeholder and title
        XSLFSlide slide1 = ppt.createSlide();
        
        // Add cover image placeholder (as a colored rectangle)
        XSLFAutoShape coverImage = slide1.createAutoShape();
        coverImage.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        coverImage.setAnchor(new Rectangle(50, 50, 620, 250));
        coverImage.setFillColor(new Color(70, 130, 180)); // Steel blue
        XSLFTextParagraph imgPara = coverImage.addNewTextParagraph();
        XSLFTextRun imgRun = imgPara.addNewTextRun();
        imgRun.setText("[Immagine Copertina]");
        imgRun.setFontSize(20.0);
        imgRun.setFontColor(Color.WHITE);
        imgPara.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);
        
        // Add title placeholder
        XSLFAutoShape titleBox = slide1.createAutoShape();
        titleBox.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        titleBox.setAnchor(new Rectangle(50, 350, 620, 120));
        titleBox.setFillColor(Color.WHITE);
        titleBox.setLineColor(new Color(70, 130, 180));
        titleBox.setLineWidth(2.0);
        XSLFTextParagraph titlePara = titleBox.addNewTextParagraph();
        XSLFTextRun titleRun = titlePara.addNewTextRun();
        titleRun.setText("{TITLE}");
        titleRun.setFontSize(44.0);
        titleRun.setFontColor(new Color(70, 130, 180));
        titleRun.setBold(true);
        titlePara.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);

        // Slide 2: Content slide with title, 3 boxes, and chart
        XSLFSlide slide2 = ppt.createSlide();
        
        // Add chart title
        XSLFAutoShape chartTitleBox = slide2.createAutoShape();
        chartTitleBox.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        chartTitleBox.setAnchor(new Rectangle(50, 30, 620, 60));
        chartTitleBox.setFillColor(Color.WHITE);
        chartTitleBox.setLineWidth(0.0);
        XSLFTextParagraph chartTitlePara = chartTitleBox.addNewTextParagraph();
        XSLFTextRun chartTitleRun = chartTitlePara.addNewTextRun();
        chartTitleRun.setText("{CHART_TITLE}");
        chartTitleRun.setFontSize(32.0);
        chartTitleRun.setFontColor(new Color(51, 51, 51));
        chartTitleRun.setBold(true);
        chartTitlePara.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);

        // Add 3 boxes
        int boxWidth = 190;
        int boxHeight = 150;
        int boxY = 110;
        int spacing = 25;
        
        for (int i = 0; i < 3; i++) {
            XSLFAutoShape box = slide2.createAutoShape();
            box.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
            int boxX = 50 + i * (boxWidth + spacing);
            box.setAnchor(new Rectangle(boxX, boxY, boxWidth, boxHeight));
            box.setFillColor(new Color(240, 248, 255)); // Alice blue
            box.setLineColor(new Color(70, 130, 180));
            box.setLineWidth(2.0);
            
            XSLFTextParagraph boxPara = box.addNewTextParagraph();
            XSLFTextRun boxRun = boxPara.addNewTextRun();
            boxRun.setText("{BOX" + (i + 1) + "}");
            boxRun.setFontSize(18.0);
            boxRun.setFontColor(new Color(51, 51, 51));
            boxPara.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);
        }

        // Add chart placeholder
        XSLFAutoShape chartBox = slide2.createAutoShape();
        chartBox.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        chartBox.setAnchor(new Rectangle(50, 280, 620, 220));
        chartBox.setFillColor(new Color(245, 245, 245));
        chartBox.setLineColor(new Color(70, 130, 180));
        chartBox.setLineWidth(2.0);
        XSLFTextParagraph chartPara = chartBox.addNewTextParagraph();
        XSLFTextRun chartRun = chartPara.addNewTextRun();
        chartRun.setText("[Grafico]");
        chartRun.setFontSize(24.0);
        chartRun.setFontColor(new Color(128, 128, 128));
        chartPara.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);

        // Slide 3: Static closing slide
        XSLFSlide slide3 = ppt.createSlide();
        
        // Add title
        XSLFAutoShape closingTitle = slide3.createAutoShape();
        closingTitle.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        closingTitle.setAnchor(new Rectangle(50, 80, 620, 80));
        closingTitle.setFillColor(Color.WHITE);
        closingTitle.setLineWidth(0.0);
        XSLFTextParagraph closingTitlePara = closingTitle.addNewTextParagraph();
        XSLFTextRun closingTitleRun = closingTitlePara.addNewTextRun();
        closingTitleRun.setText("Grazie per l'attenzione!");
        closingTitleRun.setFontSize(40.0);
        closingTitleRun.setFontColor(new Color(70, 130, 180));
        closingTitleRun.setBold(true);
        closingTitlePara.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);

        // Add closing text
        XSLFAutoShape closingText = slide3.createAutoShape();
        closingText.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        closingText.setAnchor(new Rectangle(100, 220, 520, 200));
        closingText.setFillColor(Color.WHITE);
        closingText.setLineWidth(0.0);
        XSLFTextParagraph closingPara = closingText.addNewTextParagraph();
        XSLFTextRun closingRun = closingPara.addNewTextRun();
        closingRun.setText("Questa presentazione è stata generata automaticamente.\n\n" +
                          "Per ulteriori informazioni, contattaci all'indirizzo:\n" +
                          "info@example.com");
        closingRun.setFontSize(20.0);
        closingRun.setFontColor(new Color(51, 51, 51));
        closingPara.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);

        // Save the template
        String outputPath = "src/main/resources/templates/presentation-template.pptx";
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            ppt.write(out);
        }
        ppt.close();
        
        System.out.println("Template created successfully at: " + outputPath);
    }
}
