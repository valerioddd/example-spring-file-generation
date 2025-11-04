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

Generate a PPTX presentation by parsing HTML content and mapping HTML tags to native PPTX elements.

#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `html` | String | Yes | HTML content to convert to PPTX |

#### Supported HTML Tags

The following HTML tags are mapped to PPTX elements:

- `<h1>` to `<h6>` - Headings with appropriate font sizes (36pt to 16pt)
- `<p>` - Paragraphs (18pt)
- `<div class="box">` - Styled boxes with light blue background and border
- `<ul>` / `<ol>` - Unordered and ordered lists with bullet points or numbers
- `<img>` - Image placeholders
- `<hr>` - Slide separator (creates a new slide)

#### Example Usage

##### Single slide presentation:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx-from-html" \
  -H "Content-Type: application/json" \
  -d '{"html":"<html><body><h1>Title</h1><p>This is a paragraph.</p><div class=\"box\">Box content</div></body></html>"}' \
  --output presentation-from-html.pptx
```

##### Multi-slide presentation (using `<hr>` separators):
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx-from-html" \
  -H "Content-Type: application/json" \
  -d '{"html":"<html><body><h1>Slide 1</h1><p>First slide content</p><hr><h2>Slide 2</h2><p>Second slide content</p></body></html>"}' \
  --output multi-slide.pptx
```

##### With lists:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx-from-html" \
  -H "Content-Type: application/json" \
  -d '{"html":"<html><body><h2>Features</h2><ul><li>Feature 1</li><li>Feature 2</li><li>Feature 3</li></ul></body></html>"}' \
  --output presentation-with-list.pptx
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
