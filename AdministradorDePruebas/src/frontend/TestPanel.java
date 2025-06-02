// frontend/TestPanel.java
package frontend;

import backend.TestManager;
import backend.model.MultipleChoiceQuestion;
import backend.model.Question;
import backend.model.TrueFalseQuestion;

import javax.swing.*;
import java.awt.*;


public class TestPanel extends JPanel {

    private final TestManager testManager;
    private JLabel questionNumberLabel;
    private JTextArea statementArea;
    private JPanel optionsPanel; // Contenedor para opciones (botones de radio, etc.)
    private ButtonGroup optionGroup; // Para preguntas de selección múltiple/verdadero-falso
    private JButton backButton;
    private JButton nextButton;

    private Question currentQuestionDisplayed; // Referencia a la pregunta actualmente mostrada

    public TestPanel(TestManager testManager) {
        this.testManager = testManager;
        setLayout(new BorderLayout(10, 10)); // Espaciado
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margen interior

        initComponents();
        setupLayout();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario del panel.
     */
    private void initComponents() {
        questionNumberLabel = new JLabel("Pregunta X de Y");
        questionNumberLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        questionNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statementArea = new JTextArea();
        statementArea.setWrapStyleWord(true);
        statementArea.setLineWrap(true);
        statementArea.setEditable(false);
        statementArea.setFont(new Font("Serif", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(statementArea);

        optionsPanel = new JPanel(); // El layout se definirá dinámicamente
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS)); // Por defecto vertical

        backButton = new JButton("Volver Atrás");
        backButton.addActionListener(e -> {
            saveCurrentAnswer(); // Guardar la respuesta actual antes de navegar
            testManager.goToPreviousQuestion();
        });

        nextButton = new JButton("Avanzar a la Siguiente");
        nextButton.addActionListener(e -> {
            saveCurrentAnswer(); // Guardar la respuesta actual antes de navegar
            testManager.goToNextQuestion();
        });
    }

    /**
     * Configura el diseño de los componentes en el panel.
     */
    private void setupLayout() {
        // Norte: Número de pregunta
        add(questionNumberLabel, BorderLayout.NORTH);

        // Centro: Enunciado de la pregunta y opciones
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(new JScrollPane(statementArea), BorderLayout.CENTER);
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Sur: Botones de navegación
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void displayQuestion(Question question, int questionIndex, int totalQuestions, boolean canGoBack, boolean isLastQuestion) {
        this.currentQuestionDisplayed = question;

        questionNumberLabel.setText("Pregunta " + (questionIndex + 1) + " de " + totalQuestions);
        statementArea.setText(question.getStatement());
        statementArea.setCaretPosition(0);
        // Limpiar opciones anteriores
        optionsPanel.removeAll();
        optionGroup = new ButtonGroup();

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            for (int i = 0; i < mcq.getOptions().size(); i++) {
                JRadioButton radioButton = new JRadioButton(mcq.getOptions().get(i));
                radioButton.setActionCommand(String.valueOf(i));
                optionGroup.add(radioButton);
                optionsPanel.add(radioButton);

                if (!mcq.getUserAnswer().isEmpty()) {
                    try {
                        int savedIndex = Integer.parseInt(mcq.getUserAnswer());
                        if (savedIndex == i) {
                            radioButton.setSelected(true);
                        }
                    } catch (NumberFormatException e) {
                        if (mcq.getUserAnswer().equalsIgnoreCase(mcq.getOptions().get(i))) {
                            radioButton.setSelected(true);
                        }
                    }
                }
            }
        } else if (question instanceof TrueFalseQuestion) {
            JRadioButton trueButton = new JRadioButton("Verdadero");
            trueButton.setActionCommand("Verdadero");
            optionGroup.add(trueButton);
            optionsPanel.add(trueButton);

            JRadioButton falseButton = new JRadioButton("Falso");
            falseButton.setActionCommand("Falso");
            optionGroup.add(falseButton);
            optionsPanel.add(falseButton);

            // Seleccionar la respuesta del usuario si ya existe
            String userAnswer = question.getUserAnswer();
            if ("Verdadero".equalsIgnoreCase(userAnswer) || "V".equalsIgnoreCase(userAnswer) || "True".equalsIgnoreCase(userAnswer)) {
                trueButton.setSelected(true);
            } else if ("Falso".equalsIgnoreCase(userAnswer) || "F".equalsIgnoreCase(userAnswer) || "False".equalsIgnoreCase(userAnswer)) {
                falseButton.setSelected(true);
            }
        }

        // Actualizar estado de los botones de navegación
        backButton.setEnabled(canGoBack);
        if (isLastQuestion) {
            nextButton.setText("Enviar Respuestas");
        } else {
            nextButton.setText("Avanzar a la Siguiente");
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();
        revalidate();
        repaint();
    }

    /**
     * Guarda la respuesta seleccionada por el usuario para la pregunta actual.
     */
    private void saveCurrentAnswer() {
        if (currentQuestionDisplayed == null) {
            return;
        }

        String selectedAnswer = "";
        ButtonModel selectedButton = optionGroup.getSelection();
        if (selectedButton != null) {
            selectedAnswer = selectedButton.getActionCommand();
        }
        currentQuestionDisplayed.setUserAnswer(selectedAnswer);
        testManager.saveUserAnswer(selectedAnswer); // También notificar al manager si es necesario
    }
}