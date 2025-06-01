import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import java.util.List;
import java.awt.Toolkit;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.sql.SQLException;

public class QuizGUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel welcomePanel;
    private JPanel quizPanel;
    private JPanel resultPanel;
    private JPanel reviewPanel;
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private JButton nextButton;
    private JButton submitButton;
    private JProgressBar progressBar;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private javax.swing.Timer timer;
    private int timeLeft;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<Question> questions;
    private List<Question> answeredQuestions;
    
    // Gen Z Color Palette - Vibrant & Modern
    private Color primaryGradientStart = new Color(138, 43, 226); // BlueViolet
    private Color primaryGradientEnd = new Color(30, 144, 255); // DodgerBlue
    private Color secondaryGradientStart = new Color(255, 20, 147); // DeepPink
    private Color secondaryGradientEnd = new Color(255, 165, 0); // Orange
    private Color backgroundColor = new Color(10, 10, 20); // Ultra Dark Navy
    private Color surfaceColor = new Color(25, 25, 40); // Dark Surface
    private Color cardColor = new Color(35, 35, 55); // Card Background
    private Color textColor = new Color(255, 255, 255); // Pure White
    private Color accentColor = new Color(0, 255, 127); // SpringGreen (Success)
    private Color errorColor = new Color(255, 69, 58); // SystemRed
    private Color warningColor = new Color(255, 214, 10); // SystemYellow
    private Color mutedText = new Color(155, 155, 170); // Muted Text
    private Color hoverColor = new Color(60, 60, 80); // Hover State
    
    // Dark mode colors (slightly different for variety)
    private Color darkBg = new Color(0, 0, 0); // Pure Black
    private Color darkSurface = new Color(18, 18, 18); // Almost Black
    private Color darkCard = new Color(28, 28, 28); // Dark Card
    private Color darkAccent = new Color(255, 0, 255); // Magenta
    
    private boolean isDarkMode = false;
    private JButton themeToggleButton;
    private JButton soundToggleButton;
    private JLabel difficultyLabel;
    private JProgressBar timeProgressBar;
    private Timer animationTimer;
    private float animationProgress = 0f;
    private boolean isPracticeMode = false;
    private int hintsRemaining = 3;
    private JButton hintButton;
    private JLabel hintsLabel;
    private JButton practiceModeButton;
    private JComboBox<String> categoryComboBox;
    private Map<String, Integer> highScores = new HashMap<>();
    private JLabel highScoreLabel;
    
    // Remove sound clip variables
    private boolean soundEnabled = true;

    private static final int QUESTIONS_PER_QUIZ = 10; // Number of questions to show in each quiz

    public QuizGUI() {
        setTitle("🔥 Quiz Beast - Level Up Your Knowledge 🚀");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set custom icon and styling
        try {
            setIconImage(createGradientIcon());
        } catch (Exception e) {
            System.out.println("Could not set custom icon");
        }

        // Initialize components
        initializeComponents();
        setupLayout();
        setupWelcomePanel();
        setupQuizPanel();
        setupResultPanel();
        setupReviewPanel();

        // Show welcome panel
        cardLayout.show(mainPanel, "WELCOME");
    }
    
    private Image createGradientIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gradient = new GradientPaint(0, 0, primaryGradientStart, 32, 32, primaryGradientEnd);
        g2d.setPaint(gradient);
        g2d.fillOval(2, 2, 28, 28);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Q", 10, 22);
        
        g2d.dispose();
        return icon;
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        welcomePanel = new JPanel();
        quizPanel = new JPanel();
        resultPanel = new JPanel();
        reviewPanel = new JPanel();

        questionLabel = new JLabel();
        optionButtons = new JRadioButton[4];
        nextButton = createStyledButton("Next ➡️", false);
        submitButton = createStyledButton("Submit 🎯", true);
        
        progressBar = new JProgressBar(0, 100);
        styleProgressBar(progressBar);
        
        timerLabel = new JLabel("⏰ 30s");
        timerLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        timerLabel.setForeground(textColor);
        
        scoreLabel = new JLabel("🏆 Score: 0");
        scoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        scoreLabel.setForeground(accentColor);

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createStyledRadioButton();
            group.add(optionButtons[i]);
            optionButtons[i].addActionListener(new OptionSelectListener(i));
        }

        // Theme toggle with modern styling
        themeToggleButton = createIconButton(isDarkMode ? "🌞" : "🌙");
        themeToggleButton.addActionListener(e -> toggleTheme());
        themeToggleButton.setToolTipText("Toggle Dark/Light Mode");
        
        // Sound toggle button
        soundToggleButton = createIconButton(soundEnabled ? "🔊" : "🔇");
        soundToggleButton.addActionListener(e -> toggleSound());
        soundToggleButton.setToolTipText("Toggle Sound Effects");
        
        timeProgressBar = new JProgressBar(0, 100);
        styleProgressBar(timeProgressBar);
        
        // Animation timer with smoother transitions
        animationTimer = new Timer(16, e -> {
            animationProgress += 0.05f;
            if (animationProgress >= 1f) {
                animationProgress = 0f;
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });

        practiceModeButton = createStyledButton("🧠 Practice Mode", false);
        practiceModeButton.addActionListener(e -> togglePracticeMode());
        
        highScoreLabel = new JLabel("🎖️ High Score: 0");
        highScoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        highScoreLabel.setForeground(accentColor);
        
        questions = new ArrayList<>();
        answeredQuestions = new ArrayList<>();
    }
    
    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setBorder(BorderFactory.createLineBorder(accentColor, 2, true));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBorder(BorderFactory.createEmptyBorder());
            }
        });
        
        // Custom painting for gradient buttons
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = c.getWidth();
                int height = c.getHeight();
                
                // Create gradient
                GradientPaint gradient;
                if (isPrimary) {
                    gradient = new GradientPaint(0, 0, primaryGradientStart, width, height, primaryGradientEnd);
                } else {
                    gradient = new GradientPaint(0, 0, secondaryGradientStart, width, height, secondaryGradientEnd);
                }
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, width, height, 25, 25);
                
                // Add subtle shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, width, height, 25, 25);
                
                g2d.dispose();
                super.paint(g, c);
            }
        });
        
        return button;
    }
    
    private JButton createIconButton(String icon) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Apple Color Emoji", Font.PLAIN, 24));
        button.setForeground(textColor);
        button.setBackground(surfaceColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Rounded corners
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(surfaceColor);
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                
                g2d.dispose();
                super.paint(g, c);
            }
        });
        
        return button;
    }
    
    private JRadioButton createStyledRadioButton() {
        JRadioButton button = new JRadioButton();
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        button.setForeground(textColor);
        button.setBackground(cardColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                    button.setForeground(accentColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(cardColor);
                    button.setForeground(textColor);
                }
            }
        });
        
        return button;
    }
    
    private void styleProgressBar(JProgressBar progressBar) {
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        progressBar.setForeground(accentColor);
        progressBar.setBackground(surfaceColor);
        progressBar.setBorderPainted(false);
        
        // Custom UI for rounded progress bar
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = c.getWidth();
                int height = c.getHeight();
                
                // Background
                g2d.setColor(surfaceColor);
                g2d.fillRoundRect(0, 0, width, height, height, height);
                
                // Progress
                int progressWidth = (int) (width * (progressBar.getPercentComplete()));
                GradientPaint gradient = new GradientPaint(0, 0, primaryGradientStart, progressWidth, 0, primaryGradientEnd);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, progressWidth, height, height, height);
                
                g2d.dispose();
            }
        });
    }

    private void playSound(String soundType) {
        if (!soundEnabled) return;
        
        try {
            switch (soundType) {
                case "correct":
                    // Play system beep once for correct answer
                    Toolkit.getDefaultToolkit().beep();
                    break;
                case "incorrect":
                    // Play system beep twice with different frequency for incorrect
                    Toolkit.getDefaultToolkit().beep();
                    try { Thread.sleep(200); } catch (InterruptedException e) {}
                    Toolkit.getDefaultToolkit().beep();
                    break;
                case "submit":
                    // Play system beep three times with increasing frequency for submit
                    for (int i = 0; i < 3; i++) {
                        Toolkit.getDefaultToolkit().beep();
                        try { Thread.sleep(150); } catch (InterruptedException e) {}
                    }
                    break;
                case "timeup":
                    // Play system beep four times with decreasing frequency for time up
                    for (int i = 0; i < 4; i++) {
                        Toolkit.getDefaultToolkit().beep();
                        try { Thread.sleep(100); } catch (InterruptedException e) {}
                    }
                    break;
                case "click":
                    // Play a single beep for button clicks
                    Toolkit.getDefaultToolkit().beep();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        // Set overall background
        getContentPane().setBackground(backgroundColor);
    }

    private void setupWelcomePanel() {
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBackground(backgroundColor);

        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Title with gradient effect
        JLabel titleLabel = new JLabel("QUIZ BEAST", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 48));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("🚀 Level up your knowledge game", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 20));
        subtitleLabel.setForeground(mutedText);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emojiLabel = new JLabel("🧠💯🔥", SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Apple Color Emoji", Font.PLAIN, 36));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = createStyledButton("🎮 START QUIZ", true);
        startButton.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(250, 60));
        startButton.addActionListener(e -> startQuiz());

        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        statsPanel.setBackground(backgroundColor);
        statsPanel.add(highScoreLabel);

        // Control buttons panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlsPanel.setBackground(backgroundColor);
        controlsPanel.add(practiceModeButton);

        contentPanel.add(emojiLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(startButton);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(controlsPanel);

        // Theme toggle in top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(backgroundColor);
        topPanel.add(soundToggleButton);
        topPanel.add(themeToggleButton);

        welcomePanel.add(topPanel, BorderLayout.NORTH);
        welcomePanel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(welcomePanel, "WELCOME");
    }

    private void setupQuizPanel() {
        quizPanel.setLayout(new BorderLayout(0, 20));
        quizPanel.setBackground(backgroundColor);
        quizPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Top panel with better spacing
        JPanel topPanel = new JPanel(new BorderLayout(20, 15));
        topPanel.setBackground(backgroundColor);
        
        // Stats row
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        statsPanel.setBackground(backgroundColor);
        statsPanel.add(scoreLabel);
        statsPanel.add(timerLabel);
        
        // Theme toggle
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(backgroundColor);
        rightPanel.add(soundToggleButton);
        rightPanel.add(themeToggleButton);
        
        topPanel.add(statsPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        topPanel.add(progressBar, BorderLayout.SOUTH);

        // Center panel with card-like styling
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(cardColor);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        questionLabel.setFont(new Font("SF Pro Display", Font.BOLD, 22));
        questionLabel.setForeground(textColor);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(cardColor);

        for (JRadioButton button : optionButtons) {
            optionsPanel.add(button);
            optionsPanel.add(Box.createVerticalStrut(15));
        }

        centerPanel.add(questionLabel);
        centerPanel.add(optionsPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setBackground(backgroundColor);

        submitButton.setVisible(true);
        submitButton.setPreferredSize(new Dimension(180, 50));
        submitButton.addActionListener(e -> submitQuiz());

        bottomPanel.add(submitButton);

        quizPanel.add(topPanel, BorderLayout.NORTH);
        quizPanel.add(centerPanel, BorderLayout.CENTER);
        quizPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(quizPanel, "QUIZ");
    }

    private void setupResultPanel() {
        resultPanel.setLayout(new BorderLayout());
        resultPanel.setBackground(backgroundColor);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));

        JLabel resultEmojiLabel = new JLabel("🎉🏆🎊", SwingConstants.CENTER);
        resultEmojiLabel.setFont(new Font("Apple Color Emoji", Font.PLAIN, 48));
        resultEmojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel resultLabel = new JLabel("QUIZ COMPLETE!", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SF Pro Display", Font.BOLD, 42));
        resultLabel.setForeground(accentColor);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel finalScoreLabel = new JLabel();
        finalScoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 28));
        finalScoreLabel.setForeground(textColor);
        finalScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton reviewButton = createStyledButton("📊 Review Answers", false);
        reviewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reviewButton.setPreferredSize(new Dimension(220, 50));
        reviewButton.addActionListener(e -> showReview());

        JButton newQuizButton = createStyledButton("🔄 Try Again", true);
        newQuizButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newQuizButton.setPreferredSize(new Dimension(220, 50));
        newQuizButton.addActionListener(e -> resetQuiz());

        contentPanel.add(resultEmojiLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(resultLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(finalScoreLabel);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(reviewButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(newQuizButton);

        resultPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, "RESULT");
    }

    private void setupReviewPanel() {
        reviewPanel.setLayout(new BorderLayout(20, 20));
        reviewPanel.setBackground(backgroundColor);
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header panel with title and back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        
        JLabel reviewTitle = new JLabel("📝 Answer Review");
        reviewTitle.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        reviewTitle.setForeground(textColor);
        headerPanel.add(reviewTitle, BorderLayout.WEST);

        JButton backButton = createStyledButton("⬅️ Back to Results", false);
        backButton.setPreferredSize(new Dimension(200, 45));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "RESULT"));
        headerPanel.add(backButton, BorderLayout.EAST);

        // Center panel with scrollable content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(backgroundColor);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBackground(backgroundColor);
        scrollPane.getViewport().setBackground(backgroundColor);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        reviewPanel.add(headerPanel, BorderLayout.NORTH);
        reviewPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(reviewPanel, "REVIEW");
    }

    private void startQuiz() {
        try {
            // Fetch questions from database
            List<Question> allQuestions = DatabaseConnector.getQuestions();
            
            if (allQuestions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions available in the database.");
                return;
            }
            
            // Randomly select questions for this quiz
            questions = selectRandomQuestions(allQuestions, QUESTIONS_PER_QUIZ);
            
            // Shuffle the options for each question
            for (Question question : questions) {
                shuffleOptions(question);
            }
            
            answeredQuestions = new ArrayList<>();
            currentQuestionIndex = 0;
            score = 0;
            updateScoreLabel();
            displayQuestion();
            startTimer();
            cardLayout.show(mainPanel, "QUIZ");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage());
        }
    }

    private List<Question> selectRandomQuestions(List<Question> allQuestions, int count) {
        List<Question> selectedQuestions = new ArrayList<>(allQuestions);
        Collections.shuffle(selectedQuestions);
        return selectedQuestions.subList(0, Math.min(count, selectedQuestions.size()));
    }

    private void shuffleOptions(Question question) {
        String[] options = question.getOptions();
        String correctAnswer = question.getCorrectAnswer();
        
        // Create a list of option indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < options.length; i++) {
            indices.add(i);
        }
        
        // Shuffle the indices
        Collections.shuffle(indices);
        
        // Create new arrays for shuffled options
        String[] shuffledOptions = new String[options.length];
        for (int i = 0; i < indices.size(); i++) {
            shuffledOptions[i] = options[indices.get(i)];
        }
        
        // Update the question with shuffled options
        question.setOptions(shuffledOptions);
        
        // Update the correct answer index
        for (int i = 0; i < shuffledOptions.length; i++) {
            if (shuffledOptions[i].equals(correctAnswer)) {
                question.setCorrectAnswer(shuffledOptions[i]);
                break;
            }
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionLabel.setText("<html><div style='text-align: center;'>" + question.getQuestionText() + "</div></html>");
            
            String[] options = question.getOptions();
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText(options[i]);
                optionButtons[i].setSelected(false);
                optionButtons[i].setEnabled(true);
                // Reset styling
                optionButtons[i].setBackground(cardColor);
                optionButtons[i].setForeground(textColor);
            }
            
            progressBar.setValue((currentQuestionIndex * 100) / questions.size());
            progressBar.setString(String.format("Question %d of %d", currentQuestionIndex + 1, questions.size()));
            
            animationProgress = 0f;
            animationTimer.start();
        } else {
            showResults();
        }
    }

    private void processAnswer() {
        if (currentQuestionIndex >= questions.size()) {
            showResults();
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        String selectedAnswer = getSelectedAnswer();
        
        if (selectedAnswer != null) {
            question.setUserAnswer(selectedAnswer);
            answeredQuestions.add(question);
            
            // Visual feedback
            for (JRadioButton button : optionButtons) {
                button.setEnabled(false);
                if (button.isSelected()) {
                    if (selectedAnswer.equals(question.getCorrectAnswer())) {
                        button.setBackground(accentColor);
                        button.setForeground(Color.WHITE);
                        playSound("correct");
                    } else {
                        button.setBackground(errorColor);
                        button.setForeground(Color.WHITE);
                        playSound("incorrect");
                    }
                }
            }

            boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());
            if (isCorrect) {
                score++;
                updateScoreLabel();
            }
            
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                javax.swing.Timer delayTimer = new javax.swing.Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        displayQuestion();
                        startTimer();
                    }
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            } else {
                showResults();
            }
        }
    }

    private void submitQuiz() {
        if (currentQuestionIndex < questions.size()) {
            // If there are remaining questions, add them as unanswered
            for (int i = currentQuestionIndex; i < questions.size(); i++) {
                Question question = questions.get(i);
                question.setUserAnswer("No Answer");
                answeredQuestions.add(question);
            }
        }
        playSound("submit");
        showResults();
    }

    private void showResults() {
        if (timer != null) {
            timer.stop();
        }

        JPanel contentPanel = (JPanel) resultPanel.getComponent(0);
        contentPanel.removeAll();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));

        JLabel resultEmojiLabel = new JLabel("🎉🏆🎊", SwingConstants.CENTER);
        resultEmojiLabel.setFont(new Font("Apple Color Emoji", Font.PLAIN, 48));
        resultEmojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel resultLabel = new JLabel("QUIZ COMPLETE!", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SF Pro Display", Font.BOLD, 42));
        resultLabel.setForeground(accentColor);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calculate answered questions count
        int answeredCount = (int) answeredQuestions.stream()
            .filter(q -> q.getUserAnswer() != null && !q.getUserAnswer().equals("No Answer"))
            .count();

        JLabel finalScoreLabel = new JLabel(String.format("Your Score: %d/%d", score, answeredCount), SwingConstants.CENTER);
        finalScoreLabel.setFont(new Font("SF Pro Display", Font.BOLD, 28));
        finalScoreLabel.setForeground(textColor);
        finalScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add overview panel
        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout(new BoxLayout(overviewPanel, BoxLayout.Y_AXIS));
        overviewPanel.setBackground(cardColor);
        overviewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel overviewTitle = new JLabel("📊 Quiz Overview", SwingConstants.CENTER);
        overviewTitle.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        overviewTitle.setForeground(textColor);
        overviewTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel correctAnswers = new JLabel(String.format("✅ Correct Answers: %d", score), SwingConstants.CENTER);
        correctAnswers.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        correctAnswers.setForeground(accentColor);
        correctAnswers.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel incorrectAnswers = new JLabel(String.format("❌ Incorrect Answers: %d", answeredCount - score), SwingConstants.CENTER);
        incorrectAnswers.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        incorrectAnswers.setForeground(errorColor);
        incorrectAnswers.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel unansweredQuestions = new JLabel(String.format("⏭️ Unanswered Questions: %d", questions.size() - answeredCount), SwingConstants.CENTER);
        unansweredQuestions.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        unansweredQuestions.setForeground(mutedText);
        unansweredQuestions.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel accuracy = new JLabel(String.format("🎯 Accuracy: %.1f%%", answeredCount > 0 ? (score * 100.0) / answeredCount : 0), SwingConstants.CENTER);
        accuracy.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        accuracy.setForeground(textColor);
        accuracy.setAlignmentX(Component.CENTER_ALIGNMENT);

        overviewPanel.add(overviewTitle);
        overviewPanel.add(Box.createVerticalStrut(10));
        overviewPanel.add(correctAnswers);
        overviewPanel.add(Box.createVerticalStrut(5));
        overviewPanel.add(incorrectAnswers);
        overviewPanel.add(Box.createVerticalStrut(5));
        overviewPanel.add(unansweredQuestions);
        overviewPanel.add(Box.createVerticalStrut(5));
        overviewPanel.add(accuracy);

        JButton reviewButton = createStyledButton("📊 Review Answers", false);
        reviewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reviewButton.setPreferredSize(new Dimension(220, 50));
        reviewButton.addActionListener(e -> showReview());

        JButton newQuizButton = createStyledButton("🔄 Try Again", true);
        newQuizButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newQuizButton.setPreferredSize(new Dimension(220, 50));
        newQuizButton.addActionListener(e -> resetQuiz());

        contentPanel.add(resultEmojiLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(resultLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(finalScoreLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(overviewPanel);
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(reviewButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(newQuizButton);

        contentPanel.revalidate();
        contentPanel.repaint();

        // Update high score if needed
        int currentHighScore = highScores.getOrDefault("default", 0);
        if (score > currentHighScore) {
            highScores.put("default", score);
            highScoreLabel.setText("🎖️ High Score: " + score);
        }

        cardLayout.show(mainPanel, "RESULT");
    }

    private void showReview() {
        JPanel centerPanel = (JPanel) ((JScrollPane) reviewPanel.getComponent(1)).getViewport().getView();
        centerPanel.removeAll();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(backgroundColor);

        for (Question question : answeredQuestions) {
            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBackground(cardColor);
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            // Question number and text
            JLabel questionNumber = new JLabel("Question " + (answeredQuestions.indexOf(question) + 1));
            questionNumber.setFont(new Font("SF Pro Display", Font.BOLD, 16));
            questionNumber.setForeground(accentColor);
            questionNumber.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel questionLabel = new JLabel("<html><div style='text-align: left;'>" + question.getQuestionText() + "</div></html>");
            questionLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
            questionLabel.setForeground(textColor);
            questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // User's answer
            JLabel userAnswerLabel = new JLabel("Your Answer: " + (question.getUserAnswer() != null ? question.getUserAnswer() : "No Answer"));
            userAnswerLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            userAnswerLabel.setForeground(question.getUserAnswer() != null && question.getUserAnswer().equals(question.getCorrectAnswer()) ? 
                accentColor : errorColor);
            userAnswerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Correct answer
            JLabel correctAnswerLabel = new JLabel("Correct Answer: " + question.getCorrectAnswer());
            correctAnswerLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            correctAnswerLabel.setForeground(accentColor);
            correctAnswerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Add status icon
            JLabel statusIcon = new JLabel(question.getUserAnswer() != null && question.getUserAnswer().equals(question.getCorrectAnswer()) ? 
                "✅" : "❌");
            statusIcon.setFont(new Font("Apple Color Emoji", Font.PLAIN, 20));
            statusIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Add all components to question panel
            questionPanel.add(questionNumber);
            questionPanel.add(Box.createVerticalStrut(10));
            questionPanel.add(questionLabel);
            questionPanel.add(Box.createVerticalStrut(15));
            questionPanel.add(statusIcon);
            questionPanel.add(Box.createVerticalStrut(5));
            questionPanel.add(userAnswerLabel);
            questionPanel.add(Box.createVerticalStrut(5));
            questionPanel.add(correctAnswerLabel);

            // Add question panel to center panel
            centerPanel.add(questionPanel);
            centerPanel.add(Box.createVerticalStrut(20));
        }

        // Add padding at the bottom
        centerPanel.add(Box.createVerticalStrut(20));

        // Refresh the panel
        centerPanel.revalidate();
        centerPanel.repaint();

        // Show the review panel
        cardLayout.show(mainPanel, "REVIEW");
    }

    private void resetQuiz() {
        if (timer != null) {
            timer.stop();
        }
        
        try {
            // Fetch new questions from database
            List<Question> allQuestions = DatabaseConnector.getQuestions();
            
            if (allQuestions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions available in the database.");
                return;
            }
            
            // Reset all state variables
            currentQuestionIndex = 0;
            score = 0;
            hintsRemaining = 3;
            if (hintsLabel != null) {
                hintsLabel.setText("✨ Hints: " + hintsRemaining);
            }
            
            // Clear answered questions
            if (answeredQuestions == null) {
                answeredQuestions = new ArrayList<>();
            } else {
                answeredQuestions.clear();
            }
            
            // Reset UI elements
            for (JRadioButton button : optionButtons) {
                button.setSelected(false);
                button.setEnabled(true);
                button.setBackground(cardColor);
                button.setForeground(textColor);
            }
            
            // Select new random questions
            questions = selectRandomQuestions(allQuestions, QUESTIONS_PER_QUIZ);
            
            // Shuffle options for each question
            for (Question question : questions) {
                shuffleOptions(question);
            }
            
            // Update UI
            updateScoreLabel();
            displayQuestion();
            startTimer();
            
            // Show quiz panel
            cardLayout.show(mainPanel, "QUIZ");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error resetting quiz: " + e.getMessage());
        }
    }

    private String getSelectedAnswer() {
        for (JRadioButton button : optionButtons) {
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    private void startTimer() {
        timeLeft = 30;
        timerLabel.setText("⏰ " + timeLeft + "s");
        timeProgressBar.setValue(100);
        
        if (timer != null) {
            timer.stop();
        }
        
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("⏰ " + timeLeft + "s");
            timeProgressBar.setValue((timeLeft * 100) / 30);
            
            if (timeLeft <= 0) {
                timer.stop();
                playSound("timeup");
                processAnswer();
            }
        });
        timer.start();
    }

    private void updateScoreLabel() {
        scoreLabel.setText("🏆 Score: " + score);
    }

    private class OptionSelectListener implements ActionListener {
        private final int optionIndex;
        public OptionSelectListener(int optionIndex) {
            this.optionIndex = optionIndex;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            processAnswer();
        }
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        updateTheme();
        themeToggleButton.setText(isDarkMode ? "🌞" : "🌙");
    }
    
    private void updateTheme() {
        Color bgColor = isDarkMode ? darkBg : backgroundColor;
        Color surfaceColor = isDarkMode ? darkSurface : this.surfaceColor;
        Color cardColor = isDarkMode ? darkCard : this.cardColor;
        Color accentColor = isDarkMode ? darkAccent : this.accentColor;
        
        mainPanel.setBackground(bgColor);
        welcomePanel.setBackground(bgColor);
        quizPanel.setBackground(bgColor);
        resultPanel.setBackground(bgColor);
        reviewPanel.setBackground(bgColor);
        
        // Update all components' colors
        updateComponentColors(bgColor, surfaceColor, cardColor, accentColor);
    }
    
    private void updateComponentColors(Color bgColor, Color surfaceColor, Color cardColor, Color accentColor) {
        for (Component comp : getAllComponents(mainPanel)) {
            if (comp instanceof JLabel) {
                ((JLabel)comp).setForeground(textColor);
            } else if (comp instanceof JButton) {
                JButton btn = (JButton)comp;
                if (btn != themeToggleButton) {
                    btn.setBackground(surfaceColor);
                    btn.setForeground(textColor);
                }
            } else if (comp instanceof JRadioButton) {
                ((JRadioButton)comp).setForeground(textColor);
                ((JRadioButton)comp).setBackground(cardColor);
            } else if (comp instanceof JPanel) {
                ((JPanel)comp).setBackground(bgColor);
            }
        }
    }
    
    private Component[] getAllComponents(Container container) {
        Component[] components = container.getComponents();
        ArrayList<Component> allComponents = new ArrayList<>();
        for (Component comp : components) {
            allComponents.add(comp);
            if (comp instanceof Container) {
                allComponents.addAll(Arrays.asList(getAllComponents((Container)comp)));
            }
        }
        return allComponents.toArray(new Component[0]);
    }

    private void togglePracticeMode() {
        isPracticeMode = !isPracticeMode;
        practiceModeButton.setText(isPracticeMode ? "⏱️ Timed Mode" : "🧠 Practice Mode");
        if (isPracticeMode) {
            if (timer != null) {
                timer.stop();
            }
            timerLabel.setVisible(false);
            timeProgressBar.setVisible(false);
        } else {
            timerLabel.setVisible(true);
            timeProgressBar.setVisible(true);
            startTimer();
        }
    }
    
    private void showHint() {
        JOptionPane.showMessageDialog(this, "Hints are not available in this version.", "💡 Hint", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleSound() {
        if (!soundEnabled) {
            // If turning sound on, just do it
            soundEnabled = true;
            soundToggleButton.setText("🔊");
            playSound("click");
        } else {
            // If turning sound off, show confirmation
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to disable sound effects?",
                "Disable Sound",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                soundEnabled = false;
                soundToggleButton.setText("🔇");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new QuizGUI().setVisible(true);
        });
    }
}