package Data;
import java.io.Serializable;
import java.util.ArrayList;

public class Faculty implements Serializable {
  private static final long serialVersionUID = -3785830534253236995L;
  private String name;
  private ArrayList<Department> departments;

  public Faculty() {}

  public Faculty(String name) {
    this.name = name;
    this.departments = new ArrayList<Department>();
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

  public String prettyPrint() {
    String res = "Name: " + this.name + "\nDepartments: ";
    for (int i = 0; i < departments.size(); i++) {
      res += departments.get(i).getName() + " ,";
    }
    return res.substring(0, res.length() - 2);
  }

  @Override
  public String toString() {
    return "Faculty{" +
            "name=" + name +
            ",departments=" + departments +
            '}';
  }
}
