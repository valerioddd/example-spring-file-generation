package com.example.demo.controller;

import jakarta.validation.constraints.NotBlank;

public class HtmlRequest {
    @NotBlank(message = "HTML content is required")
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
