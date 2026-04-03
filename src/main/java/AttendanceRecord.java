import java.time.LocalDateTime;

/**
 * this is the class to create a record
 */
public class AttendanceRecord {
    private int studentId;
    private LocalDateTime signInTime;
    private LocalDateTime signOutTime;
    private boolean autoLoggedOut;

    /**
     * this is the constructor that creates a record
     * @param studentId student id of student that needs a record
     * @param signInTime sign in time of that student
     */
    public AttendanceRecord(int studentId, LocalDateTime signInTime) {
        this.studentId = studentId;
        this.signInTime = signInTime;
        this.signOutTime = null; //temporary, sign out time is set properly when user sign out
        this.autoLoggedOut = false;
    }

    /**
     * this returns the student id from a record
     * @return student id of that student
     */
    public int getStudentId() {
        return this.studentId;
    }

    /**
     * returns the sign in time of the student from the record
     * @return the sign in in time of that student
     */
    public LocalDateTime getSignInTime() {
        return this.signInTime;
    }

    /**
     * returns the sign out time of the student from the record
     * @return the sign out time of that student
     */
    public LocalDateTime getSignOutTime() {
        return this.signOutTime;
    }

    /**
     * change weather a student was auto logged out
     * @param autoLoggedOut true or false of what you want to set auto logged out to
     */
    public void setAutoLoggedOut(boolean autoLoggedOut) {
        this.autoLoggedOut = autoLoggedOut;
    }

    /**
     * sets the sign in time of a record of a student
     * @param signOutTime the sign out time you want to set
     */
    public void setSignOutTime(LocalDateTime signOutTime) {
        this.signOutTime = signOutTime;
    }
}
