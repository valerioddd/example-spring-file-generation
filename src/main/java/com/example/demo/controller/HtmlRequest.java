package com.example.demo.controller;

import java.util.Map;

/**
 * Request DTO for HTML-based PPTX generation.
 * Contains template ID and placeholder values to replace in the HTML template.
 */
public class HtmlRequest {
    private String templateId = "default";
    private Map<String, String> placeholders;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }
}
