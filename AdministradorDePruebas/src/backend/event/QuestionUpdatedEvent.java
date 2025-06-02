// backend/event/QuestionUpdatedEvent.java
package backend.event;

import backend.model.Question;

public class QuestionUpdatedEvent extends BackendEvent {
    private final Question currentQuestion;
    private final int currentQuestionIndex;
    private final int totalQuestions;
    private final boolean canGoBack;
    private final boolean isLastQuestion;


    public QuestionUpdatedEvent(Question currentQuestion, int currentQuestionIndex,
                                int totalQuestions, boolean canGoBack, boolean isLastQuestion) {
        this.currentQuestion = currentQuestion;
        this.currentQuestionIndex = currentQuestionIndex;
        this.totalQuestions = totalQuestions;
        this.canGoBack = canGoBack;
        this.isLastQuestion = isLastQuestion;
    }

    /**
     * Obtiene la pregunta actual.
     * @return La pregunta actual.
     */
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Obtiene el índice de la pregunta actual.
     * @return El índice.
     */
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    /**
     * Obtiene el número total de preguntas.
     * @return El total de preguntas.
     */
    public int getTotalQuestions() {
        return totalQuestions;
    }

    /**
     * Indica si es posible volver a la pregunta anterior.
     * @return true si se puede volver, false en caso contrario.
     */
    public boolean canGoBack() {
        return canGoBack;
    }

    /**
     * Indica si la pregunta actual es la última de la prueba.
     * @return true si es la última, false en caso contrario.
     */
    public boolean isLastQuestion() {
        return isLastQuestion;
    }
}