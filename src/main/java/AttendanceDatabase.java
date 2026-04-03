import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * this class creates a database storing the students who sign in and out
 */
public class AttendanceDatabase {
    private final Connection conn;

    /**
     * this is a constructor that creates a new attendance database
     * @throws SQLException exception if database fails to work
     */
    public AttendanceDatabase() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:~/attendance_db"); //makes the storage persistent in home directory
        conn.createStatement().execute("CREATE TABLE IF NOT EXISTS records (student_id INT NOT NULL REFERENCES students(student_id), sign_in_time TIMESTAMP NOT NULL, sign_out_time TIMESTAMP, auto_logged_out BOOLEAN DEFAULT FALSE, PRIMARY KEY (student_id, sign_in_time))");
    }

    /**
     * returns the last attendance record of a student
     * @param studentId student id of student
     * @return the last attendance record of a student
     * @throws SQLException exception if select records fails
     */
    public AttendanceRecord getLastRecord(int studentId) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM records WHERE student_id = ? ORDER BY sign_in_time DESC LIMIT 1");
        preparedStatement.setInt(1, studentId); //inserts studentId into first question mark
        ResultSet result = preparedStatement.executeQuery();
        if (!result.next()) {
            return null;
        }
        AttendanceRecord record = new AttendanceRecord(result.getInt("student_id"), result.getObject("sign_in_time", LocalDateTime.class));
        record.setSignOutTime(result.getObject("sign_out_time", LocalDateTime.class));
        record.setAutoLoggedOut(result.getBoolean("auto_logged_out"));
        return record;
    }

    /**
     * this allows a student to sign in
     * @param studentId student id of student
     * @return a record of the student sign in
     * @throws SQLException exception if insert fails
     */
    public AttendanceRecord signIn(int studentId) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO records (student_id, sign_in_time) VALUES (?, ?)");
        preparedStatement.setInt(1, studentId);
        preparedStatement.setObject(2, LocalDateTime.now());
        preparedStatement.executeUpdate();
        return getLastRecord(studentId); //for success message
    }

    /**
     * this allows a student to sign out
     * @param studentId student id of studet
     * @return a record of the student sign out
     * @throws SQLException exception if update records fails
     */
    public AttendanceRecord signOut(int studentId) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("UPDATE records SET sign_out_time = ? WHERE student_id = ? AND sign_out_time IS NULL"); //by default signing in makes sign out time null so it only affects one row
        preparedStatement.setObject(1, LocalDateTime.now());
        preparedStatement.setInt(2, studentId);
        preparedStatement.executeUpdate();
        return getLastRecord(studentId); //for success message
    }

    /**
     * this returns the records in a specific date range
     * @param start start date
     * @param end end date
     * @return the list of records in that date range
     * @throws SQLException exception if select records fails
     */
    public List<AttendanceRecord> getDateRangeRecords(LocalDate start, LocalDate end) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM records WHERE sign_in_time BETWEEN ? AND ? ORDER BY sign_in_time DESC");
        preparedStatement.setObject(1, start.atStartOfDay());
        preparedStatement.setObject(2, end.plusDays(1).atStartOfDay());
        return toList(preparedStatement.executeQuery());
    }

    /**
     * turns a result set into an iterable list
     * @param result result set that needs to be turned into a list
     * @return an iterable list of the result set
     * @throws SQLException exception if getting something from result set fails
     */
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

    /**
     * this is the method that allows the student to logout automatically
     * @param studentId student id of a student
     * @param deadline deadline by which to logout
     * @return the attendance record of the student being auto logged out
     * @throws SQLException exception if update records fails
     */
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

    /**
     * the method to return all records
     * @return a list of all records
     * @throws SQLException exception if select records fails
     */
    public List<AttendanceRecord> getAllRecords() throws SQLException {
        return toList(conn.createStatement().executeQuery("SELECT * FROM records ORDER BY sign_in_time DESC"));
    }

    /**
     * the method to edit the sign-out time manually
     * @param studentId student id of student
     * @param newTime the new sign-out time
     * @throws SQLException exception if the updating of records fails
     */
    public void editSignOutTime(int studentId, LocalDateTime newTime) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("UPDATE records SET sign_out_time = ? WHERE student_id = ? AND sign_out_time IS NULL");
        preparedStatement.setObject(1, newTime);
        preparedStatement.setInt(2, studentId);
        preparedStatement.executeUpdate();
    }
}