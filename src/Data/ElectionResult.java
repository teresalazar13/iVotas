package Data;

import java.io.Serializable;
import java.util.ArrayList;

public class ElectionResult implements Serializable {
  private Election election;
  private ArrayList<CandidateResults> candidatesResults;
  private int numberOfEmptyVotes;
  private int percentageOfEmptyVotes;

  public ElectionResult() {}

  public ElectionResult(Election election, ArrayList<CandidateResults> candidatesResults, int numberOfEmptyVotes, int percentageOfEmptyVotes) {
    this.election = election;
    this.candidatesResults = candidatesResults;
    this.numberOfEmptyVotes = numberOfEmptyVotes;
    this.percentageOfEmptyVotes = percentageOfEmptyVotes;
  }

  public Election getElection() {
    return election;
  }

  public void setElection(Election election) {
    this.election = election;
  }

  public ArrayList<CandidateResults> getCandidatesResults() {
    return candidatesResults;
  }

  public void setCandidatesResults(ArrayList<CandidateResults> candidatesResults) {
    this.candidatesResults = candidatesResults;
  }

  public int getNumberOfEmptyVotes() {
    return numberOfEmptyVotes;
  }

  public void setNumberOfEmptyVotes(int numberOfEmptyVotes) {
    this.numberOfEmptyVotes = numberOfEmptyVotes;
  }

  public int getPercentageOfEmptyVotes() {
    return percentageOfEmptyVotes;
  }

  public void setPercentageOfEmptyVotes(int percentageOfEmptyVotes) {
    this.percentageOfEmptyVotes = percentageOfEmptyVotes;
  }

  public String getElectionResults() {
    String res = "Election: " + election.getName() + "\n";
    for (int i = 0; i < candidatesResults.size(); i++) {
      CandidateResults candidateResult = candidatesResults.get(i);
      res += "Candidate List Name: " + candidateResult.getCandidateList().getName() +
              " --- Number of votes: " + candidateResult.getNumberOfVotes() +
              " --- Percentage: " + candidateResult.getPercentage() + "\n";
    }
    return res += "Empty votes --- Number of votes: " + numberOfEmptyVotes + " --- Percentage: " + percentageOfEmptyVotes;
  }

  @Override
  public String toString() {
    return "ElectionResult{" +
            "election=" + election +
            ", candidatesResults=" + candidatesResults +
            ", numberOfEmptyVotes=" + numberOfEmptyVotes +
            ", percentageOfEmptyVotes=" + percentageOfEmptyVotes +
            '}';
  }
}
