package src.main.java.IB.IA.signInSystem;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private String studentId;
    private LocalDateTime signInTime;
    private LocalDateTime signOutTime;
    private boolean autoLoggedOut;

    public AttendanceRecord(String studentId, LocalDateTime signInTime, LocalDateTime signOutTime) {
        this.studentId = studentId;
        this.signInTime = signInTime;
        this.signOutTime = signOutTime;
        this.autoLoggedOut = false;
    }

    public String getStudentId() {
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
