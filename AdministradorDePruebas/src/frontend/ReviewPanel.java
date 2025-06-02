// frontend/ReviewPanel.java
package frontend;

import backend.TestManager;
import backend.model.MultipleChoiceQuestion;
import backend.model.Question;
import backend.model.TrueFalseQuestion;

import javax.swing.*;
import java.awt.*;


public class ReviewPanel extends JPanel {

    private final TestManager testManager;
    private JLabel questionNumberLabel;
    private JTextArea statementArea;
    private JPanel optionsPanel; // Contenedor para opciones y estado
    private JLabel resultLabel; // Para indicar Correcta/Incorrecta

    private JButton backButton;
    private JButton nextButton;
    private JButton backToSummaryButton; // Nuevo botón para volver al resumen

    public ReviewPanel(TestManager testManager) {
        this.testManager = testManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        questionNumberLabel = new JLabel("Pregunta X de Y (Revisión)");
        questionNumberLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        questionNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statementArea = new JTextArea();
        statementArea.setWrapStyleWord(true);
        statementArea.setLineWrap(true);
        statementArea.setEditable(false);
        statementArea.setFont(new Font("Serif", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(statementArea);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        resultLabel = new JLabel(); // Para mostrar "Correcta" o "Incorrecta"
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        backButton = new JButton("Volver Atrás");
        backButton.addActionListener(e -> testManager.goToPreviousQuestion());

        nextButton = new JButton("Avanzar a la Siguiente");
        nextButton.addActionListener(e -> testManager.goToNextQuestion());

        backToSummaryButton = new JButton("Volver a Resumen");
        backToSummaryButton.addActionListener(e -> testManager.returnToSummary());
    }

    private void setupLayout() {
        add(questionNumberLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(new JScrollPane(statementArea), BorderLayout.CENTER);
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(resultLabel, BorderLayout.NORTH); // Resultado de la pregunta
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(backToSummaryButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    public void displayReviewQuestion(Question question, int questionIndex, int totalQuestions, boolean canGoBack, boolean isLastQuestion) {
        questionNumberLabel.setText("Pregunta " + (questionIndex + 1) + " de " + totalQuestions + " (Revisión)");
        statementArea.setText(question.getStatement());
        statementArea.setCaretPosition(0);

        optionsPanel.removeAll();
        ButtonGroup optionGroup = new ButtonGroup();

        Color correctColor = new Color(144, 238, 144); // Verde claro
        Color incorrectColor = new Color(255, 160, 122); // Naranja claro

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            for (int i = 0; i < mcq.getOptions().size(); i++) {
                JRadioButton radioButton = new JRadioButton(mcq.getOptions().get(i));
                radioButton.setEnabled(false); // Deshabilitar para que no se pueda cambiar la respuesta
                optionGroup.add(radioButton);
                optionsPanel.add(radioButton);

                boolean isUserAnswer = false;
                try {
                    int userSavedIndex = Integer.parseInt(question.getUserAnswer());
                    if (userSavedIndex == i) {
                        radioButton.setSelected(true);
                        isUserAnswer = true;
                    }
                } catch (NumberFormatException e) {
                    if (question.getUserAnswer().equalsIgnoreCase(mcq.getOptions().get(i))) {
                        radioButton.setSelected(true);
                        isUserAnswer = true;
                    }
                }

                if (isUserAnswer) {
                    if (question.isCorrect()) {
                        radioButton.setBackground(correctColor); // Respuesta del usuario es correcta
                        radioButton.setOpaque(true);
                    } else {
                        radioButton.setBackground(incorrectColor); // Respuesta del usuario es incorrecta
                        radioButton.setOpaque(true);
                    }
                }
                // Marcar la respuesta correcta si el usuario no la acertó
                if (!question.isCorrect() && i == mcq.getCorrectOptionIndex()) {
                    radioButton.setBackground(correctColor); // Resaltar la opción correcta
                    radioButton.setOpaque(true);
                }
            }
        } else if (question instanceof TrueFalseQuestion) {
            TrueFalseQuestion tfq = (TrueFalseQuestion) question;

            JRadioButton trueButton = new JRadioButton("Verdadero");
            trueButton.setEnabled(false);
            optionGroup.add(trueButton);
            optionsPanel.add(trueButton);

            JRadioButton falseButton = new JRadioButton("Falso");
            falseButton.setEnabled(false);
            optionGroup.add(falseButton);
            optionsPanel.add(falseButton);

            String userAnswer = question.getUserAnswer();
            boolean userSelectedTrue = "Verdadero".equalsIgnoreCase(userAnswer) || "V".equalsIgnoreCase(userAnswer) || "True".equalsIgnoreCase(userAnswer);
            boolean userSelectedFalse = "Falso".equalsIgnoreCase(userAnswer) || "F".equalsIgnoreCase(userAnswer) || "False".equalsIgnoreCase(userAnswer);

            if (userSelectedTrue) {
                trueButton.setSelected(true);
                if (question.isCorrect()) {
                    trueButton.setBackground(correctColor);
                } else {
                    trueButton.setBackground(incorrectColor);
                }
                trueButton.setOpaque(true);
            } else if (userSelectedFalse) {
                falseButton.setSelected(true);
                if (question.isCorrect()) {
                    falseButton.setBackground(correctColor);
                } else {
                    falseButton.setBackground(incorrectColor);
                }
                falseButton.setOpaque(true);
            }

            // Resaltar la respuesta correcta si el usuario no la acertó
            if (!question.isCorrect()) {
                if (tfq.getCorrectAnswer()) { // Si la respuesta correcta era Verdadero
                    trueButton.setBackground(correctColor);
                    trueButton.setOpaque(true);
                } else { // Si la respuesta correcta era Falso
                    falseButton.setBackground(correctColor);
                    falseButton.setOpaque(true);
                }
            }
        }

        if (question.isCorrect()) {
            resultLabel.setText("¡Correcta!");
            resultLabel.setForeground(new Color(0, 128, 0)); // Verde oscuro
        } else {
            resultLabel.setText("Incorrecta.");
            resultLabel.setForeground(new Color(200, 0, 0)); // Rojo oscuro
        }

        backButton.setEnabled(canGoBack);
        nextButton.setEnabled(!isLastQuestion);

        optionsPanel.revalidate();
        optionsPanel.repaint();
        revalidate();
        repaint();
    }
}