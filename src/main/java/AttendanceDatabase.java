import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDatabase {
    private final Connection conn;

    public AttendanceDatabase() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:~/attendance_db"); //makes the storage persistent in home directory
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS records (student_id INT NOT NULL REFERENCES students(student_id), sign_in_time TIMESTAMP NOT NULL, sign_out_time TIMESTAMP, auto_logged_out BOOLEAN DEFAULT FALSE, PRIMARY KEY (student_id, sign_in_time))");
    }

    private AttendanceRecord getLastRecord(int studentId) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM records WHERE student_id = ? ORDER BY sign_in_time DESC LIMIT 1"); //only pulls most recent record of student, have to use prepared statement cause SQL injection
        preparedStatement.setInt(1, studentId); //inserts studentId into first question mark
        ResultSet result = preparedStatement.executeQuery();
        if (!result.next()) {
            return null;
        }
        AttendanceRecord record = new AttendanceRecord(result.getInt("student_id"), result.getObject("sign_in_time", LocalDateTime.class)); //turns sql types into java types and makes new record
        record.setSignOutTime(result.getObject("sign_out_time", LocalDateTime.class)); //signs out when sign back in, easier than running a loop or smth in the background
        record.setAutoLoggedOut(result.getBoolean("auto_logged_out"));
        return record;
    }

    public AttendanceRecord signIn(int studentId) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO records (student_id, sign_in_time) VALUES (?, ?)");
        preparedStatement.setInt(1, studentId);
        preparedStatement.setObject(2, LocalDateTime.now());
        preparedStatement.executeUpdate();
        return getLastRecord(studentId); //for success message
    }

    public AttendanceRecord signOut(int studentId) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("UPDATE records SET sign_out_time = ? WHERE student_id = ? AND sign_out_time IS NULL"); //by default signing in makes sign out time null so it only affects one row
        preparedStatement.setObject(1, LocalDateTime.now());
        preparedStatement.setInt(2, studentId);
        preparedStatement.executeUpdate();
        return getLastRecord(studentId); //for success message
    }

    public List<AttendanceRecord> getStudentRecords(int studentId) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM records WHERE student_id = ? ORDER BY sign_in_time DESC");
        preparedStatement.setInt(1, studentId);
        return toList(preparedStatement.executeQuery());
    }

    public List<AttendanceRecord> getDateRangeRecords(LocalDate start, LocalDate end) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM records WHERE sign_in_time BETWEEN ? AND ? ORDER BY sign_in_time DESC");
        preparedStatement.setObject(1, start.atStartOfDay());
        preparedStatement.setObject(2, end.plusDays(1).atStartOfDay());
        return toList(preparedStatement.executeQuery());
    }

    private List<AttendanceRecord> toList(ResultSet result) throws SQLException { //formats result into list
        List<AttendanceRecord> records = new ArrayList<AttendanceRecord>();
        while (result.next()) {
            AttendanceRecord record = new AttendanceRecord(result.getInt("student_id"), result.getObject("sign_in_time", LocalDateTime.class));
            record.setSignOutTime(result.getObject("sign_out_time", LocalDateTime.class));
            record.setAutoLoggedOut(result.getBoolean("auto_logged_out"));
            records.add(record);
        }
        return records;
    }

    public AttendanceRecord autoLogout(int studentId, LocalDateTime deadline) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("UPDATE records SET sign_out_time = ?, auto_logged_out = TRUE WHERE student_id = ? AND sign_out_time IS NULL");
        if(getLastRecord(studentId) == null) {
            return null;
        }
        preparedStatement.setObject(1, deadline);
        preparedStatement.setInt(2, studentId);
        preparedStatement.executeUpdate();
        return getLastRecord(studentId);
    }

    public List<AttendanceRecord> getAllRecords() throws SQLException {
        return toList(conn.createStatement().executeQuery("SELECT * FROM records ORDER BY sign_in_time DESC"));
    }
}