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

  public void remote_print(String s) throws RemoteException {
    System.out.println("Server: " + s);
  }

  public void createUser(String name, String password, Department department, Faculty faculty, String contact, String address, String cc, String expireDate, int type) throws RemoteException {
    User user = new User(name, password, department, faculty, contact, address, cc, expireDate, type);
    this.users.add(user);
    updateUsersFile();
  }

  public void createFaculty(String name) throws RemoteException {
    Faculty faculty = new Faculty(name);
    this.faculties.add(faculty);
    updateFacultiesFile();
  }

  public void createDepartment(String name, Faculty faculty) throws RemoteException {
    Department department = new Department(name);
    updateFacultyDepartment(faculty, department);
    this.departments.add(department);
    updateFacultiesFile();
    updateDepartmentsFile();
  }


  public void createElection(String name, String description, long startDate, long endDate, int type) throws RemoteException {
    Election election = new Election(name, description, startDate, endDate, type);
    this.elections.add(election);
    this.updateElectionsFile();
  }

  public boolean createStudentsElection(String name, String description, long startDate, long endDate, int type, String departmentName) throws RemoteException {
    Department department = getDepartmentByName(departmentName);
    if (department == null) {
      return false;
    }
    Election election = new Election(name, description, startDate, endDate, type, department);
    this.elections.add(election);
    this.updateElectionsFile();
    return true;
  }

  public void createCandidateList(String name, ArrayList<User> users, Election election) throws RemoteException {
    CandidateList candidateList = new CandidateList(name, users);
    this.candidateLists.add(candidateList);
    election.addCandidateList(candidateList);
    this.updateCandidateListsFile();
    this.updateElectionsFile();
  }

  public void createCandidateListCouncil(String name, ArrayList<User> users, Election election, int usersType) throws RemoteException {
    CandidateList candidateList = new CandidateList(name, users, usersType);
    this.candidateLists.add(candidateList);
    election.addCandidateList(candidateList);
    this.updateCandidateListsFile();
    this.updateElectionsFile();
  }

  public int createVotingTable(String electionName, String departmentName) throws RemoteException {
    Election election = getElectionByName(electionName);
    if (election == null) {
      return 2;
    }
    Department department = getDepartmentByName(departmentName);
    if (department == null) {
      return 3;
    }
    ArrayList<VotingTerminal> votingTerminals = new ArrayList<VotingTerminal>();
    VotingTable votingTable = new VotingTable(election, department, votingTerminals);
    votingTables.add(votingTable);
    updateVotingTablesFile();
    return 1;
  }

  public void updateDepartmentName(Department department, String name) throws RemoteException {
    for (int i = 0; i < departments.size(); i++) {
      if (departments.get(i).getName().equals(department.getName())) {
        departments.get(i).setName(name);
      }
    }
    updateDepartmentsFile();
  }

  public void updateFacultyDepartment(Faculty faculty, Department department) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(faculty.getName())) {
        faculties.get(i).addDepartment(department);
      }
    }
    updateFacultiesFile();
  }

  public void updateFacultyDepartmentName(Department department, String name) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      for (int j = 0; j < faculties.get(i).getDepartments().size(); j++) {
        if (faculties.get(i).getDepartments().get(j).getName().equals(department.getName())) {
          faculties.get(i).getDepartments().get(j).setName(name);
        }
      }
    }
    updateFacultiesFile();
  }

  public void updateFacultyName(Faculty faculty, String name) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(faculty.getName())) {
        faculties.get(i).setName(name);
      }
    }
    updateFacultiesFile();
  }

  public void removeDepartment(Department department) throws RemoteException {
    for (int i = 0; i < departments.size(); i++) {
      if (departments.get(i).getName().equals(department.getName())) {
        departments.remove(departments.get(i));
      }
    }
    updateDepartmentsFile();
  }

  public int updateElection(String electionName, Object toChange, int type) throws RemoteException {
    for (int i = 0; i < elections.size(); i++) {
      if (elections.get(i).getName().equals(electionName)) {
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
          elections.get(i).setEndDate((long) toChange);
        }
        updateElectionsFile();
        return 1;
      }
    }
    return 2;
  }

  public void removeFaculty(Faculty faculty) throws RemoteException {
    for (int i = 0; i < faculty.getDepartments().size(); i++) {
      for (int j = 0; j < this.departments.size(); j++) {
        if (faculty.getDepartments().get(i).getName().equals(departments.get(j).getName())) {
          departments.remove(departments.get(j));
        }
      }
    }
    updateDepartmentsFile();
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(faculty.getName())) {
        faculties.remove(faculties.get(i));
      }
    }
    updateFacultiesFile();
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

  public void vote(User user, Election election, CandidateList candidateList, Department department) throws RemoteException {
    Vote vote = new Vote(user, election, candidateList, department);
    System.out.println(vote);
    this.votes.add(vote);
    updateVotesFile();
  }

  public String knowWhereUserVoted(String userName, String electionName) throws RemoteException {
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

  public Vote getVoteByUserAndElection(User user, Election election) throws RemoteException {
    for (int i = 0; i < votes.size(); i++) {
      if (votes.get(i).getElection().getName().equals(election.getName()) && votes.get(i).getUser().getName().equals(user.getName())) {
        return votes.get(i);
      }
    }
    return null;
  }

  public String detailsOfPastElections() throws RemoteException {
    String res = "";
    for (int i = 0; i < electionResults.size(); i++) {
      res += electionResults.get(i).getElectionResults();
    }
    return res;
  }

  public Department getDepartmentByName(String departmentName) throws RemoteException {
    for (int i = 0; i < departments.size(); i++) {
      if (departments.get(i).getName().equals(departmentName)) {
        return departments.get(i);
      }
    }
    return null;
  }

  public Faculty getFacultyByName(String facultyName) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(facultyName)) {
        return faculties.get(i);
      }
    }
    return null;
  }

  public Faculty getFacultyByDepartmentName(String department) throws RemoteException {
    for (int i = 0; i < faculties.size(); i++) {
      for (int j = 0; j < faculties.get(i).getDepartments().size(); j++) {
        if (faculties.get(i).getDepartments().get(j).getName().equals(department)) {
          return faculties.get(i);
        }
      }
    }
    return null;
  }

  public Election getElectionByName(String electionName) throws RemoteException {
    for (int i = 0; i < elections.size(); i++) {
      if (elections.get(i).getName().equals(electionName)) {
        return elections.get(i);
      }
    }
    return null;
  }

  public User getUserByName(String userName) throws RemoteException {
    for (int i = 0; i < users.size(); i++) {
      if (users.get(i).getName().equals(userName)) {
        return users.get(i);
      }
    }
    return null;
  }

  public CandidateList getCandidateListByName(String listName) throws RemoteException {
    for (int i = 0; i < candidateLists.size(); i++) {
      if (candidateLists.get(i).getName().equals(listName)) {
        return candidateLists.get(i);
      }
    }
    return null;
  }

  public void updateUsersFile() {
    try {
      this.data.writeFile(this.users, "Users");
    } catch(IOException e) {
      System.out.println("IOException: Error writing in users file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in users file");
    }
    System.out.println(this.users);
  }

  public void updateFacultiesFile() {
    try {
      this.data.writeFile(this.faculties, "Faculties");
    } catch(IOException e) {
      System.out.println("IOException: Error writing in faculties file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in faculties file");
    }
    System.out.println(this.faculties);
  }

  public void updateDepartmentsFile() {
    try {
      this.data.writeFile(this.departments, "Departments");
    } catch(IOException e) {
      System.out.println("IOException: Error writing in departments file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in departments file");
    }
    System.out.println(this.departments);
  }

  public void updateElectionsFile() {
    try {
      this.data.writeFile(this.elections, "Elections");
    } catch(IOException e) {
      System.out.println("IOException: Error writing in elections file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in elections file");
    }
    System.out.println(this.elections);
  }

  public void updateCandidateListsFile() {
    try {
      this.data.writeFile(this.candidateLists, "CandidateLists");
    } catch(IOException e) {
      System.out.println("IOException: Error writing in candidateLists file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in candidateLists file");
    }
    System.out.println(this.candidateLists);
  }

  public void updateVotingTablesFile() {
    try {
      this.data.writeFile(this.votingTables, "votingTables");
    } catch(IOException e) {
      System.out.println("IOException: Error writing in votingTables file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in votingTables file");
    }
    System.out.println(this.votingTables);
  }

  public void updateVotesFile() {
    try {
      this.data.writeFile(this.votes, "Votes");
    } catch(IOException e) {
      System.out.println("IOException: Error writing in votes file");
    } catch(ClassNotFoundException e) {
      System.out.println("ClassNotFoundException: Error writing in votes file");
    }
    System.out.println(this.votes);
  }

  // The backup server will be a client that will send a message to the main server every 5 seconds. The main server has
  // 1 second to reply to the backup server. If it doesn't reply we have to turn the backup server into the main server.
  // The backup server then has to read the object files to get the updated data.

  // args server => 1 6789 7000
  // args backup => 0 localhost 6789 8000

  public static void main(String args[]) {

    if(args.length == 0) {
      System.out.println("java RMIIMpl 1 UDPPort RegistryPort");
      System.exit(0);
    }

    int isMainServer = Integer.parseInt(args[0]);

    if (isMainServer == 1) {
      if(args.length != 3) {
        System.out.println("java RMIIMpl 1 UDPPort RegistryPort");
        System.exit(0);
      }

      int UDPPort = Integer.parseInt(args[1]);
      int registryPort = Integer.parseInt(args[2]);

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

    else {
      backupServer(args);
    }
  }

  // args backup => 0 localhost 6789 8000

  public static void backupServer(String args[]) {

    if(args.length != 4) {
      System.out.println("java RMIIMpl 0 UDPPort RegistryPort localhost");
      System.exit(0);
    }

    int UDPPort = Integer.parseInt(args[2]);
    int registryPort = Integer.parseInt(args[3]);

    DatagramSocket aSocket = null;

    try {
      aSocket = new DatagramSocket();
      String text = "Server Status OK";
      int numberOfFails = 0;

      while (true) {
        byte[] m = text.getBytes();
        InetAddress aHost = InetAddress.getByName(args[1]);
        int serverPort = UDPPort;

        DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
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
            try {
              RMIImpl backupServer = new RMIImpl();
              Registry reg = LocateRegistry.createRegistry(registryPort);
              reg.rebind("ivotas", backupServer);
              System.out.println("RMI Backup Server ready.");
            }
            catch (RemoteException re) {
              System.out.println("Exception in RMIImpl.backupServer: " + re);
            }
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
