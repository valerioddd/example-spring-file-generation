package com.example.demo.service;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Component
public class ChartDataModifier {

    private static final Logger logger = LoggerFactory.getLogger(ChartDataModifier.class);

    /**
     * Updates chart data in a PPTX byte array (second slide - index 1)
     * 
     * @param pptxBytes The PPTX file as byte array
     * @param numberOfColumns Number of data points
     * @param barChartValues Values for the bar chart
     * @param lineChartValues Values for the line chart
     * @return Modified PPTX file as byte array
     */
    public byte[] updateChartData(byte[] pptxBytes, Integer numberOfColumns, 
                                 List<Double> barChartValues, List<Double> lineChartValues) throws Exception {
        if (numberOfColumns == null || barChartValues == null || lineChartValues == null) {
            logger.debug("Skipping chart update - one or more parameters are null");
            return pptxBytes; // Return unchanged if no chart data provided
        }

        logger.info("Starting chart update with {} columns", numberOfColumns);

        // Validate input
        if (barChartValues.size() < numberOfColumns || lineChartValues.size() < numberOfColumns) {
            String errorMsg = String.format("Chart values arrays must have at least %d elements. " +
                "Received: barChartValues=%d, lineChartValues=%d", 
                numberOfColumns, barChartValues.size(), lineChartValues.size());
            throw new IllegalArgumentException(errorMsg);
        }

        // Open the PPTX package
        try (ByteArrayInputStream bis = new ByteArrayInputStream(pptxBytes);
             OPCPackage pkg = OPCPackage.open(bis)) {
            
            // Find the chart part - typically /ppt/charts/chart1.xml
            PackagePart chartPart = null;
            for (PackagePart part : pkg.getParts()) {
                if (part.getPartName().getName().equals("/ppt/charts/chart1.xml")) {
                    chartPart = part;
                    break;
                }
            }
            
            if (chartPart == null) {
                logger.warn("No chart part found in PPTX package");
                return pptxBytes;
            }
            
            logger.debug("Found chart part");

            // Parse the existing chart XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(chartPart.getInputStream());

            // Update the embedded Excel file first
            logger.debug("Updating embedded Excel file");
            updateEmbeddedExcelFile(pkg, barChartValues, lineChartValues, numberOfColumns);

            // Update the chart data cache
            logger.debug("Updating bar chart data cache");
            updateBarChartData(doc, barChartValues, numberOfColumns);
            logger.debug("Updating line chart data cache");
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
                logger.debug("Successfully wrote updated chart data");
            }
            
            // Write the modified package to a new byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            pkg.save(bos);
            logger.info("Package saved successfully with updated chart data");
            return bos.toByteArray();
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

    /**
     * Updates the embedded Excel file with new chart data
     * 
     * @param pkg The OPC package
     * @param barChartValues Values for the bar chart (row 1 in Excel)
     * @param lineChartValues Values for the line chart (row 2 in Excel)
     * @param numberOfColumns Number of data points
     */
    private void updateEmbeddedExcelFile(OPCPackage pkg, List<Double> barChartValues, 
                                        List<Double> lineChartValues, int numberOfColumns) throws Exception {
        // Find the embedded Excel file - typically /ppt/embeddings/Microsoft_Excel_Binary_Worksheet.xlsb
        PackagePart excelPart = null;
        for (PackagePart part : pkg.getParts()) {
            String partName = part.getPartName().getName();
            if (partName.startsWith("/ppt/embeddings/") && 
                (partName.endsWith(".xlsx") || partName.endsWith(".xlsb"))) {
                excelPart = part;
                break;
            }
        }
        
        if (excelPart == null) {
            logger.warn("No embedded Excel file found in PPTX package");
            return;
        }
        
        logger.debug("Found embedded Excel file: {}", excelPart.getPartName().getName());
        
        // Read the existing Excel file
        Workbook workbook;
        try (InputStream is = excelPart.getInputStream()) {
            workbook = WorkbookFactory.create(is);
        }
        
        // Get the first sheet (Sheet1)
        Sheet sheet = workbook.getSheetAt(0);
        
        // Update or create row 1 (bar chart data) - row index 0
        Row row1 = sheet.getRow(0);
        if (row1 == null) {
            row1 = sheet.createRow(0);
        }
        
        // Update or create row 2 (line chart data) - row index 1
        Row row2 = sheet.getRow(1);
        if (row2 == null) {
            row2 = sheet.createRow(1);
        }
        
        // Clear existing cells beyond numberOfColumns and update with new values
        for (int i = 0; i < numberOfColumns; i++) {
            // Validate that we have values for this index (should already be validated, but double-check)
            if (i >= barChartValues.size() || i >= lineChartValues.size()) {
                logger.warn("Insufficient values for column {}, skipping", i);
                break;
            }
            
            // Update bar chart values in row 1
            Cell cell1 = row1.getCell(i);
            if (cell1 == null) {
                cell1 = row1.createCell(i);
            }
            cell1.setCellValue(barChartValues.get(i));
            
            // Update line chart values in row 2
            Cell cell2 = row2.getCell(i);
            if (cell2 == null) {
                cell2 = row2.createCell(i);
            }
            cell2.setCellValue(lineChartValues.get(i));
        }
        
        // Remove cells beyond numberOfColumns to avoid stale data
        int lastCellNum1 = row1.getLastCellNum();
        int lastCellNum2 = row2.getLastCellNum();
        // getLastCellNum returns -1 if no cells exist, so use 0 as minimum
        int lastCellNum = Math.max(Math.max(lastCellNum1, lastCellNum2), 0);
        for (int i = numberOfColumns; i < lastCellNum; i++) {
            Cell cell1 = row1.getCell(i);
            if (cell1 != null) {
                row1.removeCell(cell1);
            }
            Cell cell2 = row2.getCell(i);
            if (cell2 != null) {
                row2.removeCell(cell2);
            }
        }
        
        logger.debug("Updated Excel file with {} columns", numberOfColumns);
        
        // Write the modified workbook back to the package part
        try (OutputStream out = excelPart.getOutputStream()) {
            workbook.write(out);
            logger.debug("Successfully wrote updated Excel file");
        }
        
        workbook.close();
    }
}
