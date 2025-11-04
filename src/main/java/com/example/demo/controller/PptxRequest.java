package com.example.demo.controller;

import java.util.List;

public class PptxRequest {
    private String title;

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

    private String chartTitle;
    // default boxes provided in the DTO (can be overridden by setter / constructor)
    private List<String> boxTexts = List.of("Default box 1", "Default box 2", "Default box 3");
}
