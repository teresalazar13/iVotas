package Data;

import java.io.Serializable;
import java.util.ArrayList;


public class Election implements Serializable {
  private static final long serialVersionUID = 7538208724782622046L;
  private String name;
  private String description;
  private long startDate;
  private long endDate;
  private int type;
  private Department department;
  private ArrayList<CandidateList> candidateLists;
  private ArrayList<Vote> votes;

  public Election() {}

  public Election(String name, String description, long startDate, long endDate, int type) {
    this.name = name;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
    this.type = type;
    this.candidateLists = new ArrayList<CandidateList>();
    this.votes = new ArrayList<Vote>();
    this.department = null;
  }

  public Election(String name, String description, long startDate, long endDate, int type, Department department) {
    this.name = name;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
    this.type = type;
    this.candidateLists = new ArrayList<CandidateList>();
    this.votes = new ArrayList<Vote>();
    this.department = department;
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

  public long getStartDate() {
      return startDate;
  }
  public void setStartDate(long startDate) {
      this.startDate = startDate;
  }

  public long getEndDate() {
      return endDate;
  }
  public void setEndDate(long endDate) {
      this.endDate = endDate;
  }

  public int getType() {
      return type;
  }
  public void setType(int type) {
      this.type = type;
  }

  public ArrayList<CandidateList> getCandidateLists() { return candidateLists; }
  public void setCandidateLists(ArrayList<CandidateList> candidateLists) { this.candidateLists = candidateLists; }

  public ArrayList<Vote> getVotes() { return votes; }
  public void setVotes(ArrayList<Vote> votes) { this.votes = votes; }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public void addCandidateList(CandidateList candidateList) {
    candidateLists.add(candidateList);
  }

  @Override
  public String toString() {
    return "Election{" +
            "name=" + name+
            ",description=" + description +
            ",startDate=" + startDate +
            ",endDate=" + endDate +
            ",type=" + type +
            ",candidateLists|" + candidateLists +
            ",votes|" + votes +
            '}';
  }
}
