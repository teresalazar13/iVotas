package Data;

import java.io.Serializable;
import java.util.*;

public class VotingTable implements Serializable {
  private int id;
  private Election election;
  private Department department;

  public VotingTable() {}

  public VotingTable(int id, Election election, Department department) {
    this.id = id;
    this.election = election;
    this.department = department;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Election getElection() {
    return election;
  }

  public void setElection(Election election) {
    this.election = election;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public String prettyPrint() {
    return "\n\nID: " + this.id + "\nElection: " + this.election.getName() + "\nDepartment: " + this.getDepartment().getName();
  }

  @Override
  public String toString() {
    return "VotingTable{" +
            "id=" + id +
            ", election=" + election +
            ", department=" + department +
            '}';
  }
}
