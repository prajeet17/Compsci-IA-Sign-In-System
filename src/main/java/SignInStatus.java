/**
 * represents what action was taken when signing in
 */
public class SignInStatus {
    public final Type type;
    public final AttendanceRecord activeRecord;
    public final AttendanceRecord autoRecord;

    /**
     * the sign in status constructor
     * @param type the type of action, sign in or out or auto
     * @param activeRecord the sign in record
     * @param autoRecord the initial sign out record
     */
    public SignInStatus(Type type, AttendanceRecord activeRecord, AttendanceRecord autoRecord) {
        this.type = type;
        this.activeRecord = activeRecord;
        this.autoRecord = autoRecord;
    }
}
