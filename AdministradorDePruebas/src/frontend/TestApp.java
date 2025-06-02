// frontend/TestApp.java
package frontend;

import backend.TestManager; // Importar el TestManager del backend
import javax.swing.SwingUtilities;

public class TestApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crear la instancia del TestManager (backend)
            TestManager testManager = new TestManager();

            // Crear la ventana principal de la aplicación y pasarle el TestManager
            MainFrame mainFrame = new MainFrame(testManager);
            mainFrame.setVisible(true); // Hacer visible la ventana
        });
    }
}