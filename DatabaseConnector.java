import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnector {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Connection pool settings
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 1000;
    
    private static Connection connection = null;

    /**
     * Gets database connection with retry mechanism
     */
    public static Connection getConnection() throws SQLException {
        int attempts = 0;
        SQLException lastException = null;
        
        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                if (connection == null || connection.isClosed()) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    LOGGER.info("Database connection established successfully");
                }
                return connection;
                
            } catch (ClassNotFoundException e) {
                String errorMsg = "MySQL JDBC Driver not found. Please ensure mysql-connector-java is in classpath";
                LOGGER.severe(errorMsg);
                throw new SQLException(errorMsg, e);
                
            } catch (SQLException e) {
                lastException = e;
                attempts++;
                
                String errorMsg = String.format("Database connection attempt %d failed: %s", 
                    attempts, e.getMessage());
                LOGGER.warning(errorMsg);
                
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Connection retry interrupted", ie);
                    }
                } else {
                    LOGGER.severe("All database connection attempts failed");
                    throw new SQLException("Failed to establish database connection after " + 
                        MAX_RETRY_ATTEMPTS + " attempts", lastException);
                }
            }
        }
        
        throw new SQLException("Unexpected error in connection establishment", lastException);
    }

    /**
     * Retrieves questions from database with comprehensive error handling
     */
    public static List<Question> getQuestions() throws SQLException {
        return getQuestions(20); // Default to 20 questions
    }
    
    /**
     * Retrieves specified number of questions with error handling
     */
    public static List<Question> getQuestions(int limit) throws SQLException {
        List<Question> questions = new ArrayList<>();
        
        // Validate input
        if (limit <= 0) {
            throw new IllegalArgumentException("Question limit must be positive, got: " + limit);
        }
        
        String query = "SELECT id, question, option1, option2, option3, option4, correct_answer " +
                      "FROM questions ORDER BY RAND() LIMIT ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            LOGGER.info("Executing query to fetch " + limit + " questions");
            
            try (ResultSet rs = stmt.executeQuery()) {
                int questionCount = 0;
                
                while (rs.next()) {
                    try {
                        Question question = createQuestionFromResultSet(rs);
                        questions.add(question);
                        questionCount++;
                        
                    } catch (Exception e) {
                        // Log individual question parsing errors but continue
                        LOGGER.warning("Failed to parse question with ID " + 
                            rs.getInt("id") + ": " + e.getMessage());
                    }
                }
                
                LOGGER.info("Successfully retrieved " + questionCount + " questions");
                
                if (questions.isEmpty()) {
                    LOGGER.warning("No questions found in database");
                    throw new SQLException("No questions available in the database");
                }
            }
            
        } catch (SQLException e) {
            String errorMsg = "Database error while fetching questions: " + e.getMessage();
            LOGGER.severe(errorMsg);
            
            // Provide more specific error messages based on error code
            if (e.getErrorCode() == 1146) { // Table doesn't exist
                throw new SQLException("Questions table not found. Please check database schema.", e);
            } else if (e.getErrorCode() == 1054) { // Unknown column
                throw new SQLException("Database schema mismatch. Please verify table structure.", e);
            }
            
            throw new SQLException(errorMsg, e);
        }
        
        return questions;
    }
    
    /**
     * Creates Question object from ResultSet with validation
     */
    private static Question createQuestionFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String questionText = rs.getString("question");
        String correctAnswer = rs.getString("correct_answer");
        
        // Validate required fields
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new SQLException("Question text is null or empty for ID: " + id);
        }
        
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new SQLException("Correct answer is null or empty for question ID: " + id);
        }
        
        // Get options with validation
        String[] options = new String[4];
        for (int i = 1; i <= 4; i++) {
            options[i-1] = rs.getString("option" + i);
            if (options[i-1] == null || options[i-1].trim().isEmpty()) {
                LOGGER.warning("Option " + i + " is null or empty for question ID: " + id);
                options[i-1] = ""; // Set to empty string instead of null
            }
        }
        
        return new Question(questionText.trim(), options, correctAnswer.trim());
    }

    /**
     * Tests database connection with detailed diagnostics
     */
    public static boolean testConnection() {
        try {
            LOGGER.info("Testing database connection...");
            Connection conn = getConnection();
            
            if (conn != null && !conn.isClosed()) {
                // Test with a simple query
                try (PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next()) {
                        LOGGER.info("Database connection test successful");
                        return true;
                    }
                }
            }
            
            LOGGER.warning("Database connection test failed - connection is null or closed");
            return false;
            
        } catch (SQLException e) {
            LOGGER.severe("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets database metadata for diagnostics
     */
    public static void printDatabaseInfo() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("=== Database Information ===");
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Username: " + metaData.getUserName());
            
            // Check if questions table exists
            try (ResultSet tables = metaData.getTables(null, null, "questions", new String[]{"TABLE"})) {
                if (tables.next()) {
                    System.out.println("Questions table: EXISTS");
                    
                    // Get column information
                    try (ResultSet columns = metaData.getColumns(null, null, "questions", null)) {
                        System.out.println("Table columns:");
                        while (columns.next()) {
                            System.out.println("  - " + columns.getString("COLUMN_NAME") + 
                                " (" + columns.getString("TYPE_NAME") + ")");
                        }
                    }
                } else {
                    System.out.println("Questions table: NOT FOUND");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to retrieve database information: " + e.getMessage());
        }
    }
    
    /**
     * Gets question count for validation
     */
    public static int getQuestionCount() throws SQLException {
        String query = "SELECT COUNT(*) as count FROM questions";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                LOGGER.info("Total questions in database: " + count);
                return count;
            }
            
            return 0;
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to get question count: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Safely closes database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error closing database connection: " + e.getMessage());
        } finally {
            connection = null;
        }
    }
    
    /**
     * Health check method for monitoring
     */
    public static DatabaseHealth checkHealth() {
        DatabaseHealth health = new DatabaseHealth();
        
        try {
            // Test connection
            health.connectionAvailable = testConnection();
            
            if (health.connectionAvailable) {
                // Test question retrieval
                health.questionCount = getQuestionCount();
                health.canRetrieveQuestions = health.questionCount > 0;
                
                // Test query performance
                long startTime = System.currentTimeMillis();
                getQuestions(1);
                health.queryResponseTime = System.currentTimeMillis() - startTime;
                
                health.status = "HEALTHY";
            } else {
                health.status = "CONNECTION_FAILED";
            }
            
        } catch (Exception e) {
            health.status = "ERROR: " + e.getMessage();
            health.lastError = e.getMessage();
        }
        
        return health;
    }
    
    /**
     * Database health status class
     */
    public static class DatabaseHealth {
        public boolean connectionAvailable = false;
        public boolean canRetrieveQuestions = false;
        public int questionCount = 0;
        public long queryResponseTime = 0;
        public String status = "UNKNOWN";
        public String lastError = null;
        
        @Override
        public String toString() {
            return String.format(
                "DatabaseHealth{status='%s', connection=%s, questions=%d, responseTime=%dms, error='%s'}",
                status, connectionAvailable, questionCount, queryResponseTime, lastError
            );
        }
    }
}