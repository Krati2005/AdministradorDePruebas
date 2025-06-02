// backend/file/CSVLoader.java
package backend.file;

import backend.model.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CSVLoader implements TestLoader {

    private static final String CSV_DELIMITER = ";"; // Separador de columnas
    private static final String OPTIONS_DELIMITER = ","; // Separador de opciones para selección múltiple

    @Override
    public Test loadTest(File file) throws IOException, IllegalArgumentException {
        Test test = new Test(file.getName().replace(".csv", "")); // Nombre de la prueba basado en el archivo
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) { // Ignorar líneas vacías
                    continue;
                }
                String[] parts = line.split(CSV_DELIMITER, -1);

                // Se esperan 6 partes: tipo, enunciado, opciones, respuesta, nivel, tiempo
                if (parts.length != 6) {
                    throw new IllegalArgumentException("Línea " + lineNumber + ": Formato CSV inválido. Se esperaban 6 columnas, se encontraron " + parts.length + ".");
                }

                String questionType = parts[0].trim();
                String statement = parts[1].trim();
                String optionsRaw = parts[2].trim();
                String correctAnswer = parts[3].trim();
                String bloomLevelStr = parts[4].trim();
                String estimatedTimeStr = parts[5].trim();

                BloomLevel bloomLevel = BloomLevel.fromString(bloomLevelStr);
                if (bloomLevel == null) {
                    throw new IllegalArgumentException("Línea " + lineNumber + ": Nivel de Bloom inválido: " + bloomLevelStr);
                }

                int estimatedTime;
                try {
                    estimatedTime = Integer.parseInt(estimatedTimeStr);
                    if (estimatedTime <= 0) {
                        throw new IllegalArgumentException("Línea " + lineNumber + ": Tiempo estimado debe ser un número positivo.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Línea " + lineNumber + ": Tiempo estimado inválido: " + estimatedTimeStr);
                }

                Question question;
                if ("Selección Múltiple".equalsIgnoreCase(questionType)) {
                    List<String> options = new ArrayList<>();
                    if (!optionsRaw.isEmpty()) {
                        options = Arrays.asList(optionsRaw.split(OPTIONS_DELIMITER));
                        options.replaceAll(String::trim);
                    }
                    if (options.isEmpty()) {
                        throw new IllegalArgumentException("Línea " + lineNumber + ": Las preguntas de Selección Múltiple deben tener opciones.");
                    }

                    // Intentar cargar la respuesta correcta por índice o por texto
                    try {
                        int correctIndex = Integer.parseInt(correctAnswer.trim());
                        question = new MultipleChoiceQuestion(statement, bloomLevel, estimatedTime, options, correctIndex);
                    } catch (NumberFormatException e) {
                        // Si no es un número, intentar por texto
                        question = new MultipleChoiceQuestion(statement, bloomLevel, estimatedTime, options, correctAnswer);
                    }

                } else if ("Verdadero/Falso".equalsIgnoreCase(questionType)) {
                    question = new TrueFalseQuestion(statement, bloomLevel, estimatedTime, correctAnswer);
                } else {
                    throw new IllegalArgumentException("Línea " + lineNumber + ": Tipo de pregunta no soportado: " + questionType);
                }
                test.addQuestion(question);
            }
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo CSV: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error inesperado al procesar el archivo CSV en la línea " + lineNumber + ": " + e.getMessage(), e);
        }
        if (test.getNumberOfItems() == 0) {
            throw new IllegalArgumentException("El archivo CSV no contiene preguntas válidas.");
        }
        return test;
    }
}