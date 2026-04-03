import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

/**
 * this is the class that allows us to determine what role the student has so we can limit their access to only what they need
 */
public class Authentication {
    private final StudentDatabase studentDatabase;

    /**
     * constructor to create authentication for the necessary student database
     * @param studentDatabase the student database you want to create authentication for
     */
    public Authentication(StudentDatabase studentDatabase) {
        this.studentDatabase = studentDatabase;
    }

    /**
     * this determines weather the student is an officer, a student, an admin, or if they entered the wrong input
     * @param input the password a student enters
     * @return what role the student is or if the password was wrong
     * @throws SQLException exception if something database related fails
     */
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

    /**
     * this is an internal class that allows us to determine what should be done based on person who enters password
     */
    public static class Result { //class within in a class
        public final Action action;
        public final Student student;

        /**
         * this is the enum that holds the possible actions
         */
        public enum Action {
            SIGN_IN_OUT, OFFICER_DASHBOARD, ADMIN_DASHBOARD, WRONG
        }

        /**
         * this is the constructor that creates a new result based on the action needed and the student
         * @param action action to take
         * @param student student in question
         */
        public Result(Action action, Student student) {
            this.action = action;
            this.student = student;
        }
    }
}