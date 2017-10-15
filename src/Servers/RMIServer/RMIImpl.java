package Servers.RMIServer;

import Data.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIImpl extends UnicastRemoteObject implements RMIInterface {
  // para escrever e ler de ficherios de objectos temos aqui as array lists que vao ter tudo
  ArrayList<CandidateList> candidateLists;
  ArrayList<Faculty> faculties;
  ArrayList<Election> elections;
  ArrayList<User> users;
  ArrayList<VotingTable> votingTables;

  protected RMIImpl() throws RemoteException {
    super();
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

  public void createVotingTerminal(int status) {

  }

  public void identifyUser(String field, String res) {

  }

  public void authenticateUser(String name, String password) {

  }

  public void vote(int userID, int electionID, int candidateListID) {

  }

  public List getVotingInfo(User user, Election election) throws RemoteException {
    return null;
  }

  public void getElectionResults() {

  }
}
