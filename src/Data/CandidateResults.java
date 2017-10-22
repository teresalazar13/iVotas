package Data;

public class CandidateResults {
  private CandidateList candidateList;
  private int numberOfVotes;
  private float percentage;

  public CandidateResults() {}

  public CandidateResults(CandidateList candidateList, int numberOfVotes, float percentage) {
    this.candidateList = candidateList;
    this.numberOfVotes = numberOfVotes;
    this.percentage = percentage;
  }

  public CandidateList getCandidateList() {
    return candidateList;
  }

  public void setCandidateList(CandidateList candidateList) {
    this.candidateList = candidateList;
  }

  public int getNumberOfVotes() {
    return numberOfVotes;
  }

  public void setNumberOfVotes(int numberOfVotes) {
    this.numberOfVotes = numberOfVotes;
  }

  public float getPercentage() {
    return percentage;
  }

  public void setPercentage(float percentage) {
    this.percentage = percentage;
  }

  @Override
  public String toString() {
    return "CandidateResults{" +
            "candidateList=" + candidateList +
            ", numberOfVotes=" + numberOfVotes +
            ", percentage=" + percentage +
            '}';
  }
}
