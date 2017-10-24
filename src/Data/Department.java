package Data;
import java.io.Serializable;

public class Department implements Serializable {
  private String name;

  public Department() {}

  public Department(String name) {
    this.name = name;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String prettyPrint() {
    return "Name: " + this.name + "\n";
  }

  @Override
  public String toString() {
    return "Department{" +
            "name=" + name+
            '}';
  }
}
