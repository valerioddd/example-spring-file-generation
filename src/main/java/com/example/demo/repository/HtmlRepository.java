package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository that stores HTML templates.
 * In a real application, this would be connected to a database.
 * For now, templates are stored in memory.
 */
@Repository
public class HtmlRepository {

    private final Map<String, String> templates = new HashMap<>();

    public HtmlRepository() {
        // Initialize with a default template
        initializeDefaultTemplate();
    }

    private void initializeDefaultTemplate() {
        String defaultTemplate = """
                <html>
                <body>
                    <h1>{TITLE}</h1>
                    <p>{DESCRIPTION}</p>
                    <div class="box">{BOX1}</div>
                    <div class="box">{BOX2}</div>
                    <div class="box">{BOX3}</div>
                    <hr>
                    <h2>{SUBTITLE}</h2>
                    <ul>
                        <li>{ITEM1}</li>
                        <li>{ITEM2}</li>
                        <li>{ITEM3}</li>
                    </ul>
                </body>
                </html>
                """;
        templates.put("default", defaultTemplate);
    }

    /**
     * Retrieves an HTML template by its ID.
     * 
     * @param templateId the ID of the template to retrieve
     * @return the HTML template, or null if not found
     */
    public String getTemplate(String templateId) {
        return templates.get(templateId);
    }

    /**
     * Saves or updates an HTML template.
     * 
     * @param templateId the ID of the template
     * @param html the HTML content
     */
    public void saveTemplate(String templateId, String html) {
        templates.put(templateId, html);
    }

    /**
     * Checks if a template exists.
     * 
     * @param templateId the ID of the template
     * @return true if the template exists, false otherwise
     */
    public boolean exists(String templateId) {
        return templates.containsKey(templateId);
    }
}
