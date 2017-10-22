package Data;

import java.util.ArrayList;

public class ElectionResults {
  private ArrayList<CandidateResults> candidatesResults;
  private int numberOfEmptyVotes;
  private int percentageOfEmptyVotes;

  public ElectionResults() {}

  public ElectionResults(ArrayList<CandidateResults> candidatesResults, int numberOfEmptyVotes, int percentageOfEmptyVotes) {
    this.candidatesResults = candidatesResults;
    this.numberOfEmptyVotes = numberOfEmptyVotes;
    this.percentageOfEmptyVotes = percentageOfEmptyVotes;
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
    percentageOfEmptyVotes = percentageOfEmptyVotes;
  }

  public void getElectionResults() {
    for (int i = 0; i < candidatesResults.size(); i++) {
      CandidateResults candidateResult = candidatesResults.get(i);
      System.out.println("Candidate List Name: " + candidateResult.getCandidateList().getName() +
              " --- Number of votes: " + candidateResult.getNumberOfVotes() +
              " --- Percentage: " + candidateResult.getPercentage());
    }
    System.out.println("Empty votes --- Number of votes: " + numberOfEmptyVotes + " --- Percentage: " + percentageOfEmptyVotes);
  }

  @Override
  public String toString() {
    return "ElectionResults{" +
            "candidateResults=" + candidatesResults +
            ", emptyVotes=" + emptyVotes +
            '}';
  }
}
