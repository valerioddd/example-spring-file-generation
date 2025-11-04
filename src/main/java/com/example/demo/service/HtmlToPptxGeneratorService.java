package com.example.demo.service;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HtmlToPptxGeneratorService {

    // Page dimensions in points (1 point = 1/72 inch)
    // 720x540 points = 10x7.5 inches (standard 4:3 aspect ratio)
    private static final int PAGE_WIDTH = 720;
    private static final int PAGE_HEIGHT = 540;
    private static final int MARGIN = 50;
    private static final int CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;
    
    // Text estimation constants
    private static final double CHARS_PER_LINE = 50.0;

    public byte[] generatePptxFromHtml(String html) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();
        ppt.setPageSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));

        // Parse HTML
        Document doc = Jsoup.parse(html);

        // Group content by slides - split on <hr> or create single slide
        List<Element> slideElements = new ArrayList<>();
        Elements hrElements = doc.select("hr");
        
        if (hrElements.isEmpty()) {
            // Single slide with all content
            slideElements.add(doc.body());
        } else {
            // Multiple slides separated by <hr>
            Elements children = doc.body().children();
            Element currentSlideContent = new Element("div");
            
            for (Element child : children) {
                if (child.tagName().equals("hr")) {
                    if (currentSlideContent.childrenSize() > 0) {
                        slideElements.add(currentSlideContent);
                    }
                    currentSlideContent = new Element("div");
                } else {
                    currentSlideContent.appendChild(child.clone());
                }
            }
            
            // Add last slide
            if (currentSlideContent.childrenSize() > 0) {
                slideElements.add(currentSlideContent);
            }
        }

        // Create slides
        int yPosition = MARGIN;
        for (Element slideContent : slideElements) {
            XSLFSlide slide = ppt.createSlide();
            yPosition = MARGIN;
            yPosition = renderElements(slide, slideContent.children(), yPosition);
        }

        // Write to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ppt.write(out);
        ppt.close();
        return out.toByteArray();
    }

    private int renderElements(XSLFSlide slide, Elements elements, int yPosition) {
        for (Element element : elements) {
            yPosition = renderElement(slide, element, yPosition);
        }
        return yPosition;
    }

    private int renderElement(XSLFSlide slide, Element element, int yPosition) {
        String tagName = element.tagName();
        String text = element.text();

        // Skip empty elements
        if (text.isEmpty() && !tagName.equals("img")) {
            return yPosition;
        }

        switch (tagName) {
            case "h1":
                return renderHeading(slide, text, yPosition, 36.0, true);
            case "h2":
                return renderHeading(slide, text, yPosition, 28.0, true);
            case "h3":
                return renderHeading(slide, text, yPosition, 24.0, true);
            case "h4":
                return renderHeading(slide, text, yPosition, 20.0, true);
            case "h5":
                return renderHeading(slide, text, yPosition, 18.0, true);
            case "h6":
                return renderHeading(slide, text, yPosition, 16.0, true);
            case "p":
                return renderParagraph(slide, text, yPosition, 18.0);
            case "div":
                // Check if div has class="box"
                if (element.hasClass("box")) {
                    return renderBox(slide, text, yPosition);
                } else {
                    // Render div content recursively
                    return renderElements(slide, element.children(), yPosition);
                }
            case "ul":
            case "ol":
                return renderList(slide, element, yPosition);
            case "img":
                return renderImagePlaceholder(slide, yPosition);
            default:
                // For unsupported tags, render as plain text
                if (!text.isEmpty()) {
                    return renderParagraph(slide, text, yPosition, 18.0);
                }
                return yPosition;
        }
    }

    private int renderHeading(XSLFSlide slide, String text, int yPosition, double fontSize, boolean bold) {
        XSLFAutoShape textBox = slide.createAutoShape();
        textBox.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        
        int height = (int) (fontSize * 1.5);
        textBox.setAnchor(new Rectangle(MARGIN, yPosition, CONTENT_WIDTH, height));
        textBox.setFillColor(Color.WHITE);
        textBox.setLineWidth(0.0);
        
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun run = paragraph.addNewTextRun();
        run.setText(text);
        run.setFontSize(fontSize);
        run.setFontColor(new Color(51, 51, 51));
        run.setBold(bold);
        
        return yPosition + height + 10;
    }

    private int renderParagraph(XSLFSlide slide, String text, int yPosition, double fontSize) {
        XSLFAutoShape textBox = slide.createAutoShape();
        textBox.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        
        // Estimate height based on text length and width
        int estimatedHeight = Math.max(40, (int) (text.length() / CHARS_PER_LINE * fontSize * 1.5));
        textBox.setAnchor(new Rectangle(MARGIN, yPosition, CONTENT_WIDTH, estimatedHeight));
        textBox.setFillColor(Color.WHITE);
        textBox.setLineWidth(0.0);
        
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun run = paragraph.addNewTextRun();
        run.setText(text);
        run.setFontSize(fontSize);
        run.setFontColor(new Color(51, 51, 51));
        
        return yPosition + estimatedHeight + 10;
    }

    private int renderBox(XSLFSlide slide, String text, int yPosition) {
        XSLFAutoShape box = slide.createAutoShape();
        box.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        
        int boxHeight = 80;
        box.setAnchor(new Rectangle(MARGIN, yPosition, CONTENT_WIDTH, boxHeight));
        box.setFillColor(new Color(240, 248, 255)); // Alice blue
        box.setLineColor(new Color(70, 130, 180)); // Steel blue
        box.setLineWidth(2.0);
        
        XSLFTextParagraph paragraph = box.addNewTextParagraph();
        XSLFTextRun run = paragraph.addNewTextRun();
        run.setText(text);
        run.setFontSize(18.0);
        run.setFontColor(new Color(51, 51, 51));
        paragraph.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);
        
        return yPosition + boxHeight + 15;
    }

    private int renderList(XSLFSlide slide, Element listElement, int yPosition) {
        Elements items = listElement.select("li");
        boolean isOrdered = listElement.tagName().equals("ol");
        
        int itemIndex = 1;
        for (Element item : items) {
            String text = item.text();
            String prefix = isOrdered ? (itemIndex + ". ") : "• ";
            
            XSLFAutoShape textBox = slide.createAutoShape();
            textBox.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
            
            int itemHeight = 30;
            textBox.setAnchor(new Rectangle(MARGIN + 20, yPosition, CONTENT_WIDTH - 20, itemHeight));
            textBox.setFillColor(Color.WHITE);
            textBox.setLineWidth(0.0);
            
            XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
            XSLFTextRun run = paragraph.addNewTextRun();
            run.setText(prefix + text);
            run.setFontSize(16.0);
            run.setFontColor(new Color(51, 51, 51));
            
            yPosition += itemHeight + 5;
            itemIndex++;
        }
        
        return yPosition + 10;
    }

    private int renderImagePlaceholder(XSLFSlide slide, int yPosition) {
        XSLFAutoShape imageBox = slide.createAutoShape();
        imageBox.setShapeType(org.apache.poi.sl.usermodel.ShapeType.RECT);
        
        int imageHeight = 150;
        imageBox.setAnchor(new Rectangle(MARGIN, yPosition, CONTENT_WIDTH, imageHeight));
        imageBox.setFillColor(new Color(220, 220, 220)); // Light gray
        imageBox.setLineColor(new Color(128, 128, 128)); // Gray
        imageBox.setLineWidth(2.0);
        
        XSLFTextParagraph paragraph = imageBox.addNewTextParagraph();
        XSLFTextRun run = paragraph.addNewTextRun();
        run.setText("[Immagine]");
        run.setFontSize(20.0);
        run.setFontColor(new Color(128, 128, 128));
        paragraph.setTextAlign(org.apache.poi.sl.usermodel.TextParagraph.TextAlign.CENTER);
        
        return yPosition + imageHeight + 15;
    }
}
