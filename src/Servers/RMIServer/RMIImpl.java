package Servers.RMIServer;

import Data.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class RMIImpl extends UnicastRemoteObject implements RMIInterface {

  private ArrayList<User> users;
  private ArrayList<Faculty> faculties;
  private ArrayList<Department> departments;
  ArrayList<Election> elections;
  ArrayList<CandidateList> candidateLists;
  ArrayList<VotingTable> votingTables;

  public RMIImpl() throws RemoteException {
    super();

    try {
      FileWrapper data = new FileWrapper();
      users = data.users;
      faculties = data.faculties;
      departments = data.departments;
    } catch (ClassNotFoundException e) {
      System.out.println("Class Not Found Exception " + e);
    } catch (java.io.IOException e) {
      System.out.println("java.io.IOException " + e);
    }
  }

  public void remote_print(String s) throws RemoteException {
    System.out.println("Server: " + s);
  }

  public void createUser(String name, String password, Department department, Faculty faculty, String contact, String address, String cc, String expireDate, int type) {
    User user = new User(name, password, department, faculty, contact, address, cc, expireDate, type);
  }

  public void createFaculty(String name) throws RemoteException {

  }

  public void createDepartment(String name, int facultyID) throws RemoteException {

  }

  public void updateDepartment(Department department) throws RemoteException {

  }

  public void updateFaculty(Faculty faculty) throws RemoteException {

  }

  public Department removeDepartment(Department department) throws RemoteException {
    return null;
  }

  public Faculty removeFaculty(Faculty faculty) throws RemoteException {
    return null;
  }

  public void createElection(String name, String description, Date startDate, Date endDate, int type) throws RemoteException {

  }

  public void updateElection(Election election) throws RemoteException {

  }

  public void createList(Election electionID, int[] candidatesIDs) throws RemoteException {

  }

  public void updateList(Election electionID, int[] candidatesIDs) throws RemoteException {

  }

  public List removeList(CandidateList candidateList) throws RemoteException {
    return null;
  }

  public void createVotingTable(Election election, int machineID, int[] votingTerminalsIDs, String location) throws RemoteException {

  }

  public VotingTable removeVotingTable() throws RemoteException {
    return null;
  }

  public void createVotingTerminal(int status) throws RemoteException {

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
    try {
      FileWrapper fw = new FileWrapper();
      ArrayList<User> users = fw.users;
      ArrayList<String> values = fieldValues(field, users);

      for (int i = 0; i < values.size(); i++) {
        if (values.get(i).equals(value)) {
          return users.get(i);
        }
      }

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }

  public void authenticateUser(String name, String password) throws RemoteException {

  }

  public void vote(int userID, int electionID, int candidateListID) throws RemoteException {

  }

  public List getVotingInfo(User user, Election election) throws RemoteException {
    return null;
  }

  public void getElectionResults() throws RemoteException {

  }

  public Department getDepartmentByName(String departmentName) {
    for (int i = 0; i < departments.size(); i++) {
      if (departments.get(i).getName().equals(departmentName)) {
        return departments.get(i);
      }
    }
    return null;
  }

  public Faculty getFacultyByName(String facultyName) {
    for (int i = 0; i < faculties.size(); i++) {
      if (faculties.get(i).getName().equals(facultyName)) {
        return faculties.get(i);
      }
    }
    return null;
  }

  // args server => 1
  // args backup => 0 localhost

  public static void main(String args[]) {
    int isMainServer = Integer.parseInt(args[0]);

    if (isMainServer == 1) {
      try {
        RMIImpl server = new RMIImpl();
        NewThread thread = new NewThread("CheckRMIServerStatus");

        /*
        System.out.println(server.users);
        System.out.println(server.faculties);
        System.out.println(server.departments); */

        Registry reg = LocateRegistry.createRegistry(1099);
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

  // The backup server will be a client that will send a message to the main server every 5 seconds. The main server has
  // 1 second to reply to the backup server. If it doesn't reply we have to turn the backup server into the main server.
  // The backup server then has to read the object files to get the updated data.

  public static void backupServer(String args[]) {

    if(args.length == 0) {
      System.out.println("java UDPClient hostname");
      System.exit(0);
    }
    DatagramSocket aSocket = null;

    try {
      aSocket = new DatagramSocket();
      String text = "Server Status OK";
      int numberOfFails = 0;

      while (true) {
        byte[] m = text.getBytes();
        InetAddress aHost = InetAddress.getByName(args[1]);
        int serverPort = 6789;

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
              Registry reg = LocateRegistry.createRegistry(8000);
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

  NewThread(String threadName) {
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
      aSocket = new DatagramSocket(6789);
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
