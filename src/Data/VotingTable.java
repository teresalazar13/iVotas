package Data;

import java.io.Serializable;
import java.util.*;

public class VotingTable implements Serializable {
  private int id;
  private Election election;
  private Department department;
  private ArrayList<VotingTerminal> votingTerminals;

  public VotingTable() {}

  public VotingTable(int id, Election election, Department department, ArrayList<VotingTerminal> votingTerminals) {
    this.id = id;
    this.election = election;
    this.department = department;
    this.votingTerminals = votingTerminals;
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

  public ArrayList<VotingTerminal> getVotingTerminals() {
    return votingTerminals;
  }

  public void setVotingTerminals(ArrayList<VotingTerminal> votingTerminals) {
    this.votingTerminals = votingTerminals;
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
            ", votingTerminals=" + votingTerminals +
            '}';
  }
}
