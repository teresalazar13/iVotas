package Data;

public class Vote {
  private int ID;
  private int userID;
  private int electionID;
  private int candidateListID;

  public Vote() {}

  public Vote(int ID, int userID, int electionID, int candidateListID) {
    this.ID = ID;
    this.userID = userID;
    this.electionID = electionID;
    this.candidateListID = candidateListID;
  }

  public int getID() {
      return ID;
  }
  public void setID(int ID) {
      this.ID = ID;
  }

  public int getUserID() {
      return userID;
  }
  public void setUserID(int userID) {
      this.userID = userID;
  }

  public int getElectionID() {
      return electionID;
  }
  public void setElectionID(int electionID) {
      this.electionID = electionID;
  }

  public int getCandidateListID() {
      return candidateListID;
  }
  public void setCandidateListID(int candidateListID) {
      this.candidateListID = candidateListID;
  }

  @Override
  public String toString() {
    return "Vote{" +
            "ID=" + ID +
            ", userID=" + userID +
            ", electionID=" + electionID +
            ", candidateListID=" + candidateListID +
            '}';
  }
}
