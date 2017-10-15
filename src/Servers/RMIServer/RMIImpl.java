package Servers.RMIServer;

import Data.*;

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
      GetData data = new GetData();
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
    System.out.println("Server:" + s);
  }

  public void createUser(String name, String password, int departmentID, int facultyID, String contact, String address, String cc, String expireDate, int type) throws RemoteException {

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

  public void identifyUser(String field, String res) throws RemoteException {

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
