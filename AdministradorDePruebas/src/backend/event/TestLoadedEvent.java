// backend/event/TestLoadedEvent.java
package backend.event;

import backend.model.Test;

public class TestLoadedEvent extends BackendEvent {
    private final Test loadedTest;
    private final int numberOfItems;
    private final int totalEstimatedTime;

    public TestLoadedEvent(Test loadedTest, int numberOfItems, int totalEstimatedTime) {
        this.loadedTest = loadedTest;
        this.numberOfItems = numberOfItems;
        this.totalEstimatedTime = totalEstimatedTime;
    }

    /**
     * Obtiene el objeto Test cargado.
     * @return El Test cargado.
     */
    public Test getLoadedTest() {
        return loadedTest;
    }

    /**
     * Obtiene la cantidad de ítems de la prueba.
     * @return La cantidad de ítems.
     */
    public int getNumberOfItems() {
        return numberOfItems;
    }

    /**
     * Obtiene el tiempo total estimado de la prueba.
     * @return El tiempo total estimado en segundos.
     */
    public int getTotalEstimatedTime() {
        return totalEstimatedTime;
    }
}