// backend/model/Question.java
package backend.model;

/**
 * Clase abstracta que representa una pregunta genérica de la prueba.
 * Contiene atributos comunes a todas las preguntas y métodos abstractos
 * que deben ser implementados por las subclases.
 */
public abstract class Question {
    private String statement; // Enunciado de la pregunta
    private BloomLevel bloomLevel; // Nivel de la taxonomía de Bloom
    private int estimatedTime; // Tiempo estimado para resolver la pregunta en segundos
    private String userAnswer; // Respuesta del usuario a la pregunta

    public Question(String statement, BloomLevel bloomLevel, int estimatedTime) {
        if (statement == null || statement.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado de la pregunta no puede estar vacío.");
        }
        if (bloomLevel == null) {
            throw new IllegalArgumentException("El nivel de Bloom no puede ser nulo.");
        }
        if (estimatedTime <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser un valor positivo.");
        }
        this.statement = statement;
        this.bloomLevel = bloomLevel;
        this.estimatedTime = estimatedTime;
        this.userAnswer = ""; // Inicialmente vacía
    }

    /**
     * Obtiene el enunciado de la pregunta.
     * @return El enunciado de la pregunta.
     */
    public String getStatement() {
        return statement;
    }

    /**
     * Establece el enunciado de la pregunta.
     * @param statement El nuevo enunciado de la pregunta.
     */
    public void setStatement(String statement) {
        if (statement == null || statement.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado de la pregunta no puede estar vacío.");
        }
        this.statement = statement;
    }

    /**
     * Obtiene el nivel de la taxonomía de Bloom al que pertenece la pregunta.
     * @return El nivel de Bloom de la pregunta.
     */
    public BloomLevel getBloomLevel() {
        return bloomLevel;
    }

    /**
     * Establece el nivel de la taxonomía de Bloom de la pregunta.
     * @param bloomLevel El nuevo nivel de Bloom de la pregunta.
     */
    public void setBloomLevel(BloomLevel bloomLevel) {
        if (bloomLevel == null) {
            throw new IllegalArgumentException("El nivel de Bloom no puede ser nulo.");
        }
        this.bloomLevel = bloomLevel;
    }

    /**
     * Obtiene el tiempo estimado para resolver la pregunta.
     * @return El tiempo estimado en segundos.
     */
    public int getEstimatedTime() {
        return estimatedTime;
    }

    /**
     * Establece el tiempo estimado para resolver la pregunta.
     * @param estimatedTime El nuevo tiempo estimado en segundos.
     */
    public void setEstimatedTime(int estimatedTime) {
        if (estimatedTime <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser un valor positivo.");
        }
        this.estimatedTime = estimatedTime;
    }

    /**
     * Obtiene la respuesta del usuario para esta pregunta.
     * @return La respuesta del usuario.
     */
    public String getUserAnswer() {
        return userAnswer;
    }

    /**
     * Establece la respuesta del usuario para esta pregunta.
     * @param userAnswer La respuesta ingresada por el usuario.
     */
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer != null ? userAnswer : "";
    }

    /**
     * Método abstracto para obtener el tipo de pregunta
     * Debe ser implementado por las subclases.
     * @return Una cadena que describe el tipo de pregunta.
     */
    public abstract String getType();

    /**
     * Método abstracto para verificar si la respuesta del usuario es correcta.
     * Debe ser implementado por las subclases.
     * @return true si la respuesta del usuario es correcta, false en caso contrario.
     */
    public abstract boolean isCorrect();
}