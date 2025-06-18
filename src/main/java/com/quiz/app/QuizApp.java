package src.main.java.com.quiz.app;

import src.main.java.com.quiz.app.db.DatabaseConnector;

import javax.swing.*;
import javax.swing.UIManager;

public class QuizApp extends JFrame {
    // This class is now just a launcher for src.main.java.com.quiz.app.QuizGUI
    // All GUI logic is handled in src.main.java.com.quiz.app.QuizGUI
    
    public QuizApp() {
        // This class is now just a launcher for src.main.java.com.quiz.app.QuizGUI
        // All GUI logic is handled in src.main.java.com.quiz.app.QuizGUI
    }
    
    private void cleanup() {
        DatabaseConnector.closeConnection();
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