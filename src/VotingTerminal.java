public class VotingTerminal {
    private int id;
    private int status;

    public VotingTerminal() {}

    public VotingTerminal(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VotingTerminal{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
