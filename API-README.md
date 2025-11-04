# PPTX File Generation API

This API allows you to generate PowerPoint (PPTX) presentations using two different approaches:
1. From a template using Apache POI (template-based generation)
2. From HTML content using Jsoup and Apache POI (HTML parsing approach)

## Endpoints

### 1. Template-Based Generation

**POST** `/api/files/generate-pptx`

Generate a PPTX presentation from a predefined template with customizable placeholders.

#### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | String | "Presentazione di Esempio" | Title for the cover slide |
| `chartTitle` | String | "Dati e Grafici" | Title for the content slide |
| `boxTexts` | String | "Box 1,Box 2,Box 3" | Comma-separated text for the 3 content boxes |

#### Example Usage

##### Using cURL with default parameters:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx" \
  --output presentation.pptx
```

##### Using cURL with custom parameters:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx" \
  -H "Content-Type: application/json" \
  -d '{"title":"Sales Report 2024","chartTitle":"Quarterly Performance","boxTexts":["Q1: 25% growth","Q2: 30% growth","Q3: 22% growth"]}' \
  --output sales-report.pptx
```

#### Template Structure

The generated presentation contains 3 slides:

1. **Slide 1 - Cover**: 
   - Image placeholder (blue box with "[Immagine Copertina]")
   - Customizable title

2. **Slide 2 - Content**: 
   - Customizable chart title
   - 3 customizable content boxes
   - Chart placeholder area

3. **Slide 3 - Closing**: 
   - Static thank you message
   - Contact information

#### Modifying the Template

The template file is located at:
```
src/main/resources/templates/presentation-template.pptx
```

You can modify this file directly using PowerPoint to change:
- Colors and styling
- Layout and positioning
- Static text on slide 3
- Add or remove elements

The placeholders used are:
- `{TITLE}` - Main title on the cover slide
- `{CHART_TITLE}` - Title on the content slide
- `{BOX1}`, `{BOX2}`, `{BOX3}` - Content boxes on slide 2

### 2. HTML to PPTX Generation

**POST** `/api/files/generate-pptx-from-html`

Generate a PPTX presentation by parsing HTML content and mapping HTML tags to native PPTX elements. The HTML templates are stored in the repository (simulating a database), and you provide placeholder values to customize the content.

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `templateId` | String | No | ID of the HTML template to use (default: "default") |
| `placeholders` | Object | No | Key-value pairs of placeholders to replace in the HTML template |

#### Default Template

The default template includes the following placeholders:

- `{TITLE}` - Main title on first slide
- `{DESCRIPTION}` - Description paragraph
- `{BOX1}`, `{BOX2}`, `{BOX3}` - Three styled content boxes
- `{SUBTITLE}` - Subtitle on second slide
- `{ITEM1}`, `{ITEM2}`, `{ITEM3}` - List items

#### Supported HTML Tags

The following HTML tags are mapped to PPTX elements:

- `<h1>` to `<h6>` - Headings with appropriate font sizes (36pt to 16pt)
- `<p>` - Paragraphs (18pt)
- `<div class="box">` - Styled boxes with light blue background and border
- `<ul>` / `<ol>` - Unordered and ordered lists with bullet points or numbers
- `<img>` - Image placeholders
- `<hr>` - Slide separator (creates a new slide)

#### Example Usage

##### Basic usage with default template:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx-from-html" \
  -H "Content-Type: application/json" \
  -d '{"placeholders":{"{TITLE}":"My Presentation","{DESCRIPTION}":"This is my custom description"}}' \
  --output presentation-from-html.pptx
```

##### Complete example with all placeholders:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx-from-html" \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": "default",
    "placeholders": {
      "{TITLE}": "Sales Report 2024",
      "{DESCRIPTION}": "Q4 performance overview",
      "{BOX1}": "Revenue: $1M",
      "{BOX2}": "Growth: 25%",
      "{BOX3}": "Customers: 500",
      "{SUBTITLE}": "Key Achievements",
      "{ITEM1}": "Launched new product line",
      "{ITEM2}": "Expanded to 3 new markets",
      "{ITEM3}": "Improved customer satisfaction"
    }
  }' \
  --output sales-report.pptx
```

#### Advantages of HTML Approach

- **Selectable Text**: Text remains as native text in the presentation (not images)
- **Smaller File Size**: PPTX files are more compact than image-based approaches
- **Semantic Control**: Content can be updated and styled programmatically
- **True PPT Format**: Generated files are proper PowerPoint presentations

#### Limitations

- **Manual CSS Mapping**: CSS styles must be manually mapped to PPTX coordinates
- **Partial CSS Support**: Not all CSS properties are supported
- **Complex Layouts**: May be challenging for very complex or responsive layouts

## Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080.

## Running Tests

```bash
./mvnw test
```

## Technologies Used

- Spring Boot 3.5.7
- Apache POI 5.3.0 (poi-ooxml)
- Jsoup 1.18.1 (HTML parsing)
- Java 17
