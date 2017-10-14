package Data;

import java.util.ArrayList;
import java.util.Arrays;

public class Election {
  private String name;
  private String description;
  private Date startDate;
  private Date endDate;
  private int type;
  private ArrayList<Vote> votes;

  public Election() {}

  public Election(String name, String description, Date startDate, Date endDate, int type, ArrayList<Vote> votes) {
    this.name = name;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
    this.type = type;
    this.votes = votes;
  }

  public String getName() {
      return name;
  }
  public void setName(String name) {
      this.name = name;
  }

  public String getDescription() {
      return description;
  }
  public void setDescription(String description) {
      this.description = description;
  }

  public Date getStartDate() {
      return startDate;
  }
  public void setStartDate(Date startDate) {
      this.startDate = startDate;
  }

  public Date getEndDate() {
      return endDate;
  }
  public void setEndDate(Date endDate) {
      this.endDate = endDate;
  }

  public int getType() {
      return type;
  }
  public void setType(int type) {
      this.type = type;
  }

  public ArrayList<Vote> getVotes() { return votes; }
  public void setVotes(ArrayList<Vote> votes) { this.votes = votes; }

  @Override
  public String toString() {
    return "Election{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", type=" + type +
            ", votes=" + votes +
            '}';
  }
}
