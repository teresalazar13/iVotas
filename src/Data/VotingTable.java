package Data;

import java.util.*;

public class VotingTable {
  private Election election;
  private ArrayList<VotingTerminal> votingTerminals;
  private String location;

  public VotingTable() {}

  public VotingTable(Election election, ArrayList<VotingTerminal> votingTerminals, String location) {
    this.election = election;
    this.votingTerminals = votingTerminals;
    this.location = location;
  }

  public Election getElection() { return election; }
  public void setElection(Election election) { this.election = election; }

  public ArrayList<VotingTerminal> getVotingTerminals() { return votingTerminals; }
  public void setVotingTerminals(ArrayList<VotingTerminal> votingTerminals) { this.votingTerminals = votingTerminals; }

  public String getLocation() { return location; }
  public void setLocation(String location) { this.location = location; }

  @Override
  public String toString() {
    return "VotingTable{" +
            "election=" + election +
            ", votingTerminals=" + votingTerminals +
            ", location='" + location + '\'' +
            '}';
  }
}
