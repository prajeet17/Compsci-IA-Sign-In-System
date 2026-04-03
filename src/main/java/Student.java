import org.mindrot.jbcrypt.BCrypt;

/**
 * this is the class that is used to create students
 */
public class Student {
    private String name;
    private String contact;
    private int id;
    private String password;
    private Status status;

    /**
     * the student constructor
     * @param name student name
     * @param contact student contact
     * @param studentId student id issued by school
     * @param password the password
     * @param status the role the student has
     * @param encrypted whether the student password is encrypted using BCrypt
     */
    public Student(String name, String contact, int studentId, String password, Status status, boolean encrypted) {
        this.name = name;
        this.contact = contact;
        this.id = studentId;
        if (encrypted) {
            this.password = password;
        } else {
            this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        }
        this.status = status;
    }

    /**
     * returns the student name
     * @return the student name
     */
    public String getName() {
        return this.name;
    }

    /**
     * returns the student contact
     * @return the student contact
     */
    public String getContact() {
        return this.contact;
    }

    /**
     * returns the student id
     * @return the student id
     */
    public int getId() {
        return this.id;
    }

    /**
     * returns the student password
     * @return the student password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * sets the student password
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * returns the status of the student
     * @return the status of the student
     */
    public Status getStatus() {
        return status;
    }

    /**
     * sets the status of the student
     * @param status the new status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

}
