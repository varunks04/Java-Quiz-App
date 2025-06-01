public class Question {
    private String questionText;
    private String[] options;
    private String correctAnswer;
    private String userAnswer;

    public Question(String questionText, String[] options, String correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    // Getters
    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    // Setters
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return correctAnswer.equals(userAnswer);
    }
} 