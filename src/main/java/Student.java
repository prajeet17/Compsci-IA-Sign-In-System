import org.mindrot.jbcrypt.BCrypt;

public class Student {
    private String name;
    private String contact;
    private int id;
    private String password;
    private Status status;

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

    public String getName() {
        return this.name;
    }

    public String getContact() {
        return this.contact;
    }

    public int getId() {
        return this.id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
