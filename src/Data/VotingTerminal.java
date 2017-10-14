package Data;

public class VotingTerminal {
  private int ID;
  private int status;

  public VotingTerminal() {}

  public VotingTerminal(int ID, int status) {
    this.ID = ID;
    this.status = status;
  }

  public int getID() {
      return ID;
  }
  public void setID(int ID) {
      this.ID = ID;
  }

  public int getStatus() {
      return status;
  }
  public void setStatus(int status) {
      this.status = status;
  }

  @Override
  public String toString() {
    return "Data.VotingTerminal{" +
            "ID=" + ID +
            ", status=" + status +
            '}';
  }
}
