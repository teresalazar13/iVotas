package Data;

public class Department {
  private String name;
  private Faculty faculty;

  public Department() {}

  public Department(String name, Faculty faculty) {
    this.name = name;
    this.faculty = faculty;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Faculty getFaculty() { return faculty; }
  public void setFaculty(Faculty faculty) { this.faculty = faculty; }

  @Override
  public String toString() {
    return "Department{" +
            "name='" + name + '\'' +
            ", faculty=" + faculty +
            '}';
  }
}
