import java.util.Arrays;

public class Election {
    private int ID;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private int type;
    private int[] votesIDs;

    public Election() {}

    public Election(int ID, String name, String description, Date startDate, Date endDate, int type, int[] votesIDs) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.votesIDs = votesIDs;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public int[] getVotesIDs() {
        return votesIDs;
    }

    public void setVotesIDs(int[] votesIDs) {
        this.votesIDs = votesIDs;
    }

    @Override
    public String toString() {
        return "Election{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", type=" + type +
                ", votesIDs=" + Arrays.toString(votesIDs) +
                '}';
    }
}
