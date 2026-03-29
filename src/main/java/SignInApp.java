import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SignInApp extends Application {
    private StudentDatabase studentDatabase;
    private AttendanceDatabase attendanceDatabase;
    private Authentication authentication;
    private Charts charts;
    int sessionEnd = 22; //10:00 pm by default
    int logoutDelay = 72; //1 hour and 12 minutes

    @Override
    public void start (Stage stage) {
        try {
            this.studentDatabase = new StudentDatabase();
            this.attendanceDatabase = new AttendanceDatabase();
            this.authentication = new Authentication(studentDatabase);
            this.charts = new Charts(attendanceDatabase, studentDatabase);
        } catch (SQLException e) {
            System.out.println("Setup failed");
        }
    }

    public Status login(int studentId, String password) throws SQLException {
        return authentication.authenticate(studentId, password);
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
        System.out.println("Login screen");
    }

    public void showLoginSuccessScreen() {
        System.out.println("Login successful");
    }

    public void showOfficerDashboardScreen() {
        System.out.println("Officer dashboard");
    }

    public void showAdminDashboardScreen() {
        System.out.println("Admin dashboard");
    }

    public static void main(String[] args) {
        launch(args); //still need the javafx stuff
    }
}
