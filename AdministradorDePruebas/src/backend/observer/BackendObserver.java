// backend/observer/BackendObserver.java
package backend.observer;

import backend.event.BackendEvent;

public interface BackendObserver {
    /**
     * Método invocado cuando se produce un evento en el backend.
     * @param event El evento que ha ocurrido.
     */
    void onBackendEvent(BackendEvent event);
}