// backend/model/TrueFalseQuestion.java
package backend.model;

public class TrueFalseQuestion extends Question {
    private boolean correctAnswer; // La respuesta correcta (true para Verdadero, false para Falso)


    public TrueFalseQuestion(String statement, BloomLevel bloomLevel, int estimatedTime, boolean correctAnswer) {
        super(statement, bloomLevel, estimatedTime);
        this.correctAnswer = correctAnswer;
    }

    public TrueFalseQuestion(String statement, BloomLevel bloomLevel, int estimatedTime, String correctAnswerString) {
        super(statement, bloomLevel, estimatedTime);
        if (correctAnswerString == null || correctAnswerString.trim().isEmpty()) {
            throw new IllegalArgumentException("La cadena de respuesta correcta no puede ser nula o vacía.");
        }
        String normalizedAnswer = correctAnswerString.trim().toLowerCase();
        if (normalizedAnswer.equals("verdadero") || normalizedAnswer.equals("true") || normalizedAnswer.equals("v")) {
            this.correctAnswer = true;
        } else if (normalizedAnswer.equals("falso") || normalizedAnswer.equals("false") || normalizedAnswer.equals("f")) {
            this.correctAnswer = false;
        } else {
            throw new IllegalArgumentException("Formato de respuesta correcta no válido para Verdadero/Falso: " + correctAnswerString);
        }
    }


    /**
     * Obtiene la respuesta correcta booleana.
     * @return true si la respuesta correcta es Verdadero, false si es Falso.
     */
    public boolean getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Establece la respuesta correcta booleana.
     * @param correctAnswer La nueva respuesta correcta.
     */
    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean isCorrect() {
        String userAnswer = getUserAnswer();
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        String normalizedUserAnswer = userAnswer.trim().toLowerCase();

        if (this.correctAnswer) { // Si la respuesta correcta es Verdadero
            return normalizedUserAnswer.equals("verdadero") || normalizedUserAnswer.equals("true") || normalizedUserAnswer.equals("v");
        } else { // Si la respuesta correcta es Falso
            return normalizedUserAnswer.equals("falso") || normalizedUserAnswer.equals("false") || normalizedUserAnswer.equals("f");
        }
    }

    /**
     * Devuelve el tipo de pregunta.
     * @return La cadena "Verdadero/Falso".
     */
    @Override
    public String getType() {
        return "Verdadero/Falso";
    }
}