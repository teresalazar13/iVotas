package Data;

public class Faculty {
  private String name;

  public Faculty() {}

  public Faculty(String name) {
    this.name = name;
  }

  public String getName() {
      return name;
  }
  public void setName(String name) {
      this.name = name;
  }

  @Override
  public String toString() {
    return "Faculty{" +
            "Name='" + name + '\'' +
            '}';
  }
}
