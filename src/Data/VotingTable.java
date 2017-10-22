package Data;

import java.io.Serializable;
import java.util.*;

public class VotingTable implements Serializable {
  private Election election;
  private Department department;
  private ArrayList<VotingTerminal> votingTerminals;

  public VotingTable() {}

  public VotingTable(Election election, Department department, ArrayList<VotingTerminal> votingTerminals) {
    this.election = election;
    this.department = department;
    this.votingTerminals = votingTerminals;
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

  @Override
  public String toString() {
    return "VotingTable{" +
            "election=" + election +
            ", department=" + department +
            ", votingTerminals=" + votingTerminals +
            '}';
  }
}
