# Java Quiz Application

A modern, interactive quiz application built with Java Swing that tests your knowledge with a variety of questions. The application features a sleek, user-friendly interface with smooth animations and sound effects.

## 🌟 Features
![Screenshot 2025-06-01 134537](https://github.com/user-attachments/assets/89b3c778-e236-4b89-82c6-d91abe466cd0)

- **Modern UI Design**: Clean and intuitive interface with smooth animations
- **Dark/Light Mode**: Toggle between dark and light themes
- **Sound Effects**: Interactive sound feedback with toggle option
- **Practice Mode**: Learn at your own pace without time pressure
- **Progress Tracking**: Visual progress bar and score display
- **Answer Review**: Detailed review of all questions after completion
- **High Score Tracking**: Keep track of your best performance
- **Responsive Design**: Smooth animations and transitions
- **Accessibility**: Clear contrast and readable text
- **Database Integration**: Questions stored in MySQL database

## 🎮 How to Play

1. Launch the application
2. Click "START QUIZ" to begin
3. Read each question carefully
4. Select your answer from the options provided
5. Click "Submit" or wait for the timer
6. Review your answers at the end
7. Try again to improve your score!

![Screenshot 2025-06-01 134752](https://github.com/user-attachments/assets/2f01860d-c8cd-4569-b854-6be583ea8ada)
![Screenshot 2025-06-01 134811](https://github.com/user-attachments/assets/0c51a981-3694-48e2-aee9-00a5fb9ea112)
![Screenshot 2025-06-01 134940](https://github.com/user-attachments/assets/e8b0e90a-eb4c-4e26-98cd-9ba6825cb49b)

## ⚙️ Technical Features

- **Database Integration**: MySQL database for question storage
- **Object-Oriented Design**: Clean code structure with separate classes
- **Event Handling**: Smooth user interaction handling
- **Custom Styling**: Modern UI components with gradients
- **Sound System**: Interactive sound effects for feedback
- **Theme System**: Dynamic theme switching
- **Progress Tracking**: Real-time score and progress updates

## 🛠️ Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Server (Version 8.0 or higher recommended)
- MySQL Connector/J (JDBC Driver)
- Java IDE (e.g., IntelliJ IDEA, Eclipse)

## 📦 Installation

### 1. MySQL Setup

#### Option 1: MySQL Community Server
1. Download MySQL Community Server from [MySQL Official Website](https://dev.mysql.com/downloads/mysql/)
2. Run the installer and follow the setup wizard
3. Remember to note down your root password

#### Option 2: XAMPP (Recommended for Beginners)
1. Download XAMPP from [Apache Friends](https://www.apachefriends.org/)
2. Install XAMPP which includes MySQL
3. Start MySQL from XAMPP Control Panel

#### Recommended MySQL Tools
- **MySQL Workbench**: Official GUI tool for MySQL
  - Download from [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
  - Great for database management and query execution
- **phpMyAdmin**: Web-based MySQL administration tool
  - Included with XAMPP
  - User-friendly interface for database management
- **HeidiSQL**: Lightweight alternative
  - Download from [HeidiSQL](https://www.heidisql.com/)
  - Simple and fast database management

### 2. Database Setup

1. Open your preferred MySQL tool (MySQL Workbench, phpMyAdmin, or HeidiSQL)
2. Create a new database and tables using the provided SQL script:

```sql
CREATE DATABASE quiz_app;
USE quiz_app;

-- Create questions table
CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question TEXT NOT NULL,
    option1 VARCHAR(255),
    option2 VARCHAR(255),
    option3 VARCHAR(255),
    option4 VARCHAR(255),
    correct_answer VARCHAR(255) NOT NULL
);

-- Insert sample questions
INSERT INTO questions (question, option1, option2, option3, option4, correct_answer) VALUES
('What is 2 + 2?', '3', '4', '5', '6', '4'),
('What is the capital of France?', 'London', 'Berlin', 'Paris', 'Madrid', 'Paris'),
('Which is a programming language?', 'HTML', 'CSS', 'Java', 'HTTP', 'Java'),
('What does CPU stand for?', 'Central Processing Unit', 'Computer Personal Unit', 'Central Program Unit', 'Computer Processing Unit', 'Central Processing Unit'),
('Which is the largest planet?', 'Earth', 'Mars', 'Jupiter', 'Saturn', 'Jupiter');
```

### 3. Application Setup

1. Clone the repository
2. Download MySQL Connector/J:
   - Visit [MySQL Connector/J Downloads](https://dev.mysql.com/downloads/connector/j/)
   - Download the Platform Independent (ZIP) version
   - Extract the ZIP file
   - Add the `mysql-connector-j-*.jar` file to your project's classpath
3. Update database credentials in `DatabaseConnector.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/quiz_app";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

### 4. Compilation and Running

#### Windows
```bash
# Compile
javac -cp ".;mysql-connector-j-*.jar" *.java

# Run
java -cp ".;mysql-connector-j-*.jar" QuizApp
```

#### Linux/Mac
```bash
# Compile
javac -cp ".:mysql-connector-j-*.jar" *.java

# Run
java -cp ".:mysql-connector-j-*.jar" QuizApp
```

#### Using an IDE (Recommended)

##### IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Right-click on the project folder
3. Select "Add Framework Support"
4. Choose "Maven" or "Gradle"
5. Add the following dependency to your `pom.xml` (Maven) or `build.gradle` (Gradle):

Maven (`pom.xml`):
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

Gradle (`build.gradle`):
```groovy
dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
}
```

##### Eclipse
1. Open the project in Eclipse
2. Right-click on the project
3. Select "Build Path" → "Configure Build Path"
4. Click on "Libraries" tab
5. Click "Add External JARs"
6. Select the downloaded MySQL Connector JAR file

#### Troubleshooting Compilation

1. **Classpath Issues**
   - Make sure the MySQL Connector JAR is in the correct location
   - Verify the classpath separator (use `;` for Windows, `:` for Linux/Mac)
   - Check for spaces in file paths

2. **Version Compatibility**
   - Ensure Java version matches the project requirements (JDK 8 or higher)
   - Verify MySQL Connector version compatibility

3. **Common Errors**
   - "Class not found": Check classpath and JAR file location
   - "Package not found": Verify import statements
   - "Access denied": Check database credentials

4. **Database Connection**
   - Verify MySQL server is running
   - Check database credentials in `DatabaseConnector.java`
   - Ensure database and tables are created

#### Quick Start (Windows)
```bash
# 1. Download MySQL Connector
# 2. Place it in the project directory
# 3. Compile
javac -cp ".;mysql-connector-j-*.jar" *.java

# 4. Run
java -cp ".;mysql-connector-j-*.jar" QuizApp
```

#### Quick Start (Linux/Mac)
```bash
# 1. Download MySQL Connector
# 2. Place it in the project directory
# 3. Compile
javac -cp ".:mysql-connector-j-*.jar" *.java

# 4. Run
java -cp ".:mysql-connector-j-*.jar" QuizApp
```

Note: Replace `mysql-connector-j-*.jar` with the actual filename of your downloaded connector (e.g., `mysql-connector-j-8.0.33.jar`).

## 🎨 UI Components

- **Welcome Screen**: Modern landing page with start button
- **Quiz Interface**: Clean question display with options
- **Progress Bar**: Visual progress indicator
- **Score Display**: Real-time score tracking
- **Review Panel**: Detailed answer review
- **Theme Toggle**: Dark/Light mode switch
- **Sound Toggle**: Enable/disable sound effects

## 🔧 Configuration

The application can be configured through:
- `DatabaseConnector.java`: Database settings
- `QuizGUI.java`: UI customization
- `Question.java`: Question structure

## 🎯 Game Modes

- **Timed Mode**: Answer questions within the time limit
- **Practice Mode**: Learn at your own pace
- **Review Mode**: Study your answers after completion

## 🎨 Customization

- **Colors**: Modern color scheme with gradients
- **Fonts**: Clean, readable typography
- **Animations**: Smooth transitions and effects
- **Sound**: Interactive audio feedback
- **Themes**: Dark and light mode support

## 🔄 Recent Updates

- Added sound toggle functionality with confirmation dialog
- Enhanced button hover effects
- Added tooltips for better user guidance
- Improved sound system with different feedback types
- Enhanced UI responsiveness
- Added confirmation dialog for sound toggle
- Improved button feedback and accessibility
- Updated scoring system to only count answered questions
- Added unanswered questions counter in results

## 🚀 Future Enhancements

- Multiple question categories
- Difficulty levels
- User profiles and statistics
- Online multiplayer mode
- Custom question creation
- Achievement system
- Leaderboard integration
- Mobile app version
- More sound options and customization
- Enhanced animations and transitions

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Contact

For any questions or suggestions, please open an issue in the repository.

## 🔍 Troubleshooting

### Common MySQL Issues

1. **Connection Refused**
   - Check if MySQL server is running
   - Verify port number (default: 3306)
   - Check firewall settings

2. **Access Denied**
   - Verify username and password
   - Check user privileges
   - Reset root password if needed

3. **Driver Not Found**
   - Ensure MySQL Connector/J is in classpath
   - Check JAR file version compatibility
   - Verify build path settings in IDE

### Application Issues

1. **Compilation Errors**
   - Verify JDK installation
   - Check classpath settings
   - Ensure all required files are present

2. **Runtime Errors**
   - Check database connection settings
   - Verify MySQL server status
   - Check application logs

## 📚 Additional Resources

- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Java Documentation](https://docs.oracle.com/en/java/)
- [MySQL Workbench Guide](https://dev.mysql.com/doc/workbench/en/)
- [Java Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
