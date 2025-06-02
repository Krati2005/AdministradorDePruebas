// frontend/ResultSummaryPanel.java
package frontend;

import backend.TestManager;
import backend.model.BloomLevel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ResultSummaryPanel extends JPanel {

    private final TestManager testManager;
    private final MainFrame parentFrame; // Referencia al MainFrame para cambiar paneles
    private JLabel titleLabel;
    private JTextArea summaryTextArea;
    private JButton reviewButton;
    private JButton returnToMainButton;

    public ResultSummaryPanel(TestManager testManager, MainFrame parentFrame) {
        this.testManager = testManager;
        this.parentFrame = parentFrame; // Guardar la referencia al MainFrame
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        titleLabel = new JLabel("Resumen de Resultados de la Prueba");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        summaryTextArea = new JTextArea();
        summaryTextArea.setEditable(false);
        summaryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        summaryTextArea.setWrapStyleWord(true);
        summaryTextArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(summaryTextArea);

        reviewButton = new JButton("Revisar Respuestas");
        reviewButton.addActionListener(e -> {
            parentFrame.setAppStateToReview(); // Indicar a MainFrame que cambie a modo revisión
            testManager.startReview(); // Iniciar la revisión en el backend
        });

        returnToMainButton = new JButton("Volver a Pantalla Principal");
        returnToMainButton.addActionListener(e -> {
            parentFrame.setAppStateToInitial(); // Indicar a MainFrame que vuelva al estado inicial
        });
    }

    private void setupLayout() {
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(summaryTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(reviewButton);
        buttonPanel.add(returnToMainButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void displayResults(Map<BloomLevel, Double> bloomPercentages, Map<String, Double> itemTypePercentages) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Porcentaje de Respuestas Correctas por Nivel de Bloom ---\n");
        // Asegurarse de mostrar todos los niveles de Bloom en un orden consistente
        for (BloomLevel level : BloomLevel.values()) {
            sb.append(String.format("%-10s: %.2f%%\n", level.getName(), bloomPercentages.getOrDefault(level, 0.0)));
        }
        sb.append("\n--- Porcentaje de Respuestas Correctas por Tipo de Ítem ---\n");
        itemTypePercentages.forEach((type, percentage) ->
                sb.append(String.format("%-18s: %.2f%%\n", type, percentage))
        );

        summaryTextArea.setText(sb.toString());
        summaryTextArea.setCaretPosition(0);
    }
}