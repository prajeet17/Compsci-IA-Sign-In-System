import javafx.application.Application;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * this is the main application
 */
public class SignInApp extends Application {
    private Stage stage;
    private Student currentStudent;
    private StudentDatabase studentDatabase;
    private AttendanceDatabase attendanceDatabase;
    private Authentication authentication;
    private Charts charts;
    int sessionEnd = 22; // 10:00 pm by default
    int logoutDelay = 72; // 1 hour and 12 minutes

    /**
     * initializes the application, sets up databases, and displays initial login screen
     * @param stage the stage where everything is shown
     */
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

    /**
     * determines what action must be taken for a student has using their password
     * @param input the student password
     * @return the action
     * @throws SQLException exception if anything database related fails
     */
    public Authentication.Result resolve(String input) throws SQLException {
        return authentication.resolve(input);
    }

    /**
     * handles sign in and out logic
     * @param studentId the student id of that student
     * @return the sign in status showing what action was taken
     * @throws SQLException exception if something database related fails
     */
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

    /**
     * displays the initial login screen
     */
    public void showInitialLoginScreen() {
        new InitialLoginScreen(stage, this).show();
    }

    /**
     * displays the login success screen after sign in or out
     * @param result the sign in status
     */
    public void showLoginSuccessScreen(SignInStatus result) {
        new LoginSuccessScreen(stage, this, result).show();
    }

    /**
     * displays officer dashboard screen
     */
    public void showOfficerDashboardScreen() {
        new OfficerDashboardScreen(stage, this).show();
    }

    /**
     * displays the admin dashboard
     */
    public void showAdminDashboardScreen() {
        new AdminDashboardScreen(stage, this).show();
    }

    /**
     * gets the student database
     * @return the student database
     */
    public StudentDatabase getStudentDatabase() {
        return studentDatabase;
    }

    /**
     * returns the attendance database
     * @return the attendance database
     */
    public AttendanceDatabase getAttendanceDatabase() {
        return attendanceDatabase;
    }

    /**
     * sets the student that has just signed in
     * @param currentStudent the student that just signed in
     */
    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    /**
     * return the student that has just signed in
     * @return the student that has just signed in
     */
    public Student getCurrentStudent() {
        return this.currentStudent;
    }

    /**
     * returns the charts
     * @return the charts
     */
    public Charts getCharts() {
        return charts;
    }

    /**
     * main method
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * adds starting data when empty
     * @throws SQLException exception is something database related fails
     */
    private void fill() throws SQLException {
        if (studentDatabase.size() == 0) {
            studentDatabase.addStudent(new Student("Jack", "123-456-789", 1003, BCrypt.hashpw("officer", BCrypt.gensalt()), Status.OFFICER, true));
            studentDatabase.addStudent(new Student("Jeff", "123-456-789", 1004, BCrypt.hashpw("admin", BCrypt.gensalt()), Status.ADMIN,true));
        }
    }
}