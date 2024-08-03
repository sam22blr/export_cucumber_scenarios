package export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;

public class ExportExcel {

    public static void main(String[] args) {
    	List<String> featureFilePaths = new ArrayList<>();
        featureFilePaths.add("PlaceValidations.feature");
        featureFilePaths.add("Another.feature");
        
        String excelFilePath = "FeatureScenarios.xlsx";

        // Create a workbook and sheet for the Excel file
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Feature Scenarios");

        // Set up headers for the Excel file
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Feature Name");
        headerRow.createCell(1).setCellValue("Scenario Name");
        headerRow.createCell(2).setCellValue("Steps");

        AtomicInteger rowNum = new AtomicInteger(1);

        for (String featureFilePath : featureFilePaths) {
            try {
                // Read the Gherkin feature file into a String
                String featureContent = new String(Files.readAllBytes(Paths.get(featureFilePath)));

                // Create a GherkinParser instance
                GherkinParser.Builder builder = GherkinParser.builder();
                GherkinParser parser = builder.build();

                // Parse the feature file content
                try (Stream<Envelope> envelopes = parser.parse(Paths.get(featureFilePath))) {
                    envelopes.forEach(envelope -> {
                        Optional<GherkinDocument> optionalDocument = envelope.getGherkinDocument();
                        optionalDocument.ifPresent(gherkinDocument -> {
                            Optional<Feature> optionalFeature = gherkinDocument.getFeature();
                            optionalFeature.ifPresent(feature -> {
                                // Process the feature
                                System.out.println("Feature Name: " + feature.getName());
                                feature.getChildren().forEach(scenarioDefinition -> {
                                    Optional<Scenario> optionalScenario = scenarioDefinition.getScenario();
                                    optionalScenario.ifPresent(scenario -> {
                                        System.out.println("Scenario Name: " + scenario.getName());
                                        String stepsText = scenario.getSteps().stream()
                                                .map(Step::getText)
                                                .collect(Collectors.joining("\n"));
                                        System.out.println(stepsText);

                                        Row row = sheet.createRow(rowNum.getAndIncrement());
                                        row.createCell(0).setCellValue(feature.getName());
                                        row.createCell(1).setCellValue(scenario.getName());
                                        Cell stepsCell = row.createCell(2);
                                        stepsCell.setCellValue(stepsText);

                                        // Enable text wrapping in the steps cell
                                        CellStyle cellStyle = workbook.createCellStyle();
                                        cellStyle.setWrapText(true);
                                        stepsCell.setCellStyle(cellStyle);
                                    });
                                });
                            });
                        });
                    });
                }

            } catch (IOException e) {
                System.err.println("Error reading feature file: " + e.getMessage());
            }
        }

        // Adjust the column width to fit the content
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.setColumnWidth(2, 10000); // Set a specific width for the steps column to handle wrapping

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            System.err.println("Error writing Excel file: " + e.getMessage());
        }

        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            System.err.println("Error closing the workbook: " + e.getMessage());
        }

        System.out.println("Feature data written to Excel file successfully.");
    }
}