import java.util.*;

public class VotingTable {
    private int id;
    private Election election;
    private int machineID;
    private ArrayList<VotingTerminal> votingTerminals;
    private String location;

    public VotingTable() {}

    public VotingTable(int id, Election election, int machineID, ArrayList<VotingTerminal> votingTerminals, String location) {
        this.id = id;
        this.election = election;
        this.machineID = machineID;
        this.votingTerminals = votingTerminals;
        this.location = location;
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

    public int getMachineID() {
        return machineID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    public ArrayList<VotingTerminal> getVotingTerminals() {
        return votingTerminals;
    }

    public void setVotingTerminals(ArrayList<VotingTerminal> votingTerminals) {
        this.votingTerminals = votingTerminals;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "VotingTable{" +
                "id=" + id +
                ", election=" + election +
                ", machineID=" + machineID +
                ", votingTerminals=" + votingTerminals +
                ", location='" + location + '\'' +
                '}';
    }
}
