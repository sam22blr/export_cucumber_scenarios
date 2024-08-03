package export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.GherkinDocument;

public class abc {

    public static void main(String[] args) {
        try {
            // Read the Gherkin feature file into a String
            String featureFilePath = "PlaceValidations.feature";
            String featureContent = new String(Files.readAllBytes(Paths.get(featureFilePath)));

            // Create a GherkinParser instance
            GherkinParser.Builder builder =  GherkinParser.builder();
            GherkinParser parser = builder.build();

           // Stream<Envelope> envelopess = parser.parse(Paths.get(featureFilePath));
            // Parse the feature file content
            try (Stream<Envelope> envelopes = parser.parse(Paths.get(featureFilePath))) {
                envelopes.forEach(envelope -> {
                    Optional<GherkinDocument> optionalDocument = envelope.getGherkinDocument();
                    optionalDocument.ifPresent(gherkinDocument -> {
                        Optional<io.cucumber.messages.types.Feature> optionalFeature = gherkinDocument.getFeature();
                        optionalFeature.ifPresent(feature -> {
                            // Process the feature
                            System.out.println("Feature Name: " + feature.getName());
                            feature.getChildren().forEach(scenario -> {
                                System.out.println("Scenario Name: " + scenario.getScenario().get().getName());
                                scenario.getScenario().get().getSteps().forEach(step -> {
                                	System.out.println(step.getText());
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
}