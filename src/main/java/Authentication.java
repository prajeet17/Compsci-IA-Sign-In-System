import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

public class Authentication {
    private final StudentDatabase studentDatabase;

    public Authentication(StudentDatabase studentDatabase) {
        this.studentDatabase = studentDatabase;
    }

    public Result resolve(String input) throws SQLException {
        if (input == null || input.isBlank()) {
            return new Result(Result.Action.WRONG, null);
        }
        try {
            int id = Integer.parseInt(input.trim());
            Student student = studentDatabase.getStudent(id);
            if (student != null) {
                return new Result(Result.Action.SIGN_IN_OUT, student);
            }
        } catch (NumberFormatException ignored) {

        }
        List<Student> allStudents = studentDatabase.getAllStudents();
        for (Student student : allStudents) {
            if (BCrypt.checkpw(input, student.getPassword())) {
                Result.Action action;
                if (student.getStatus() == Status.OFFICER) {
                    action = Result.Action.OFFICER_DASHBOARD;
                } else if (student.getStatus() == Status.ADMIN) {
                    action = Result.Action.ADMIN_DASHBOARD;
                } else {
                    action = Result.Action.WRONG;
                }
                return new Result(action, student);
            }
        }
        return new Result(Result.Action.WRONG, null);
    }

    public static class Result { //class within in a class
        public final Action action;
        public final Student student;

        public enum Action {
            SIGN_IN_OUT, OFFICER_DASHBOARD, ADMIN_DASHBOARD, WRONG
        }

        public Result(Action action, Student student) {
            this.action = action;
            this.student = student;
        }
    }
}