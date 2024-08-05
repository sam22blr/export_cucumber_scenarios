package export;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ExportTestNGTests {

    public static List<String> listAllTestsAndMethods(String testNGXmlFilePath) {
        List<String> testDetails = new ArrayList<>();
        try {
            File xmlFile = new File(testNGXmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the document
            doc.getDocumentElement().normalize();

            // Get all <test> elements
            NodeList testNodes = doc.getElementsByTagName("test");

            for (int i = 0; i < testNodes.getLength(); i++) {
                Element testElement = (Element) testNodes.item(i);
                String testName = testElement.getAttribute("name");

                // Process <class> elements within the <test>
                processClasses(testElement, testName, testDetails);

                // Process <package> elements within the <test>
                processPackages(testElement, testName, testDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testDetails;
    }

    private static void processClasses(Element testElement, String testName, List<String> testDetails) {
        NodeList classNodes = testElement.getElementsByTagName("class");
        for (int j = 0; j < classNodes.getLength(); j++) {
            Element classElement = (Element) classNodes.item(j);
            String className = classElement.getAttribute("name");

            // Get all <include> elements within the <methods> of the <class>
            NodeList methodNodes = classElement.getElementsByTagName("include");

            for (int k = 0; k < methodNodes.getLength(); k++) {
                Element methodElement = (Element) methodNodes.item(k);
                String methodName = methodElement.getAttribute("name");
                testDetails.add("Test: " + testName + " | Class: " + className + " | Method: " + methodName);
            }

            // If no <include> tags are present, consider all methods in the class as included
            if (methodNodes.getLength() == 0) {
                testDetails.add("Test: " + testName + " | Class: " + className + " | Method: [all methods]");
            }
        }
    }

    private static void processPackages(Element testElement, String testName, List<String> testDetails) {
        NodeList packageNodes = testElement.getElementsByTagName("package");
        for (int j = 0; j < packageNodes.getLength(); j++) {
            Element packageElement = (Element) packageNodes.item(j);
            String packageName = packageElement.getAttribute("name");
            
            // Get all class names in the package
            List<String> classNames = getClassNamesInPackage(packageName);
            
            for (String className : classNames) {
                testDetails.add("Test: " + testName + " | Package: " + packageName + " | Class: " + className + " | Method: [all methods]");
            }
        }
    }

    private static List<String> getClassNamesInPackage(String packageName) {
        List<String> classNames = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        
        try (Stream<Path> paths = Files.walk(Paths.get("path/to/your/classes/root", packagePath))) {
            classNames = paths
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .filter(fileName -> fileName.endsWith(".class"))
                .map(fileName -> packageName + "." + fileName.replace(".class", ""))
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return classNames;
    }

    public static void main(String[] args) {
        String testNGXmlFilePath = "path/to/your/testng.xml";
        List<String> testDetails = listAllTestsAndMethods(testNGXmlFilePath);

        System.out.println("List of TestNG tests and methods:");
        for (String detail : testDetails) {
            System.out.println(detail);
        }
    }
}
