package Data;

import java.io.Serializable;
import java.util.*;

public class CandidateList implements Serializable {
  private static final long serialVersionUID = -1967452776847494962L;
  private String name;
  private ArrayList<User> users;
  private int usersType;

  public CandidateList() {}

  // Nucleo de Estudantes
  public CandidateList(String name, ArrayList<User> users) {
    this.name = name;
    this.users = users;
    this.usersType = 1;
  }

  // Conselho Geral
  public CandidateList(String name, ArrayList<User> users, int usersType) {
    this.name = name;
    this.users = users;
    this.usersType = usersType;
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

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int getUsersType() {
    return usersType;
  }

  public void setUsersType(int usersType) {
    this.usersType = usersType;
  }

  @Override
  public String toString() {
    return "CandidateList{" +
            "name=" + name +
            ", users=" + users +
            '}';
  }

  public String toStringClient() {
    ArrayList<User> users = this.users;
    ArrayList<String> userNames = new ArrayList<>();

    for (User user : users) {
      userNames.add(user.getName());
    }

    return "CandidateList{" +
            "name:" + this.name + "*" +
            "usernames:" + userNames +
            '}';
  }
}
