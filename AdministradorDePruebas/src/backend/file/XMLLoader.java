// backend/file/XMLLoader.java
package backend.file;

import backend.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de TestLoader para cargar pruebas desde archivos XML.
 * Estructura XML esperada:
 * <test name="Nombre de la Prueba">
 * <question type="multiple_choice" bloom_level="Recordar" estimated_time="60">
 * <statement>Enunciado de la pregunta?</statement>
 * <options>
 * <option>Opción 1</option>
 * <option>Opción 2</option>
 * </options>
 * <correct_answer index="0">Opción 1</correct_answer>
 * </question>
 * <question type="true_false" bloom_level="Entender" estimated_time="30">
 * <statement>Este enunciado es verdadero?</statement>
 * <correct_answer>Verdadero</correct_answer>
 * </question>
 * </test>
 */
public class XMLLoader implements TestLoader {

    @Override
    public Test loadTest(File file) throws IOException, IllegalArgumentException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            Element testElement = document.getDocumentElement();
            if (!"test".equals(testElement.getTagName())) {
                throw new IllegalArgumentException("El elemento raíz del XML debe ser 'test'.");
            }

            String testName = testElement.getAttribute("name");
            Test test = new Test(testName.isEmpty() ? file.getName().replace(".xml", "") : testName);

            NodeList questionNodes = document.getElementsByTagName("question");
            if (questionNodes.getLength() == 0) {
                throw new IllegalArgumentException("El archivo XML no contiene elementos 'question'.");
            }

            for (int i = 0; i < questionNodes.getLength(); i++) {
                Node node = questionNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element questionElement = (Element) node;

                    String type = questionElement.getAttribute("type");
                    String bloomLevelStr = questionElement.getAttribute("bloom_level");
                    String estimatedTimeStr = questionElement.getAttribute("estimated_time");

                    BloomLevel bloomLevel = BloomLevel.fromString(bloomLevelStr);
                    if (bloomLevel == null) {
                        throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Nivel de Bloom inválido: " + bloomLevelStr);
                    }

                    int estimatedTime;
                    try {
                        estimatedTime = Integer.parseInt(estimatedTimeStr);
                        if (estimatedTime <= 0) {
                            throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Tiempo estimado debe ser un número positivo.");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Tiempo estimado inválido: " + estimatedTimeStr);
                    }

                    String statement = getElementTextContent(questionElement, "statement");
                    if (statement.isEmpty()) {
                        throw new IllegalArgumentException("Pregunta " + (i + 1) + ": El enunciado de la pregunta no puede estar vacío.");
                    }

                    Question question;
                    if ("multiple_choice".equalsIgnoreCase(type)) {
                        List<String> options = getStrings(questionElement, i);

                        Element correctAnswerElement = (Element) questionElement.getElementsByTagName("correct_answer").item(0);
                        if (correctAnswerElement == null) {
                            throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Elemento <correct_answer> faltante para selección múltiple.");
                        }
                        String correctAnsText = correctAnswerElement.getTextContent().trim();
                        String correctAnsIndexStr = correctAnswerElement.getAttribute("index");

                        if (!correctAnsIndexStr.isEmpty()) {
                            try {
                                int correctAnsIndex = Integer.parseInt(correctAnsIndexStr);
                                question = new MultipleChoiceQuestion(statement, bloomLevel, estimatedTime, options, correctAnsIndex);
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Índice de respuesta correcta inválido: " + correctAnsIndexStr);
                            }
                        } else if (!correctAnsText.isEmpty()) {
                            question = new MultipleChoiceQuestion(statement, bloomLevel, estimatedTime, options, correctAnsText);
                        } else {
                            throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Respuesta correcta (texto o índice) faltante para selección múltiple.");
                        }

                    } else if ("true_false".equalsIgnoreCase(type)) {
                        String correctAnsText = getElementTextContent(questionElement, "correct_answer");
                        if (correctAnsText.isEmpty()) {
                            throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Respuesta correcta faltante para verdadero/falso.");
                        }
                        question = new TrueFalseQuestion(statement, bloomLevel, estimatedTime, correctAnsText);
                    } else {
                        throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Tipo de pregunta XML no soportado: " + type);
                    }
                    test.addQuestion(question);
                }
            }
            if (test.getNumberOfItems() == 0) {
                throw new IllegalArgumentException("El archivo XML no contiene preguntas válidas.");
            }
            return test;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IOException("Error al parsear el archivo XML: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar las excepciones ya detalladas
        } catch (Exception e) {
            throw new IllegalArgumentException("Error inesperado al procesar el archivo XML: " + e.getMessage(), e);
        }
    }

    private static List<String> getStrings(Element questionElement, int i) {
        List<String> options = new ArrayList<>();
        NodeList optionNodes = questionElement.getElementsByTagName("option");
        if (optionNodes.getLength() == 0) {
            throw new IllegalArgumentException("Pregunta " + (i + 1) + ": Las preguntas de selección múltiple deben tener opciones.");
        }
        for (int j = 0; j < optionNodes.getLength(); j++) {
            options.add(optionNodes.item(j).getTextContent().trim());
        }
        return options;
    }


    private String getElementTextContent(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
}