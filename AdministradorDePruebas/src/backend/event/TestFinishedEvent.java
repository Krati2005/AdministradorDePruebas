// backend/event/TestFinishedEvent.java
package backend.event;

import java.util.Map;
import java.util.List;
import backend.model.BloomLevel;
import backend.model.Question;


public class TestFinishedEvent extends BackendEvent {
    // Mapa para porcentajes correctos por nivel de Bloom
    private final Map<BloomLevel, Double> bloomPercentages;
    // Mapa para porcentajes correctos por tipo de ítem
    private final Map<String, Double> itemTypePercentages;
    // Lista de preguntas con las respuestas del usuario para revisión
    private final List<Question> questionsForReview;


    public TestFinishedEvent(Map<BloomLevel, Double> bloomPercentages,
                             Map<String, Double> itemTypePercentages,
                             List<Question> questionsForReview) {
        this.bloomPercentages = bloomPercentages;
        this.itemTypePercentages = itemTypePercentages;
        this.questionsForReview = questionsForReview;
    }

    public Map<BloomLevel, Double> getBloomPercentages() {
        return bloomPercentages;
    }

    public Map<String, Double> getItemTypePercentages() {
        return itemTypePercentages;
    }

    public List<Question> getQuestionsForReview() {
        return questionsForReview;
    }
}