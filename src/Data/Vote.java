package Data;

import java.io.Serializable;

public class Vote implements Serializable {
  private User user;
  private Election election;
  private CandidateList candidateList;

  public Vote() {}

  public Vote(User user, Election election, CandidateList candidateList) {
    this.user = user;
    this.election = election;
    this.candidateList = candidateList;
  }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public Election getElection() { return election; }
  public void setElection(Election election) { this.election = election; }

  public CandidateList getCandidateList() { return candidateList; }
  public void setCandidateList(CandidateList candidateList) { this.candidateList = candidateList; }

  @Override
  public String toString() {
    return "Vote{" +
            "user=" + user +
            ", election=" + election +
            ", candidateList=" + candidateList +
            '}';
  }
}
