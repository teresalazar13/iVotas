package Data;

public class VotingTerminal {
  private int status;

  public VotingTerminal() {}

  public VotingTerminal(int status) {
    this.status = status;
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
            "Status=" + status +
            '}';
  }
}
