package Data;

import java.util.*;

public class CandidateList {
  private Election election;
  private ArrayList<User> users;

  public CandidateList() {}

  public CandidateList(Election election, ArrayList<User> users) {
      this.election = election;
      this.users = users;
  }

  public Election getElection() {
      return election;
  }
  public void setElection(Election election) {
      this.election = election;
  }

  public ArrayList<User> getUsers() { return users; }
  public void setUsers(ArrayList<User> users) { this.users = users; }

  @Override
  public String toString() {
    return "CandidateList{" +
            "election=" + election +
            ", users=" + users +
            '}';
  }
}
