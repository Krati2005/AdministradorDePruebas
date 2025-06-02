// backend/model/Test.java
package backend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una prueba o examen, que contiene una colección de preguntas.
 */
public class Test {
    private List<Question> questions;
    private String name; // Nombre o identificador de la prueba (opcional)

    /**
     * Constructor por defecto para una nueva prueba.
     */
    public Test() {
        this.questions = new ArrayList<>();
        this.name = "Nueva Prueba";
    }

    /**
     * Constructor para una prueba con un nombre específico.
     * @param name El nombre de la prueba.
     */
    public Test(String name) {
        this.questions = new ArrayList<>();
        this.name = name;
    }

    /**
     * Añade una pregunta a la prueba.
     * @param question La pregunta a añadir.
     */
    public void addQuestion(Question question) {
        if (question != null) {
            this.questions.add(question);
        }
    }

    /**
     * Elimina una pregunta de la prueba.
     * @param question La pregunta a eliminar.
     * @return true si la pregunta fue eliminada, false en caso contrario.
     */
    public boolean removeQuestion(Question question) {
        return this.questions.remove(question);
    }

    /**
     * Obtiene una lista inmutable de todas las preguntas en la prueba.
     * @return Una lista de preguntas.
     */
    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    /**
     * Obtiene el número total de ítems (preguntas) en la prueba.
     * @return La cantidad de preguntas.
     */
    public int getNumberOfItems() {
        return questions.size();
    }

    /**
     * Calcula el tiempo total estimado para completar la prueba.
     * @return El tiempo total estimado en segundos.
     */
    public int getTotalEstimatedTime() {
        return questions.stream()
                .mapToInt(Question::getEstimatedTime)
                .sum();
    }

    /**
     * Obtiene el nombre de la prueba.
     * @return El nombre de la prueba.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la prueba.
     * @param name El nuevo nombre de la prueba.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Reinicia las respuestas de los usuarios para todas las preguntas de la prueba.
     */
    public void resetUserAnswers() {
        for (Question question : questions) {
            question.setUserAnswer(""); // Establecer la respuesta del usuario como vacía
        }
    }
}