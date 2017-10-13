public class User {
    private int id;
    private String name;
    private String password;
    private int departmentID;
    private int facultyID;
    private String contact;
    private String address;
    private String ccID;
    private String expireDate;
    private int type;

    public User() {}

    public User(int id, String name, String password, int departmentID, int facultyID, String contact, String address, String ccID, String expireDate, int type) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.departmentID = departmentID;
        this.facultyID = facultyID;
        this.contact = contact;
        this.address = address;
        this.ccID = ccID;
        this.expireDate = expireDate;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public int getFacultyID() {
        return facultyID;
    }

    public void setFacultyID(int facultyID) {
        this.facultyID = facultyID;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCcID() {
        return ccID;
    }

    public void setCcID(String ccID) {
        this.ccID = ccID;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", departmentID=" + departmentID +
                ", facultyID=" + facultyID +
                ", contact='" + contact + '\'' +
                ", address='" + address + '\'' +
                ", ccID='" + ccID + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", type=" + type +
                '}';
    }
}
