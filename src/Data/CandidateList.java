package Data;

import java.util.*;

public class CandidateList {
  private ArrayList<User> users;

  public CandidateList() {}

  public CandidateList(ArrayList<User> users) {
      this.users = users;
  }

  public ArrayList<User> getUsers() { return users; }
  public void setUsers(ArrayList<User> users) { this.users = users; }

  @Override
  public String toString() {
    return "CandidateList{" +
            ", users=" + users +
            '}';
  }
}
