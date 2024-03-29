package Servers.RMIServer;

import Admin.AdminInterface;
import Data.*;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class RMIImpl extends UnicastRemoteObject implements RMIInterface {
  private FileWrapper data;
  private ArrayList<User> users;
  private ArrayList<Faculty> faculties;
  private ArrayList<Department> departments;
  private ArrayList<Election> elections;
  private ArrayList<CandidateList> candidateLists;
  private ArrayList<VotingTable> votingTables;
  private ArrayList<Vote> votes;

  private ArrayList<AdminInterface> admins;

  private RMIImpl() throws RemoteException {
    super();

    try {
      FileWrapper data = new FileWrapper();
      this.data = data;
      users = data.users;
      faculties = data.faculties;
      departments = data.departments;
      elections = data.elections;
      candidateLists = data.candidateLists;
      votingTables = data.votingTables;
      votes = data.votes;
      admins = new ArrayList<>();
    } catch (ClassNotFoundException e) {
      System.out.println("Class Not Found Exception " + e);
    } catch (java.io.IOException e) {
      System.out.println("java.io.IOException " + e);
    }
  }


  public static void main(String args[]) {
    if(args.length != 3) {
      System.out.println("java -jar dataserver.jar IPAddress UDPPort RMIPort");
      System.exit(0);
    }

    int UDPPort = Integer.parseInt(args[1]);
    int registryPort = Integer.parseInt(args[2]);

    backupServer(args[0], UDPPort, registryPort);
  }

  private static void mainServer(int UDPPort, int registryPort) {
    try {
      RMIImpl server = new RMIImpl();

      Registry reg = LocateRegistry.createRegistry(registryPort);
      reg.rebind("ivotas", server);
      System.out.println("RMI Server ready.");

      DatagramSocket aSocket = null;
      String s;

      try {
        aSocket = new DatagramSocket(UDPPort);
        System.out.println("Socket Datagram listening.");

        while(true) {
          byte[] buffer = new byte[1000];
          DatagramPacket request = new DatagramPacket(buffer, buffer.length);

          aSocket.receive(request);
          s = new String(request.getData(), 0, request.getLength());
          if (s.equals("Server Status OK")) {
            DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
            aSocket.send(reply);
          }
        }

      }
      catch (SocketException e) {
        System.out.println("Socket: " + e.getMessage());
      }
      catch (IOException e) {
        System.out.println("IO: " + e.getMessage());
      }
      finally {
        if(aSocket != null) {
          aSocket.close();
        }
      }

    } catch (RemoteException re) {
      System.out.println("Exception in RMIImpl.main: " + re);
    }
  }


  /**
   * The backup server will be a client that will send a message to the main server every 5 seconds.
   * The main server has 1 second to reply to the backup server.
   * If it doesn't reply we have to turn the backup server into the main server.
   * The backup server then has to read the object files to get the updated data.
   * */
  private static void backupServer(String aHostName, int UDPPort, int registryPort) {

    DatagramSocket aSocket = null;

    try {
      aSocket = new DatagramSocket();
      String text = "Server Status OK";
      int numberOfFails = 0;

      while (true) {
        byte[] m = text.getBytes();
        InetAddress aHost = InetAddress.getByName(aHostName);

        DatagramPacket request = new DatagramPacket(m, m.length, aHost, UDPPort);
        aSocket.send(request);

        byte[] buffer = new byte[1000];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

        aSocket.setSoTimeout(1000);

        try {
          aSocket.receive(reply);
          String replyMessage = new String(reply.getData(), 0, reply.getLength());

          if (replyMessage.equals(text)) {
            System.out.println("Received: " + replyMessage);
            numberOfFails = 0;
            try {
              TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
              System.out.println("Error sleep: " + e.getMessage());
            }
          }
        }

        catch (SocketTimeoutException e) {
          numberOfFails += 1;

          if (numberOfFails < 5) {
            System.out.println("Number of fails: " + numberOfFails);
          }

          else if (numberOfFails == 5) {
            mainServer(UDPPort, registryPort);
            return;
          }

          else {
            System.out.println("Main Server not OK. I am replacing it.");
            aSocket.send(request);
            aSocket.setSoTimeout(1000);
          }

        }
      }
    }

    catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    }
    catch (IOException e){
      System.out.println("IO: " + e.getMessage());
    }
    finally {
      if(aSocket != null) {
        aSocket.close();
      }
    }
  }

  public synchronized int createUser(String name, String password, String departmentName, String facultyName, String contact, String address, String cc, String expireDate, int type) throws RemoteException {
    Department department = getDepartmentByName(departmentName);
    if (department == null)
      return 2;

    Faculty faculty = getFacultyByName(facultyName);
    if (faculty == null)
      return 3;

    User user = new User(name, password, department, faculty, contact, address, cc, expireDate, type);
    this.users.add(user);
    updateFile(this.users, "Users");
    return 1;
  }

  public synchronized void createFaculty(String name) throws RemoteException {
    Faculty faculty = new Faculty(name);
    this.faculties.add(faculty);
    updateFile(this.faculties, "Faculties");
  }

  public synchronized boolean createDepartment(String name, String facultyName)  throws RemoteException {
    Faculty faculty = getFacultyByName(facultyName);
    if (faculty == null) {
      return false;
    }
    Department department = new Department(name);
    this.departments.add(department);
    updateFacultyDepartment(faculty, department);
    updateFile(this.faculties, "Faculties");
    updateFile(this.departments, "Departments");
    return true;
  }

  public synchronized int createElection(String name, String description, long startDate, long endDate, int type) throws RemoteException {
    if (startDate < currentTimestamp()) {
      return 2;
    }
    Election election = new Election(name, description, startDate, endDate, type);
    this.elections.add(election);
    this.updateFile(this.elections, "Elections");
    return 1;
  }

  public synchronized int createStudentsElection(String name, String description, long startDate, long endDate, int type, String departmentName) throws RemoteException {
    if (startDate < currentTimestamp()) {
      return 3;
    }
    Department department = getDepartmentByName(departmentName);
    if (department == null) {
      return 2;
    }
    Election election = new Election(name, description, startDate, endDate, type, department);
    this.elections.add(election);
    this.updateFile(this.elections, "Elections");
    return 1;
  }

  public synchronized void createCandidateList(String name, ArrayList<User> users, Election election) throws RemoteException {
    CandidateList candidateList = new CandidateList(name, users);
    this.candidateLists.add(candidateList);
    this.getElectionByName(election.getName()).addCandidateList(candidateList);
    this.updateFile(this.candidateLists, "CandidateLists");
    this.updateFile(this.elections, "Elections");
  }

  public synchronized void createCandidateListCouncil(String name, ArrayList<User> users, Election election, int usersType) throws RemoteException {
    CandidateList candidateList = new CandidateList(name, users, usersType);
    this.candidateLists.add(candidateList);
    election.addCandidateList(candidateList);
    this.updateFile(this.candidateLists,"CandidateLists");
    this.updateFile(this.elections, "Elections");
  }

  public synchronized int createVotingTable(String electionName, String departmentName) throws RemoteException {
    Election election = getElectionByName(electionName);
    if (election == null) {
      return 2;
    }
    Department department = getDepartmentByName(departmentName);
    if (department == null) {
      return 3;
    }
    int id = generateVotingTableId();
    VotingTable votingTable = new VotingTable(id, election, department);
    votingTables.add(votingTable);
    updateFile(this.votingTables, "VotingTables");
    return 1;
  }

  public synchronized void updateDepartmentName(Department department, String name) throws RemoteException {
    for (int i = 0; i < departments.size(); i++) {
      if (departments.get(i).getName().equals(department.getName())) {
        departments.get(i).setName(name);
      }
    }
    updateFile(this.departments, "Departments");
  }

  public synchronized void updateFacultyDepartment(Faculty faculty, Department department) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(faculty.getName())) {
        faculties.get(i).addDepartment(department);
      }
    }
    updateFile(this.faculties, "Faculties");
  }

  public synchronized void updateFacultyDepartmentName(Department department, String name) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      for (int j = 0; j < faculties.get(i).getDepartments().size(); j++) {
        if (faculties.get(i).getDepartments().get(j).getName().equals(department.getName())) {
          faculties.get(i).getDepartments().get(j).setName(name);
        }
      }
    }
    updateFile(this.faculties, "Faculties");
  }

  public synchronized void updateFacultyName(Faculty faculty, String name) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(faculty.getName())) {
        faculties.get(i).setName(name);
      }
    }
    updateFile(this.faculties, "Faculties");
  }

  public synchronized int updateElection(String electionName, Object toChange, int type) throws RemoteException {
    for (int i = 0; i < elections.size(); i++) {
      if (elections.get(i).getName().equals(electionName)) {
        if (elections.get(i).getStartDate() < currentTimestamp()) // Election already started
          return 4;
        if (type == 1) {
          elections.get(i).setName((String) toChange);
        }
        else if (type == 2) {
          elections.get(i).setDescription((String) toChange);
        }
        else if (type == 3) {
          elections.get(i).setStartDate((long) toChange);
        }
        else {
          if ((long) toChange <= elections.get(i).getStartDate()) // Cant end election before it started
            return 3;
          elections.get(i).setEndDate((long) toChange);
        }
        updateFile(this.elections, "Elections");
        return 1;
      }
    }
    return 2; // No elections with that name
  }

  public synchronized void removeDepartment(Department department) throws RemoteException {
    for (int i = 0; i < departments.size(); i++) {
      if (departments.get(i).getName().equals(department.getName())) {
        departments.remove(departments.get(i));
      }
    }
    updateFile(this.departments, "Departments");
  }

  public synchronized void removeFaculty(Faculty faculty) throws RemoteException {
    for (int i = 0; i < faculty.getDepartments().size(); i++) {
      for (int j = 0; j < this.departments.size(); j++) {
        if (faculty.getDepartments().get(i).getName().equals(departments.get(j).getName())) {
          departments.remove(departments.get(j));
        }
      }
    }
    updateFile(this.departments, "Departments");
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(faculty.getName())) {
        faculties.remove(faculties.get(i));
      }
    }
    updateFile(this.faculties, "Faculties");
  }

  public synchronized String knowWhereUserVoted(String userName, String electionName) throws RemoteException {
    User user = getUserByName(userName);
    if (user == null) {
      return "There isn't a user with that name";
    }
    Election election = getElectionByName(electionName);
    if (election == null) {
      return "There isn't an election with that name";
    }
    Vote vote = getVoteByUserAndElection(user, election);
    if (vote == null) {
      return "User has not voted in that election";
    }
    return vote.getDepartment().getName();
  }

  public synchronized String detailsOfPastElections() throws RemoteException {
    String res = "";

    for (Election election : this.elections) {
      if (election.getEndDate() < currentTimestamp()) {
        ArrayList<Vote> electionVotes = this.votesOfElection(election);
        int numberOfVotes = electionVotes.size();
        ArrayList<CandidateResults> candidatesResults = new ArrayList<>();

        for (CandidateList candidateList : election.getCandidateLists()) {
          CandidateResults candidateResults = new CandidateResults(candidateList, 0, 0);
          candidatesResults.add(candidateResults);
        }

        int blankVotes = 0;
        for (Vote vote : electionVotes) {
          if (vote.getCandidateList() == null) {
            blankVotes++;
          }
          for (CandidateResults candidateResults : candidatesResults) {
            if (vote.getCandidateList() != null) {
              if (vote.getCandidateList().getName().equals(candidateResults.getCandidateList().getName())) {
                candidateResults.setNumberOfVotes(candidateResults.getNumberOfVotes() + 1);
                float percentage = ((float)candidateResults.getNumberOfVotes() / numberOfVotes) * 100;
                candidateResults.setPercentage(Math.round(percentage));
              }
            }
          }
        }

        float percentageOfBlankVotes = ((float) blankVotes / electionVotes.size()) * 100.0f;
        ElectionResult electionResult = new ElectionResult(
                election, candidatesResults, blankVotes, (Math.round(percentageOfBlankVotes)), 0, 0);
        res += electionResult.toString() + "\n\n";
      }
    }
    return res;
  }

  private synchronized ArrayList<Vote> votesOfElection(Election election) {
    ArrayList<Vote> votes = new ArrayList<>();

    for (Vote vote : this.votes) {
      if (vote.getElection().getName().equals(election.getName())) {
        votes.add(vote);
      }
    }

    return votes;
  }

  public synchronized User getUserByName(String userName) throws RemoteException {
    for (int i = 0; i < users.size(); i++) {
      if (users.get(i).getName().equals(userName)) {
        return users.get(i);
      }
    }
    return null;
  }

  public synchronized Department getDepartmentByName(String departmentName) throws RemoteException {
    for (int i = 0; i < departments.size(); i++) {
      if (departments.get(i).getName().equals(departmentName)) {
        return departments.get(i);
      }
    }
    return null;
  }

  public synchronized Faculty getFacultyByName(String facultyName) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(facultyName)) {
        return faculties.get(i);
      }
    }
    return null;
  }

  public synchronized Election getElectionByName(String electionName) throws RemoteException {
    for (int i = 0; i < elections.size(); i++) {
      if (elections.get(i).getName().equals(electionName)) {
        return elections.get(i);
      }
    }
    return null;
  }

  public synchronized CandidateList getCandidateListByName(String listName) throws RemoteException {
    for (int i = 0; i < candidateLists.size(); i++) {
      if (candidateLists.get(i).getName().equals(listName)) {
        return candidateLists.get(i);
      }
    }
    return null;
  }

  public synchronized Vote getVoteByUserAndElection(User user, Election election) throws RemoteException {
    for (int i = 0; i < votes.size(); i++) {
      if (votes.get(i).getElection().getName().equals(election.getName()) && votes.get(i).getUser().getName().equals(user.getName())) {
        return votes.get(i);
      }
    }
    return null;
  }

  public synchronized VotingTable getVotingTableById(int id) {
    for (VotingTable votingTable : this.votingTables) {
      if (votingTable.getId() == id) {
        return votingTable;
      }
    }

    return null;
  }

  private synchronized int generateVotingTableId() throws RemoteException {
    return votingTables.size();
  }

  public synchronized String prettyPrint(int option) throws RemoteException {
    String res = "";
    if (option == 1)
      res = printUsers();
    else if (option == 2)
      res = printFaculties();
    else if (option == 3)
      res = printDepartments();
    else if (option == 4)
      res = printElections();
    else if (option == 5)
      res = printCandidateLists();
    else
      res = printVotingTables();
    return res;
  }

  private synchronized String printUsers() throws RemoteException {
    String res = "";
    for (int i = 0; i < users.size(); i++) {
      res += users.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no users";
    return res;
  }

  private synchronized String printFaculties() throws RemoteException {
    String res = "";
    for (int i = 0; i < faculties.size(); i++) {
      res += faculties.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no faculties";
    return res;
  }

  private synchronized String printDepartments() throws RemoteException {
    String res = "";
    for (int i = 0; i < departments.size(); i++) {
      res += departments.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no departments";
    return res;
  }

  private synchronized String printElections() throws RemoteException {
    String res = "";
    for (int i = 0; i < elections.size(); i++) {
      res += elections.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no elections";
    return res;
  }

  private synchronized String printCandidateLists() throws RemoteException {
    String res = "";
    for (int i = 0; i < candidateLists.size(); i++) {
      res += candidateLists.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no candidate lists";
    return res;
  }

  private synchronized String printVotingTables() throws RemoteException {
    String res = "";
    for (int i = 0; i < votingTables.size(); i++) {
      res += votingTables.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no voting tables";
    return res;
  }

  // Returns all the possible values of the field
  private ArrayList<String> fieldValues(String field, ArrayList<User> users) {
    ArrayList<String> values = new ArrayList<>();

    switch (field) {
      case "name":
        for (User user : users) {
          values.add(user.getName());
        }
        break;
      case "contact":
        for (User user : users) {
          values.add(user.getContact());
        }
        break;
      case "address":
        for (User user : users) {
          values.add(user.getAddress());
        }
        break;
      case "cc":
        for (User user : users) {
          values.add(user.getCc());
        }
        break;
      case "expireDate":
        for (User user : users) {
          values.add(user.getExpireDate());
        }
        break;
    }

    return values;
  }

  // Returns all the users with the same field value
  private ArrayList<User> usersWithSameField(String field, String value) {
    ArrayList<User> users = new ArrayList<>();

    if ("faculty".equals(field)) {
      for (User user : this.users) {
        if (value.equals(user.getFaculty().getName())) {
          users.add(user);
        }
      }
    } else {
      for (User user : this.users) {
        if (value.equals(user.getDepartment().getName())) {
          users.add(user);
        }
      }
    }

    return users;
  }

  // The field value is unique, so we only need to return one user
  public User searchUser(String field, String value) throws RemoteException {
    ArrayList<User> users = this.users;
    ArrayList<String> values = fieldValues(field, users);

    for (int i = 0; i < values.size(); i++) {
      if (values.get(i).equals(value)) {
        return users.get(i);
      }
    }

    return null;
  }

  // Searchs for users based on the field value, there's several users with the same field value
  public ArrayList<User> searchUsers(String field, String value) throws RemoteException {
    return this.usersWithSameField(field, value);
  }

  public boolean authenticateUser(String name, String password) throws RemoteException {
    ArrayList<User> users = this.users;

    for (User user : users) {
      if (name.equals(user.getName()) && password.equals(user.getPassword())) {
        return true;
      }
    }

    return false;
  }

  public synchronized void vote(User user, Election election, CandidateList candidateList, Department department) throws RemoteException {
    Vote vote;

    if (candidateList == null) {
      vote = new Vote(user, election, department);
    } else {
      vote = new Vote(user, election, candidateList, department);
    }

    this.votes.add(vote);
    updateFile(this.votes, "Votes");

    int numberOfVotes = 0;
    for (int i = 0; i < this.votes.size(); i++) {
      if (votes.get(i).getElection().getName().equals(election.getName())) {
        numberOfVotes += 1;
      }
    }

    notifyAdmins("New vote on election " + election.getName() +
            ". \nCurrent number of votes " + numberOfVotes);
  }

  public synchronized boolean voteIsValid(User user, VotingTable votingTable, CandidateList candidateList) throws RemoteException {
    Election election = votingTable.getElection();
    long currentTime = currentTimestamp();
    if (!(currentTime >= votingTable.getElection().getStartDate() && currentTime < votingTable.getElection().getEndDate())) {
      return false;
    }


    // nucleo de estudantes
    if (election.getType() == 1) {
      if (user.getDepartment().getName().equals(election.getDepartment().getName()) &&
              user.getType() == 1 &&
              getVoteByUserAndElection(user, election) == null
              ) {
        return true;
      }
    } else { // conselho geral
      if (candidateList != null) {
        if (user.getType() == candidateList.getUsersType() && getVoteByUserAndElection(user, election) == null) {
          return true;
        }
      } else {
        if (getVoteByUserAndElection(user, election) == null) {
          return true;
        }
      }
    }

    return false;
  }

  private long currentTimestamp() {
    long date = 0;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();

    try {
      date = simpleDateFormat.parse(now.getDayOfMonth() + "/" +
              now.getMonthValue() + "/" +
              now.getYear() + " " +
              now.getHour() + ":" +
              now.getMinute() + ":00").getTime();
    } catch (ParseException e) {
      System.out.println("Error parsin date");
    }

    return date;
  }

  public synchronized void subscribe(AdminInterface c) throws RemoteException {
    System.out.println("Subscribing new admin");
    this.admins.add(c);
  }

  public synchronized void unsubscribe(AdminInterface c) throws RemoteException {
    System.out.println("Unsubscribed admin");
    this.admins.remove(c);
  }

  public synchronized void notifyAdmins(String s) throws RemoteException {
    for (int i = 0; i < this.admins.size(); i++) {
      System.out.println("Sending print to admin.");
      this.admins.get(i).print_on_client(s);
    }
  }

  private synchronized void updateFile(Object object, String className) {
    try {
      this.data.writeFile(object, className);
    } catch(IOException e) {
      System.out.println("IOException: Error writing in " + className + " file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in " + className + " file");
    }
  }

  public synchronized void remote_print(String s) throws RemoteException {
    System.out.println("Server: " + s);
  }

  public void test() throws RemoteException {}
}
