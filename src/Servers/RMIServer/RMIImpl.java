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

public class RMIImpl extends UnicastRemoteObject implements RMIInterface {

  private static ArrayList<User> users;
  private static ArrayList<Faculty> faculties;
  private static ArrayList<Department> departments;
  ArrayList<Election> elections;
  ArrayList<CandidateList> candidateLists;
  ArrayList<VotingTable> votingTables;

  protected RMIImpl() throws RemoteException {
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

  public User identifyUser(String field, String value) throws RemoteException {
    try {
      FileWrapper fw = new FileWrapper();
      ArrayList<User> users = fw.users;
      ArrayList<String> values = fieldValues(field, users);

      for (int i = 0; i < values.size(); i++) {
        if (values.get(i) == value) {
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

  public static void main(String args[]) {

    try {
      RMIImpl server = new RMIImpl();

      System.out.println(users);
      System.out.println(faculties);
      System.out.println(departments);

      Registry reg = LocateRegistry.createRegistry(7000);
      reg.rebind("ivotas", server);
      System.out.println("Project Server ready.");
    } catch (RemoteException re) {
      System.out.println("Exception in RMIImpl.main: " + re);
    }
  }

}
