package Data;

import java.util.*;

public class VotingTable {
  private int ID;
  private Election election;
  private int machineID;
  private int[] votingTerminalsIDs;
  private String location;

  public VotingTable() {}

  public VotingTable(int ID, Election election, int machineID, int[] votingTerminalsIDs, String location) {
    this.ID = ID;
    this.election = election;
    this.machineID = machineID;
    this.votingTerminalsIDs = votingTerminalsIDs;
    this.location = location;
  }

  public int getID() {
      return ID;
  }
  public void setID(int ID) {
      this.ID = ID;
  }

  public Election getElection() {
      return election;
  }
  public void setElection(Election election) {
      this.election = election;
  }

  public int getMachineID() {
      return machineID;
  }
  public void setMachineID(int machineID) {
      this.machineID = machineID;
  }

  public int[] getVotingTerminalsIDs() {
      return votingTerminalsIDs;
  }
  public void setVotingTerminalsIDs(int[] votingTerminalsIDs) {
      this.votingTerminalsIDs = votingTerminalsIDs;
  }

  public String getLocation() {
      return location;
  }
  public void setLocation(String location) {
      this.location = location;
  }

  @Override
  public String toString() {
    return "Data.VotingTable{" +
            "ID=" + ID +
            ", election=" + election +
            ", machineID=" + machineID +
            ", votingTerminalsIDs=" + Arrays.toString(votingTerminalsIDs) +
            ", location='" + location + '\'' +
            '}';
  }
}
