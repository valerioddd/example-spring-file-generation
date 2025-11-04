# PPTX File Generation API

This API allows you to generate PowerPoint (PPTX) presentations from a template using Apache POI.

## Endpoint

**POST** `/api/files/generate-pptx`

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | String | "Presentazione di Esempio" | Title for the cover slide |
| `chartTitle` | String | "Dati e Grafici" | Title for the content slide |
| `boxTexts` | String | "Box 1,Box 2,Box 3" | Comma-separated text for the 3 content boxes |

### Example Usage

#### Using cURL with default parameters:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx" \
  --output presentation.pptx
```

#### Using cURL with custom parameters:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx" \
  -d "title=Sales Report 2024" \
  -d "chartTitle=Quarterly Performance" \
  -d "boxTexts=Q1: 25% growth,Q2: 30% growth,Q3: 22% growth" \
  --output sales-report.pptx
```

## Template Structure

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

## Modifying the Template

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
- Java 17
