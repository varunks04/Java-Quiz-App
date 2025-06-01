import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.UIManager;

public class QuizApp extends JFrame {
    // This class is now just a launcher for QuizGUI
    // All GUI logic is handled in QuizGUI
    
    public QuizApp() {
        // This class is now just a launcher for QuizGUI
        // All GUI logic is handled in QuizGUI
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