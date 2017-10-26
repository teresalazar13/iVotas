package Data;

import java.io.Serializable;
import java.util.ArrayList;

public class ElectionResult implements Serializable {
  private Election election;
  private ArrayList<CandidateResults> candidatesResults;
  private int numberOfEmptyVotes;
  private int percentageOfEmptyVotes;
  private int numberOfNullVotes;
  private int percentageOfNullVotes;

  public ElectionResult() {}

  public ElectionResult(Election election, ArrayList<CandidateResults> candidatesResults, int numberOfEmptyVotes, int percentageOfEmptyVotes, int numberOfNullVotes, int percentageOfNullVotes) {
    this.election = election;
    this.candidatesResults = candidatesResults;
    this.numberOfEmptyVotes = numberOfEmptyVotes;
    this.percentageOfEmptyVotes = percentageOfEmptyVotes;
    this.numberOfNullVotes = numberOfNullVotes;
    this.percentageOfNullVotes = percentageOfNullVotes;
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

  public int getNumberOfNullVotes() {
    return numberOfNullVotes;
  }

  public void setNumberOfNullVotes(int numberOfNullVotes) {
    this.numberOfNullVotes = numberOfNullVotes;
  }

  public int getPercentageOfNullVotes() {
    return percentageOfNullVotes;
  }

  public void setPercentageOfNullVotes(int percentageOfNullVotes) {
    this.percentageOfNullVotes = percentageOfNullVotes;
  }

  public String toString() {
    String res = "Election: " + election.getName() + "\n";
    for (int i = 0; i < candidatesResults.size(); i++) {
      CandidateResults candidateResult = candidatesResults.get(i);
      res += "Candidate List Name: " + candidateResult.getCandidateList().getName() +
              " --- Number of votes: " + candidateResult.getNumberOfVotes() +
              " --- Percentage: " + candidateResult.getPercentage() + "\n";
    }
    return res += "Empty votes --- Number of votes: " + numberOfEmptyVotes + " --- Percentage: " + percentageOfEmptyVotes
            + " Null votes --- Number of null votes: " + numberOfNullVotes + " --- Percentage: " + percentageOfNullVotes;
  }
}
