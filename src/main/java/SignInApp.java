import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

public class SignInApp extends Application {
    private StudentDatabase studentDatabase;
    private AttendanceDatabase attendanceDatabase;
    private Authentication authentication = new Authentication(studentDatabase);
    private Charts charts = new Charts(attendanceDatabase, studentDatabase);

    @Override
    public void start (Stage stage) {
        int sessionEnd = 22; //10:00 pm by default
        int logoutDelay = 72; //1 hour and 12 minutes

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
