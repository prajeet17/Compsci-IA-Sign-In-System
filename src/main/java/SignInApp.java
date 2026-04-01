import javafx.application.Application;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SignInApp extends Application {
    private Stage stage;
    private Student currentStudent;
    private StudentDatabase studentDatabase;
    private AttendanceDatabase attendanceDatabase;
    private Authentication authentication;
    private Charts charts;
    int sessionEnd = 22; // 10:00 pm by default
    int logoutDelay = 72; // 1 hour and 12 minutes

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        try {
            this.studentDatabase = new StudentDatabase();
            this.attendanceDatabase = new AttendanceDatabase();
            this.authentication = new Authentication(studentDatabase);
            this.charts = new Charts(attendanceDatabase, studentDatabase);
            fill();
        } catch (SQLException exception) {
            System.out.println("Setup failed: " + exception.getMessage());
            return;
        }
        showInitialLoginScreen();
    }

    public Authentication.Result resolve(String input) throws SQLException {
        return authentication.resolve(input);
    }

    public SignInStatus signIn(int studentId) throws SQLException {
        AttendanceRecord last = attendanceDatabase.getLastRecord(studentId);
        if (last == null || last.getSignOutTime() != null) {
            AttendanceRecord newRecord = attendanceDatabase.signIn(studentId);
            return new SignInStatus(Type.IN, newRecord, null);
        }
        LocalDateTime end = last.getSignInTime().toLocalDate().atTime(LocalTime.of(sessionEnd, 0)).plusMinutes(logoutDelay);
        if (LocalDateTime.now().isAfter(end)) {
            AttendanceRecord oldRecord = attendanceDatabase.autoLogout(studentId, end);
            AttendanceRecord newRecord = attendanceDatabase.signIn(studentId);
            return new SignInStatus(Type.AUTO, newRecord, oldRecord);
        }

        AttendanceRecord record = attendanceDatabase.signOut(studentId);
        return new SignInStatus(Type.OUT, record, null);
    }

    public void showInitialLoginScreen() {
        new InitialLoginScreen(stage, this).show();
    }

    public void showLoginSuccessScreen(SignInStatus result) {
        new LoginSuccessScreen(stage, this, result).show();
    }

    public void showOfficerDashboardScreen() {
        new OfficerDashboardScreen(stage, this).show();
    }

    public void showAdminDashboardScreen() {
        new AdminDashboardScreen(stage, this).show();
    }

    public StudentDatabase getStudentDatabase() {
        return studentDatabase;
    }

    public AttendanceDatabase getAttendanceDatabase() {
        return attendanceDatabase;
    }

    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    public Student getCurrentStudent() {
        return this.currentStudent;
    }

    public Charts getCharts() {
        return charts;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void fill() throws SQLException { //temp, for testing, will be removed eventually
        if (studentDatabase.size() == 0) {
            studentDatabase.addStudent(new Student("Bob", "123-456-789", 1001, "1001", Status.STUDENT, false));
            studentDatabase.addStudent(new Student("Jack", "123-456-789", 1003, "officer", Status.OFFICER, false));
            studentDatabase.addStudent(new Student("Jeff", "123-456-789", 1004, "admin", Status.ADMIN,false));
        }
    }
}