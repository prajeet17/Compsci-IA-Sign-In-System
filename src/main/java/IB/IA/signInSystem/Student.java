package src.main.java.IB.IA.signInSystem;

public class Student {
    private String name;
    private String contact;
    private int id;

    public Student(String name, String contact, int studentId) {
        this.name = name;
        this.contact = contact;
        this.id = studentId;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setId(int id) {
        this.id = id;
    }

}
