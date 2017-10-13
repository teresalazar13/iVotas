public class Department {
    private int id;
    private String name;
    private int facultyID;

    public Department() {}

    public Department(int id, String name, int facultyID) {
        this.id = id;
        this.name = name;
        this.facultyID = facultyID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(int facultyID) {
        this.facultyID = facultyID;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", facultyID=" + facultyID +
                '}';
    }
}
