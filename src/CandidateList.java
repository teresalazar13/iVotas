import java.util.*;

public class CandidateList {
    private int id;
    private Election election;
    private ArrayList<User> candidates;

    public CandidateList() {}

    public CandidateList(int id, Election election, ArrayList<User> candidates) {
        this.id = id;
        this.election = election;
        this.candidates = candidates;
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

    public ArrayList<User> getCandidates() {
        return candidates;
    }

    public void setCandidates(ArrayList<User> candidates) {
        this.candidates = candidates;
    }

    @Override
    public String toString() {
        return "CandidateList{" +
                "id=" + id +
                ", election=" + election +
                ", candidates=" + candidates +
                '}';
    }
}
