package Data;
import java.io.Serializable;
import java.util.ArrayList;

public class Faculty implements Serializable {
  private static final long serialVersionUID = -3785830534253076995L;
  private String name;
  private ArrayList<Department> departments;

  public Faculty() {}

  public Faculty(String name, ArrayList<Department> departments) {
    this.name = name;
    this.departments = departments;
  }

  public String getName() {
      return name;
  }
  public void setName(String name) {
      this.name = name;
  }

  public ArrayList<Department> getDepartments() { return departments; }
  public void setDepartments(ArrayList<Department> departments) { this.departments = departments; }

  public void addDepartment(Department department) {
    this.departments.add(department);
  }

  @Override
  public String toString() {
    return "Faculty{" +
            "name='" + name + '\'' +
            ", departments=" + departments +
            '}';
  }
}
