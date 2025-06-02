// backend/TestManager.java
package backend;

import backend.event.*;
import backend.model.*;
import backend.observer.BackendObserver;
import backend.file.CSVLoader; // Todavía no creada, pero la referencia
import backend.file.XMLLoader; // Todavía no creada, pero la referencia

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList; // Para manejo seguro de observadores en multithreading

public class TestManager {
    private Test currentTest;
    private int currentQuestionIndex;
    private final List<BackendObserver> observers;

    /**
     * Constructor para TestManager.
     */
    public TestManager() {
        this.observers = new CopyOnWriteArrayList<>(); // Permite iteración segura mientras se modifican
        this.currentTest = null;
        this.currentQuestionIndex = -1; // No hay prueba cargada inicialmente
    }

    public void addObserver(BackendObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Elimina un observador de la lista.
     * @param observer El observador a eliminar.
     */
    public void removeObserver(BackendObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica a todos los observadores registrados sobre un evento.
     * @param event El evento a notificar.
     */
    private void notifyObservers(BackendEvent event) {
        for (BackendObserver observer : observers) {
            observer.onBackendEvent(event);
        }
    }

    public void loadTestFromFile(File file) throws IOException, IllegalArgumentException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("El archivo no existe o es nulo.");
        }

        String fileName = file.getName();
        Test loadedTest;

        if (fileName.toLowerCase().endsWith(".csv")) {
            loadedTest = new CSVLoader().loadTest(file);
        } else if (fileName.toLowerCase().endsWith(".xml")) {
            loadedTest = new XMLLoader().loadTest(file);
        } else {
            throw new IllegalArgumentException("Formato de archivo no soportado. Por favor, use .csv o .xml");
        }

        if (loadedTest.getNumberOfItems() == 0) {
            throw new IllegalArgumentException("El archivo no contiene ítems válidos para la prueba.");
        }

        this.currentTest = loadedTest;
        this.currentQuestionIndex = -1; // Resetear índice al cargar nueva prueba
        // Notificar al frontend que la prueba ha sido cargada
        notifyObservers(new TestLoadedEvent(currentTest, currentTest.getNumberOfItems(), currentTest.getTotalEstimatedTime()));
    }
    public void startTest() throws IllegalStateException {
        if (currentTest == null || currentTest.getNumberOfItems() == 0) {
            throw new IllegalStateException("No hay una prueba cargada para iniciar.");
        }
        currentTest.resetUserAnswers(); // Asegurarse de que las respuestas anteriores se borren
        currentQuestionIndex = 0;
        notifyCurrentQuestionUpdate();
    }

    public void goToNextQuestion() throws IllegalStateException {
        if (currentTest == null) {
            throw new IllegalStateException("No hay una prueba cargada.");
        }
        if (currentQuestionIndex < currentTest.getNumberOfItems() - 1) {
            currentQuestionIndex++;
            notifyCurrentQuestionUpdate();
        } else if (currentQuestionIndex == currentTest.getNumberOfItems() - 1) {
            // Ya es la última pregunta, significa que se envió la prueba
            finishTest();
        }
    }

    public void goToPreviousQuestion() throws IllegalStateException {
        if (currentTest == null) {
            throw new IllegalStateException("No hay una prueba cargada.");
        }
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            notifyCurrentQuestionUpdate();
        } else {
            throw new IllegalStateException("Ya estás en la primera pregunta.");
        }
    }

    public void saveUserAnswer(String answer) {
        if (currentTest != null && currentQuestionIndex >= 0 && currentQuestionIndex < currentTest.getNumberOfItems()) {
            currentTest.getQuestions().get(currentQuestionIndex).setUserAnswer(answer);
        }
    }

    public Question getCurrentQuestion() {
        if (currentTest != null && currentQuestionIndex >= 0 && currentQuestionIndex < currentTest.getNumberOfItems()) {
            return currentTest.getQuestions().get(currentQuestionIndex);
        }
        return null;
    }

    private void notifyCurrentQuestionUpdate() {
        if (currentTest != null && currentQuestionIndex != -1) {
            Question question = currentTest.getQuestions().get(currentQuestionIndex);
            boolean canGoBack = currentQuestionIndex > 0;
            boolean isLastQuestion = (currentQuestionIndex == currentTest.getNumberOfItems() - 1);
            notifyObservers(new QuestionUpdatedEvent(question, currentQuestionIndex,
                    currentTest.getNumberOfItems(), canGoBack, isLastQuestion));
        }
    }

    /**
     * Finaliza la prueba, calcula los resultados y los notifica.
     */
    public void finishTest() {
        if (currentTest == null) {
            throw new IllegalStateException("No hay una prueba para finalizar.");
        }

        Map<BloomLevel, Integer> correctByBloom = new HashMap<>();
        Map<BloomLevel, Integer> totalByBloom = new HashMap<>();
        Map<String, Integer> correctByType = new HashMap<>();
        Map<String, Integer> totalByType = new HashMap<>();

        for (Question q : currentTest.getQuestions()) {
            // Cálculos por nivel de Bloom
            totalByBloom.put(q.getBloomLevel(), totalByBloom.getOrDefault(q.getBloomLevel(), 0) + 1);
            if (q.isCorrect()) {
                correctByBloom.put(q.getBloomLevel(), correctByBloom.getOrDefault(q.getBloomLevel(), 0) + 1);
            }

            // Cálculos por tipo de ítem
            totalByType.put(q.getType(), totalByType.getOrDefault(q.getType(), 0) + 1);
            if (q.isCorrect()) {
                correctByType.put(q.getType(), correctByType.getOrDefault(q.getType(), 0) + 1);
            }
        }

        Map<BloomLevel, Double> bloomPercentages = new HashMap<>();
        for (Map.Entry<BloomLevel, Integer> entry : totalByBloom.entrySet()) {
            BloomLevel level = entry.getKey();
            int total = entry.getValue();
            int correct = correctByBloom.getOrDefault(level, 0);
            bloomPercentages.put(level, (total > 0) ? (double) correct * 100 / total : 0.0);
        }

        Map<String, Double> itemTypePercentages = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalByType.entrySet()) {
            String type = entry.getKey();
            int total = entry.getValue();
            int correct = correctByType.getOrDefault(type, 0);
            itemTypePercentages.put(type, (total > 0) ? (double) correct * 100 / total : 0.0);
        }

        // Notificar los resultados al frontend
        notifyObservers(new TestFinishedEvent(bloomPercentages, itemTypePercentages, currentTest.getQuestions()));
    }

    public void startReview() {
        if (currentTest == null) {
            throw new IllegalStateException("No hay una prueba para revisar.");
        }
        currentQuestionIndex = 0; // Ponerse en la primera pregunta para revisión
        notifyCurrentQuestionUpdate(); // Notificar para mostrar la primera pregunta en modo revisión
    }

    /**
     * Vuelve al resumen de la prueba después de la revisión.
     */
    public void returnToSummary() {
        if (currentTest == null) {
            throw new IllegalStateException("No hay una prueba cargada.");
        }
        finishTest(); // Esto recalcula y notifica los resultados, mostrando el resumen
    }
}