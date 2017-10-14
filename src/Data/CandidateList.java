package Data;

import java.util.*;

public class CandidateList {

    private int ID;
    private Election electionID;
    private int[] candidatesIDs;

    public CandidateList() {}

    public CandidateList(int ID, Election electionID, int[] candidatesIDs) {
        this.ID = ID;
        this.electionID = electionID;
        this.candidatesIDs = candidatesIDs;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }

    public Election getElectionID() {
        return electionID;
    }
    public void setElectionID(Election electionID) {
        this.electionID = electionID;
    }

    public int[] getCandidatesIDs() {
        return candidatesIDs;
    }
    public void setCandidatesIDs(int[] candidatesIDs) {
        this.candidatesIDs = candidatesIDs;
    }

    @Override
    public String toString() {
        return "Data.CandidateList{" +
                "ID=" + ID +
                ", electionID=" + electionID +
                ", candidatesIDs=" + Arrays.toString(candidatesIDs) +
                '}';
    }
}
