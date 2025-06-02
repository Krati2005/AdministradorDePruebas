// frontend/MainFrame.java
package frontend;

import backend.TestManager;
import backend.event.BackendEvent;
import backend.event.QuestionUpdatedEvent;
import backend.event.TestFinishedEvent;
import backend.event.TestLoadedEvent;
import backend.observer.BackendObserver;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class MainFrame extends JFrame implements BackendObserver {

    private final TestManager testManager;
    private JPanel currentPanel; // Panel que se está mostrando actualmente
    private JLabel itemsCountLabel;
    private JLabel totalTimeLabel;
    private JButton loadFileButton;
    private JButton startTestButton;

    private TestPanel testApplicationPanel;
    private ResultSummaryPanel resultSummaryPanel;
    private ReviewPanel reviewPanel;

    private enum AppState {
        INITIAL,
        TEST_IN_PROGRESS,
        TEST_FINISHED_SUMMARY,
        TEST_REVIEW
    }
    private AppState currentAppState;


    public MainFrame(TestManager testManager) {
        this.testManager = testManager;
        this.testManager.addObserver(this);

        setTitle("Administrador de Pruebas - Taxonomía de Bloom");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana

        initUI(); // Inicializar los componentes de la interfaz de usuario
        showInitialPanel();
        currentAppState = AppState.INITIAL; // Establecer el estado inicial
    }
    private void initUI() {
        JPanel initialPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        loadFileButton = new JButton("Cargar Ítems desde Archivo");
        loadFileButton.addActionListener(e -> selectFileToLoad());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        initialPanel.add(loadFileButton, gbc);

        itemsCountLabel = new JLabel("Cantidad de ítems: N/A");
        gbc.gridy = 1;
        initialPanel.add(itemsCountLabel, gbc);

        totalTimeLabel = new JLabel("Tiempo total estimado: N/A");
        gbc.gridy = 2;
        initialPanel.add(totalTimeLabel, gbc);

        startTestButton = new JButton("Iniciar Prueba");
        startTestButton.setEnabled(false);
        startTestButton.addActionListener(e -> startTest());
        gbc.gridy = 3;
        initialPanel.add(startTestButton, gbc);
    }
    public void showInitialPanel() {
        if (currentPanel != null) {
            remove(currentPanel); // Quitar el panel anterior si existe
        }
        JPanel initialPanelToDisplay = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Volvemos a añadir los componentes al panel para que se muestren correctamente
        initialPanelToDisplay.add(loadFileButton, gbc);
        gbc.gridy = 1;
        initialPanelToDisplay.add(itemsCountLabel, gbc);
        gbc.gridy = 2;
        initialPanelToDisplay.add(totalTimeLabel, gbc);
        gbc.gridy = 3;
        initialPanelToDisplay.add(startTestButton, gbc);

        // Resetear el estado visual del panel inicial
        resetInitialPanelDisplay();

        currentPanel = initialPanelToDisplay;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();    // Repintar el componente
        currentAppState = AppState.INITIAL;
    }

    private void selectFileToLoad() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Archivo de Prueba");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV y XML", "csv", "xml")); // Filtro de extensiones

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) { // Si el usuario selecciona un archivo y pulsa "Abrir"
            File selectedFile = fileChooser.getSelectedFile();
            try {
                testManager.loadTestFromFile(selectedFile); // Intentar cargar la prueba
            } catch (IOException | IllegalArgumentException ex) {
                // Mostrar un mensaje de error si la carga falla
                JOptionPane.showMessageDialog(this,
                        "Error al cargar la prueba: " + ex.getMessage(),
                        "Error de Carga",
                        JOptionPane.ERROR_MESSAGE);
                resetInitialPanelDisplay(); // Restablecer la visualización si hay un error
            }
        }
    }

    private void resetInitialPanelDisplay() {
        itemsCountLabel.setText("Cantidad de ítems: N/A");
        totalTimeLabel.setText("Tiempo total estimado: N/A");
        startTestButton.setEnabled(false);
    }

    private void startTest() {
        try {
            testManager.startTest(); // Iniciar la prueba a través del TestManager
            currentAppState = AppState.TEST_IN_PROGRESS; // Cambiar estado a "prueba en progreso"
        } catch (IllegalStateException ex) {
            // Mostrar un mensaje de error si la prueba no se puede iniciar
            JOptionPane.showMessageDialog(this,
                    "Error al iniciar la prueba: " + ex.getMessage(),
                    "Error de Inicio",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPanel(JPanel newPanel) {
        if (currentPanel != null) {
            remove(currentPanel); // Remover el panel actualmente visible
        }
        currentPanel = newPanel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void onBackendEvent(BackendEvent event) {
        SwingUtilities.invokeLater(() -> {
            if (event instanceof TestLoadedEvent) {
                TestLoadedEvent loadedEvent = (TestLoadedEvent) event;
                itemsCountLabel.setText("Cantidad de ítems: " + loadedEvent.getNumberOfItems()); // Actualizar cantidad de ítems
                totalTimeLabel.setText("Tiempo total estimado: " + loadedEvent.getTotalEstimatedTime() + " segundos"); // Actualizar tiempo total
                startTestButton.setEnabled(true); // Habilitar el botón de iniciar prueba
                currentAppState = AppState.INITIAL;
            } else if (event instanceof QuestionUpdatedEvent) {
                QuestionUpdatedEvent qe = (QuestionUpdatedEvent) event;
                if (currentAppState == AppState.TEST_IN_PROGRESS) { // Si la prueba está en progreso
                    if (testApplicationPanel == null) {
                        testApplicationPanel = new TestPanel(testManager); // Crear TestPanel si no existe
                    }
                    // Mostrar la pregunta en el TestPanel
                    testApplicationPanel.displayQuestion(qe.getCurrentQuestion(), qe.getCurrentQuestionIndex(),
                            qe.getTotalQuestions(), qe.canGoBack(), qe.isLastQuestion());
                    showPanel(testApplicationPanel); // Mostrar el TestPanel
                } else if (currentAppState == AppState.TEST_REVIEW) { // Si estamos en modo revisión
                    if (reviewPanel == null) {
                        reviewPanel = new ReviewPanel(testManager); // Crear ReviewPanel si no existe
                    }
                    // Mostrar la pregunta en el ReviewPanel
                    reviewPanel.displayReviewQuestion(qe.getCurrentQuestion(), qe.getCurrentQuestionIndex(),
                            qe.getTotalQuestions(), qe.canGoBack(), qe.isLastQuestion());
                    showPanel(reviewPanel); // Mostrar el ReviewPanel
                }
            } else if (event instanceof TestFinishedEvent) {
                TestFinishedEvent fe = (TestFinishedEvent) event;
                if (resultSummaryPanel == null) {
                    resultSummaryPanel = new ResultSummaryPanel(testManager, this);
                }
                resultSummaryPanel.displayResults(fe.getBloomPercentages(), fe.getItemTypePercentages());
                showPanel(resultSummaryPanel); // Mostrar el ResultSummaryPanel
                currentAppState = AppState.TEST_FINISHED_SUMMARY; // Cambiar estado a resumen
            }
        });
    }

    public void setAppStateToReview() {
        this.currentAppState = AppState.TEST_REVIEW;
    }

    public void setAppStateToInitial() {
        this.currentAppState = AppState.INITIAL;
        showInitialPanel(); // Volver a mostrar el panel inicial. Esto también reseteará su visualización.
    }
}