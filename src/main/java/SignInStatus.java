public class SignInStatus {
    public final Type type;
    public final AttendanceRecord activeRecord;
    public final AttendanceRecord autoRecord;

    public SignInStatus(Type type, AttendanceRecord activeRecord, AttendanceRecord autoRecord) {
        this.type = type;
        this.activeRecord = activeRecord;
        this.autoRecord = autoRecord;
    }
}
