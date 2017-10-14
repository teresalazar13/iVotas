public class Department {
    private int ID;
    private String name;
    private int facultyID;

    public Department() {}

    public Department(int ID, String name, int facultyID) {
        this.ID = ID;
        this.name = name;
        this.facultyID = facultyID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", facultyID=" + facultyID +
                '}';
    }
}
