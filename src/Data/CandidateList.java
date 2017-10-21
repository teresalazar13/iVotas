package Data;

import java.io.Serializable;
import java.util.*;

public class CandidateList implements Serializable {
  private static final long serialVersionUID = -1967452776847494962L;
  private String name;
  private ArrayList<User> users;

  public CandidateList() {}

  public CandidateList(String name, ArrayList<User> users) {
    this.name = name;
    this.users = users;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<User> getUsers() {
    return users;
  }

  public void setUsers(ArrayList<User> users) {
    this.users = users;
  }

  @Override
  public String toString() {
    return "CandidateList{" +
            "name='" + name + '\'' +
            ", users=" + users +
            '}';
  }
}
