# PPTX File Generation API

This API allows you to generate PowerPoint (PPTX) presentations from a template using Apache POI.

## Endpoint

**POST** `/api/files/generate-pptx`

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | String | "Presentazione di Esempio" | Title for the cover slide |
| `chartTitle` | String | "Dati e Grafici" | Title for the content slide |
| `boxTexts` | Array of String | ["Box 1", "Box 2", "Box 3"] | Text for the 3 content boxes |
| `numberOfColumns` | Integer | null | Number of data points in the chart (optional) |
| `barChartValues` | Array of Double | null | Values for the bar chart (optional) |
| `lineChartValues` | Array of Double | null | Values for the line chart (optional) |

**Note:** To modify chart data, you must provide all three chart parameters (`numberOfColumns`, `barChartValues`, and `lineChartValues`). If any of them is missing or null, the chart will not be modified and will keep the default data from the template.

### Example Usage

#### Using cURL with default parameters:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx" \
  -H "Content-Type: application/json" \
  -d '{}' \
  --output presentation.pptx
```

#### Using cURL with custom parameters:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Sales Report 2024",
    "chartTitle": "Quarterly Performance",
    "boxTexts": ["Q1: 25% growth", "Q2: 30% growth", "Q3: 22% growth"]
  }' \
  --output sales-report.pptx
```

#### Using cURL with chart data modification:
```bash
curl -X POST "http://localhost:8080/api/files/generate-pptx" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Financial Report 2024",
    "chartTitle": "Revenue and Margin Analysis",
    "boxTexts": ["Strong Growth", "Market Leader", "Profitable"],
    "numberOfColumns": 5,
    "barChartValues": [10.5, 25.3, 38.7, 45.2, 52.1],
    "lineChartValues": [8.0, 12.5, 15.3, 18.2, 22.5]
  }' \
  --output financial-report.pptx
```

## Template Structure

The generated presentation contains 3 slides:

1. **Slide 1 - Cover**: 
   - Image placeholder (blue box with "[Immagine Copertina]")
   - Customizable title

2. **Slide 2 - Content**: 
   - Customizable chart title
   - 3 customizable content boxes
   - **Chart with modifiable data** (combination bar and line chart)

3. **Slide 3 - Closing**: 
   - Static thank you message
   - Contact information

## Chart Data Modification

The chart on slide 2 is a combination chart with:
- **Bar chart** (stacked column chart): Represents primary data series
- **Line chart**: Represents secondary data series (e.g., percentage or margin)

When providing chart data:
- `numberOfColumns`: Specifies how many data points to display (e.g., 5 for 5 quarters/months)
- `barChartValues`: Array of numeric values for the bar/column chart
- `lineChartValues`: Array of numeric values for the line chart
- Both arrays must have at least `numberOfColumns` elements

Example chart configuration:
```json
{
  "numberOfColumns": 4,
  "barChartValues": [100, 120, 145, 160],
  "lineChartValues": [10.5, 12.3, 14.8, 16.2]
}
```

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
- Chart styling (but data values will be overridden if chart parameters are provided)

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
