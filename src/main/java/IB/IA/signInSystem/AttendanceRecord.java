package src.main.java.IB.IA.signInSystem;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private int studentId;
    private LocalDateTime signInTime;
    private LocalDateTime signOutTime;
    private boolean autoLoggedOut;

    public AttendanceRecord(int studentId, LocalDateTime signInTime) {
        this.studentId = studentId;
        this.signInTime = signInTime;
        this.signOutTime = null; //temporary, sign out time is set properly when user sign out
        this.autoLoggedOut = false;
    }

    public int getStudentId() {
        return this.studentId;
    }

    public LocalDateTime getSignInTime() {
        return this.signInTime;
    }

    public LocalDateTime getSignOutTime() {
        return this.signOutTime;
    }

    public boolean isAutoLoggedOut() {
        return this.autoLoggedOut;
    }

    public void setAutoLoggedOut(boolean autoLoggedOut) {
        this.autoLoggedOut = autoLoggedOut;
    }

    public void setSignOutTime(LocalDateTime signOutTime) {
        this.signOutTime = signOutTime;
    }

    public void setSignInTime(LocalDateTime signInTime) {
        this.signInTime = signInTime;
    }
}
