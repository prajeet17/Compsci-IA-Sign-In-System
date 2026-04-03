import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;

/**
 * this is the student database class that manages storing and handling of student data
 */
public class StudentDatabase {
    private final Connection conn;

    /**
     * the constructor to add a new student database
     * @throws SQLException exception if something database related fails
     */
    public StudentDatabase() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:~/attendance_db");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS students (name VARCHAR(100) NOT NULL, contact VARCHAR(100) NOT NULL, student_id INT PRIMARY KEY, password VARCHAR(200) NOT NULL, status VARCHAR(7) NOT NULL, encrypted BOOLEAN)");
    }

    /**
     * adds a new student
     * @param student the student to add
     * @throws SQLException exception if something database related fals
     */
    public void addStudent(Student student) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)");
        statement.setString(1, student.getName());
        statement.setString(2, student.getContact());
        statement.setInt(3, student.getId());
        statement.setString(4, student.getPassword());
        statement.setString(5, student.getStatus().name());
        statement.setBoolean(6, true);
        statement.executeUpdate();
    }

    /**
     * removes the student
     * @param studentId student id of student to remove
     * @throws SQLException exception if something database related fails
     */
    public void removeStudent(int studentId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("DELETE FROM students WHERE student_id = ?");
        statement.setInt(1, studentId);
        statement.executeUpdate();
    }

    /**
     * returns the student
     * @param studentId student id of student to return
     * @return the student
     * @throws SQLException exception is something database related fails
     */
    public Student getStudent(int studentId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM students WHERE student_id = ?");
        statement.setInt(1, studentId);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return new Student(result.getString("name"), result.getString("contact"), result.getInt("student_id"), result.getString("password"), Status.valueOf(result.getString("status")), result.getBoolean("encrypted"));
        } else {
            return null;
        }
    }

    /**
     * returns all students
     * @return all students
     * @throws SQLException exception if something database related fails
     */
    public ArrayList<Student> getAllStudents() throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM students");
        ResultSet result = statement.executeQuery();
        ArrayList<Student> students = new ArrayList<>();
        while (result.next()) {
            students.add(new Student(result.getString("name"), result.getString("contact"), result.getInt("student_id"), result.getString("password"), Status.valueOf(result.getString("status")), result.getBoolean("encrypted")));
        }
        return students;
    }

    /**
     * checks if a student exists
     * @param studentId the student id
     * @return true if the student exists and false if the student does not
     * @throws SQLException exception if something database related fails
     */
    public boolean checkStudentExists(int studentId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM students WHERE student_id = ?");
        statement.setInt(1, studentId);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * sets the password for the student
     * @param studentId the student id
     * @param password the new password
     * @throws SQLException exception if something database related fails
     */
    public void setPassword(int studentId, String password) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("UPDATE students SET password = ?, encrypted = ? WHERE student_id = ?");
        statement.setString(1, BCrypt.hashpw(password, BCrypt.gensalt()));
        statement.setBoolean(2, true);
        statement.setInt(3, studentId);
        statement.executeUpdate();
    }

    public int size() throws SQLException {
        ResultSet rs = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM students");
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }
}
