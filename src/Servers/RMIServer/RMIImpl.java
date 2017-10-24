package Servers.RMIServer;

import Data.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class RMIImpl extends UnicastRemoteObject implements RMIInterface {
  private FileWrapper data;
  private ArrayList<User> users;
  private ArrayList<Faculty> faculties;
  private ArrayList<Department> departments;
  private ArrayList<Election> elections;
  private ArrayList<CandidateList> candidateLists;
  private ArrayList<VotingTable> votingTables;
  private ArrayList<Vote> votes;
  private ArrayList<ElectionResult> electionResults;

  public RMIImpl() throws RemoteException {
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
      electionResults = data.electionResults;
    } catch (ClassNotFoundException e) {
      System.out.println("Class Not Found Exception " + e);
    } catch (java.io.IOException e) {
      System.out.println("java.io.IOException " + e);
    }
  }

  // The backup server will be a client that will send a message to the main server every 5 seconds. The main server has
  // 1 second to reply to the backup server. If it doesn't reply we have to turn the backup server into the main server.
  // The backup server then has to read the object files to get the updated data.

  // args server => localhost 6789 7000
  // args backup => localhost 6789 8000

  public static void main(String args[]) {
    if(args.length != 3) {
      System.out.println("java RMIIMpl localhost UDPPort RegistryPort");
      System.exit(0);
    }

    int UDPPort = Integer.parseInt(args[1]);
    int registryPort = Integer.parseInt(args[2]);

    backupServer(args[0], UDPPort, registryPort);
  }

  public static void mainServer(int UDPPort, int registryPort) {
    try {
      RMIImpl server = new RMIImpl();
      NewThread thread = new NewThread("CheckRMIServerStatus", UDPPort);

      System.out.println(server.users);
      System.out.println(server.faculties);
      System.out.println(server.departments);
      System.out.println(server.elections);
      System.out.println(server.votingTables);
      System.out.println(server.votes);
      System.out.println(server.electionResults);

      Registry reg = LocateRegistry.createRegistry(registryPort);
      reg.rebind("ivotas", server);
      System.out.println("RMI Server ready.");

    } catch (RemoteException re) {
      System.out.println("Exception in RMIImpl.main: " + re);
    }
  }


  public static void backupServer(String aHostName,int UDPPort, int registryPort) {

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

          if (replyMessage.equals("Server Status OK")) {
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

  public synchronized void createElection(String name, String description, long startDate, long endDate, int type) throws RemoteException {
    Election election = new Election(name, description, startDate, endDate, type);
    this.elections.add(election);
    this.updateFile(this.elections, "Elections");
  }

  public synchronized boolean createStudentsElection(String name, String description, long startDate, long endDate, int type, String departmentName) throws RemoteException {
    Department department = getDepartmentByName(departmentName);
    if (department == null) {
      return false;
    }
    Election election = new Election(name, description, startDate, endDate, type, department);
    this.elections.add(election);
    this.updateFile(this.elections, "Elections");
    return true;
  }

  public synchronized void createCandidateList(String name, ArrayList<User> users, Election election) throws RemoteException {
    CandidateList candidateList = new CandidateList(name, users);
    this.candidateLists.add(candidateList);
    election.addCandidateList(candidateList);
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
    int id = generateVotingTableId(election);
    ArrayList<VotingTerminal> votingTerminals = new ArrayList<VotingTerminal>();
    VotingTable votingTable = new VotingTable(id, election, department, votingTerminals);
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
    for (int i = 0; i < electionResults.size(); i++) {
      res += electionResults.get(i).getElectionResults();
    }
    return res;
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

  public synchronized Faculty getFacultyByDepartmentName(String department) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      for (int j = 0; j < faculties.get(i).getDepartments().size(); j++) {
        if (faculties.get(i).getDepartments().get(j).getName().equals(department)) {
          return faculties.get(i);
        }
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

  public synchronized String printUsers() throws RemoteException {
    String res = "";
    for (int i = 0; i < users.size(); i++) {
      res += users.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no users";
    return res;
  }

  public synchronized String printFaculties() throws RemoteException {
    String res = "";
    for (int i = 0; i < faculties.size(); i++) {
      res += faculties.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no faculties";
    return res;
  }

  public synchronized String printDepartments() throws RemoteException {
    String res = "";
    for (int i = 0; i < departments.size(); i++) {
      res += departments.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no departments";
    return res;
  }

  public synchronized String printElections() throws RemoteException {
    String res = "";
    for (int i = 0; i < elections.size(); i++) {
      res += elections.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no elections";
    return res;
  }

  public synchronized String printCandidateLists() throws RemoteException {
    String res = "";
    for (int i = 0; i < candidateLists.size(); i++) {
      res += candidateLists.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no candidate lists";
    return res;
  }

  public synchronized String printVotingTables() throws RemoteException {
    String res = "";
    for (int i = 0; i < votingTables.size(); i++) {
      res += votingTables.get(i).prettyPrint();
    }
    if (res == "")
      return "There are no voting tables";
    return res;
  }

  public synchronized VotingTable searchVotingTableById(int id) {
    for (VotingTable votingTable : this.votingTables) {
      if (votingTable.getId() == id) {
        return votingTable;
      }
    }

    return null;
  }

  public synchronized int generateVotingTableId(Election election) throws RemoteException {
    int id = 0;
    for (int i = 0; i < votingTables.size(); i++) {
      if (votingTables.get(i).getElection().getName().equals(election.getName())) {
        id += 1;
      }
    }
    return id;
  }

  private ArrayList<String> fieldValues(String field, ArrayList<User> users) {
    ArrayList<String> values = new ArrayList<>();

    switch (field) {
      case "name":
        for (User user : users) {
          values.add(user.getName());
        }
        break;
      case "department":
        for (User user : users) {
          values.add(user.getDepartment().getName());
        }
        break;
      case "faculty":
        for (User user : users) {
          values.add(user.getFaculty().getName());
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
    Vote vote = new Vote(user, election, candidateList, department);
    this.votes.add(vote);
    updateFile(this.votes, "Votes");
  }

  private long currentTimestamp() {
    long date = 0;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    Calendar currentDate = Calendar.getInstance();

    try {
      date = simpleDateFormat.parse(currentDate.get(Calendar.DAY_OF_MONTH) + "/" +
              currentDate.get(Calendar.MONTH) + "/" +
              currentDate.get(Calendar.YEAR) + " " +
              currentDate.get(Calendar.HOUR_OF_DAY) + ":" +
              currentDate.get(Calendar.MINUTE) + ":00").getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return date;
  }

  public synchronized void updateFile(Object object, String className) {
    try {
      this.data.writeFile(object, className);
    } catch(IOException e) {
      System.out.println("IOException: Error writing in " + className + " file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in " + className + " file");
    }
    System.out.println(object);
  }

  public synchronized void remote_print(String s) throws RemoteException {
    System.out.println("Server: " + s);
  }
}

class NewThread implements Runnable {
  private String threadName;
  private Thread t;
  private int port;

  NewThread(String threadName, int port) {
    this.port = port;
    this.threadName = threadName;
    t = new Thread(this, threadName);
    System.out.println("New thread: " + t);
    t.start();
  }

  public void run() {
    DatagramSocket aSocket = null;
    String s;

    System.out.println("Thread " + this.threadName + " started.");

    try {
      aSocket = new DatagramSocket(this.port);
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
  }
}