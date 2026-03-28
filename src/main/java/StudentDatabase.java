import java.sql.*;
import java.util.ArrayList;

public class StudentDatabase {
    private final Connection conn;

    public StudentDatabase() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:~/attendance_db");
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS students (name VARCHAR(100) NOT NULL, contact VARCHAR(100) NOT NULL, student_id INT PRIMARY KEY, password VARCHAR(200) NOT NULL, status VARCHAR(7) NOT NULL, encrypted BOOLEAN)");
    }

    public void addStudent(Student student ) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)");
        statement.setString(1, student.getName());
        statement.setString(2, student.getContact());
        statement.setInt(3, student.getId());
        statement.setString(4, student.getPassword());
        statement.setString(5, student.getStatus().name());
        statement.setBoolean(6, true);
        statement.executeUpdate();
    }

    public void removeStudent(int studentId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("DELETE FROM students WHERE student_id = ?");
        statement.setInt(1, studentId);
        statement.executeUpdate();
    }

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

    public ArrayList<Student> getStudentByName(String name) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM students WHERE name = ?");
        statement.setString(1, name);
        ResultSet result = statement.executeQuery();
        ArrayList<Student> students = new ArrayList<>();
        while (result.next()) {
            students.add(new Student(result.getString("name"), result.getString("contact"), result.getInt("student_id"), result.getString("password"), Status.valueOf(result.getString("status")), result.getBoolean("encrypted")));
        }
        return students;
    }

    public ArrayList<Student> getAllStudents() throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM students");
        ResultSet result = statement.executeQuery();
        ArrayList<Student> students = new ArrayList<>();
        while (result.next()) {
            students.add(new Student(result.getString("name"), result.getString("contact"), result.getInt("student_id"), result.getString("password"), Status.valueOf(result.getString("status")), result.getBoolean("encrypted")));
        }
        return students;
    }

    public void updateStudent(Student student) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("UPDATE students SET name = ?, contact = ?, password = ? WHERE student_id = ?");
        statement.setString(1, student.getName());
        statement.setString(2, student.getContact());
        statement.setString(3, student.getPassword());
        statement.setInt(4, student.getId());
        statement.executeUpdate();
    }

    public void setStatus(int studentId, Status status) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("UPDATE students SET status = ? WHERE student_id = ?");
        statement.setString(1, status.name());
        statement.setInt(2, studentId);
        statement.executeUpdate();
    }

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
}
