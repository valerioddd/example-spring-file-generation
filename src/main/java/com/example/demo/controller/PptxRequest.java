package com.example.demo.controller;

import java.util.List;

public class PptxRequest {
    private String title;
    private String chartTitle;
    // default boxes provided in the DTO (can be overridden by setter / constructor)
    private List<String> boxTexts = List.of("Default box 1", "Default box 2", "Default box 3");
    
    // Chart data fields
    private Integer numberOfColumns;
    private List<Double> barChartValues;
    private List<Double> lineChartValues;

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getBoxTexts() {
        return boxTexts;
    }

    public void setBoxTexts(List<String> boxTexts) {
        this.boxTexts = boxTexts;
    }

    public Integer getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(Integer numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public List<Double> getBarChartValues() {
        return barChartValues;
    }

    public void setBarChartValues(List<Double> barChartValues) {
        this.barChartValues = barChartValues;
    }

    public List<Double> getLineChartValues() {
        return lineChartValues;
    }

    public void setLineChartValues(List<Double> lineChartValues) {
        this.lineChartValues = lineChartValues;
    }
}
