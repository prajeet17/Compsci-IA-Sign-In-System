import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class Authentication {
    private StudentDatabase studentDatabase;

    public Authentication(StudentDatabase studentDatabase) {
        this.studentDatabase = studentDatabase;
    }

    public Status authenticate(int studentId, String password) throws SQLException {
        Student student = this.studentDatabase.getStudent(studentId);
        if(student == null || !BCrypt.checkpw(password, student.getPassword())) {
            return Status.WRONG;
        } else {
            return student.getStatus();
        }
    }
}
