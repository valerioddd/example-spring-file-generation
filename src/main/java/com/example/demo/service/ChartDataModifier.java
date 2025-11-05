package com.example.demo.service;

import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.List;

@Component
public class ChartDataModifier {

    /**
     * Updates chart data in the presentation's second slide (index 1)
     * 
     * @param ppt The presentation to modify
     * @param numberOfColumns Number of data points
     * @param barChartValues Values for the bar chart
     * @param lineChartValues Values for the line chart
     */
    public void updateChartData(XMLSlideShow ppt, Integer numberOfColumns, 
                               List<Double> barChartValues, List<Double> lineChartValues) throws Exception {
        if (numberOfColumns == null || barChartValues == null || lineChartValues == null) {
            return; // Skip if no chart data provided
        }

        // Validate input
        if (barChartValues.size() < numberOfColumns || lineChartValues.size() < numberOfColumns) {
            throw new IllegalArgumentException("Chart values arrays must have at least numberOfColumns elements");
        }

        // Get slide 2 (index 1)
        List<XSLFSlide> slides = ppt.getSlides();
        if (slides.size() < 2) {
            return; // Not enough slides
        }

        XSLFSlide slide = slides.get(1);

        // Find the chart in the slide
        XSLFChart chart = null;
        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFGraphicFrame) {
                XSLFGraphicFrame graphicFrame = (XSLFGraphicFrame) shape;
                if (graphicFrame.hasChart()) {
                    chart = graphicFrame.getChart();
                    break;
                }
            }
        }

        if (chart == null) {
            return; // No chart found
        }

        // Get the underlying package part for the chart
        PackagePart chartPart = chart.getPackagePart();

        // Parse the existing chart XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(chartPart.getInputStream());

        // Update the chart data
        updateBarChartData(doc, barChartValues, numberOfColumns);
        updateLineChartData(doc, lineChartValues, numberOfColumns);

        // Write back the modified XML to the package part
        try (OutputStream out = chartPart.getOutputStream()) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.STANDALONE, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        }
    }

    private void updateBarChartData(Document doc, List<Double> values, int count) {
        // Find bar chart series data
        NodeList barCharts = doc.getElementsByTagNameNS(
            "http://schemas.openxmlformats.org/drawingml/2006/chart", "barChart");
        
        if (barCharts.getLength() > 0) {
            Element barChart = (Element) barCharts.item(0);
            updateSeriesData(doc, barChart, values, count);
        }
    }

    private void updateLineChartData(Document doc, List<Double> values, int count) {
        // Find line chart series data
        NodeList lineCharts = doc.getElementsByTagNameNS(
            "http://schemas.openxmlformats.org/drawingml/2006/chart", "lineChart");
        
        if (lineCharts.getLength() > 0) {
            Element lineChart = (Element) lineCharts.item(0);
            updateSeriesData(doc, lineChart, values, count);
        }
    }

    private void updateSeriesData(Document doc, Element chartElement, List<Double> values, int count) {
        // Find the series element
        NodeList serElements = chartElement.getElementsByTagNameNS(
            "http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        
        if (serElements.getLength() > 0) {
            Element ser = (Element) serElements.item(0);
            
            // Find the numCache element
            NodeList numCaches = ser.getElementsByTagNameNS(
                "http://schemas.openxmlformats.org/drawingml/2006/chart", "numCache");
            
            if (numCaches.getLength() > 0) {
                Element numCache = (Element) numCaches.item(0);
                
                // Update ptCount
                NodeList ptCounts = numCache.getElementsByTagNameNS(
                    "http://schemas.openxmlformats.org/drawingml/2006/chart", "ptCount");
                if (ptCounts.getLength() > 0) {
                    Element ptCount = (Element) ptCounts.item(0);
                    ptCount.setAttribute("val", String.valueOf(count));
                }
                
                // Remove old data points
                NodeList oldPts = numCache.getElementsByTagNameNS(
                    "http://schemas.openxmlformats.org/drawingml/2006/chart", "pt");
                while (oldPts.getLength() > 0) {
                    oldPts.item(0).getParentNode().removeChild(oldPts.item(0));
                }
                
                // Add new data points
                for (int i = 0; i < count && i < values.size(); i++) {
                    Element pt = doc.createElementNS(
                        "http://schemas.openxmlformats.org/drawingml/2006/chart", "c:pt");
                    pt.setAttribute("idx", String.valueOf(i));
                    
                    Element v = doc.createElementNS(
                        "http://schemas.openxmlformats.org/drawingml/2006/chart", "c:v");
                    v.setTextContent(String.valueOf(values.get(i)));
                    
                    pt.appendChild(v);
                    numCache.appendChild(pt);
                }
            }
        }
    }
}
