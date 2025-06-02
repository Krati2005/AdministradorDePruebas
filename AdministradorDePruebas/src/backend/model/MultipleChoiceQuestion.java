// backend/model/MultipleChoiceQuestion.java
package backend.model;

import java.util.List;
import java.util.Objects;

public class MultipleChoiceQuestion extends Question {
    private List<String> options; // Opciones de respuesta
    private final String correctAnswer; // La respuesta correcta (texto de la opción)
    private int correctOptionIndex; // Índice de la respuesta correcta


    public MultipleChoiceQuestion(String statement, BloomLevel bloomLevel, int estimatedTime,
                                  List<String> options, String correctAnswer, int correctOptionIndex) {
        super(statement, bloomLevel, estimatedTime);
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Las opciones de respuesta no pueden ser nulas o vacías.");
        }
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta correcta (texto) no puede ser nula o vacía.");
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("El índice de la respuesta correcta está fuera de los límites de las opciones.");
        }
        // Verificar que el texto de la respuesta correcta coincide con la opción en el índice
        if (!options.get(correctOptionIndex).equalsIgnoreCase(correctAnswer)) {
            throw new IllegalArgumentException("El texto de la respuesta correcta no coincide con la opción en el índice especificado.");
        }
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.correctOptionIndex = correctOptionIndex;
    }

    public MultipleChoiceQuestion(String statement, BloomLevel bloomLevel, int estimatedTime,
                                  List<String> options, int correctOptionIndex) {
        super(statement, bloomLevel, estimatedTime);
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Las opciones de respuesta no pueden ser nulas o vacías.");
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("El índice de la respuesta correcta está fuera de los límites de las opciones.");
        }
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.correctAnswer = options.get(correctOptionIndex); // Inferir el texto de la respuesta correcta
    }

    public MultipleChoiceQuestion(String statement, BloomLevel bloomLevel, int estimatedTime,
                                  List<String> options, String correctAnswer) {
        super(statement, bloomLevel, estimatedTime);
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Las opciones de respuesta no pueden ser nulas o vacías.");
        }
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta correcta (texto) no puede ser nula o vacía.");
        }
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.correctOptionIndex = -1; // Inicializar en -1 para indicar que aún no se ha encontrado
        // Buscar el índice de la respuesta correcta
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).equalsIgnoreCase(correctAnswer)) {
                this.correctOptionIndex = i;
                break;
            }
        }
        if (this.correctOptionIndex == -1) {
            throw new IllegalArgumentException("El texto de la respuesta correcta no se encontró entre las opciones proporcionadas.");
        }
    }


    /**
     * Obtiene la lista de opciones de respuesta.
     * @return Una lista de cadenas, cada una representando una opción.
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Establece la lista de opciones de respuesta.
     * @param options La nueva lista de opciones.
     */
    public void setOptions(List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Las opciones de respuesta no pueden ser nulas o vacías.");
        }
        this.options = options;
        if (correctOptionIndex != -1 && (correctOptionIndex >= options.size() || !options.get(correctOptionIndex).equalsIgnoreCase(correctAnswer))) {
            this.correctOptionIndex = -1; // Marcar como inválido
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).equalsIgnoreCase(correctAnswer)) {
                    this.correctOptionIndex = i;
                    break;
                }
            }
        } else if (correctOptionIndex == -1 && correctAnswer != null) {
            // Si teníamos un texto pero no un índice, intentamos encontrar el índice
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).equalsIgnoreCase(correctAnswer)) {
                    this.correctOptionIndex = i;
                    break;
                }
            }
        }
    }

    /**
     * Obtiene la respuesta correcta (texto de la opción).
     * @return El texto de la respuesta correcta.
     */
    public String getCorrectAnswerText() {
        return correctAnswer;
    }

    /**
     * Obtiene el índice de la respuesta correcta.
     * @return El índice de la respuesta correcta.
     */
    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }


    @Override
    public boolean isCorrect() {
        String userAnswer = getUserAnswer();
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        // Opción 1: Comparar por el texto de la respuesta
        if (correctAnswer != null && userAnswer.trim().equalsIgnoreCase(correctAnswer.trim())) {
            return true;
        }

        // Opción 2: Comparar por el índice de la respuesta
        try {
            int userSelectedOptionIndex = Integer.parseInt(userAnswer.trim());
            // Si el índice del usuario corresponde al índice de la respuesta correcta
            if (userSelectedOptionIndex >= 0 && userSelectedOptionIndex < options.size() && userSelectedOptionIndex == correctOptionIndex) {
                return true;
            }
        } catch (NumberFormatException e) {
            // La respuesta del usuario no es un número, así que no se compara por índice.
        }

        return false;
    }

    /**
     * Devuelve el tipo de pregunta.
     * @return La cadena "Selección Múltiple".
     */
    @Override
    public String getType() {
        return "Selección Múltiple";
    }
}