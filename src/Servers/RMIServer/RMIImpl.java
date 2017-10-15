package Servers.RMIServer;

import Data.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIImpl extends UnicastRemoteObject implements RMIInterface {
  ArrayList<CandidateList> candidateLists;
  ArrayList<Faculty> faculties;
  ArrayList<Election> elections;
  ArrayList<User> users;
  ArrayList<VotingTable> votingTables;

  protected RMIImpl() throws RemoteException {
    super();
  }

  @Override
  public void createUser(String name, String password, int departmentID, int facultyID, String contact, String address, String cc, String expireDate, int type) throws RemoteException {

  }

  @Override
  public void createFaculty(String name) throws RemoteException {

  }

  @Override
  public void createDepartment(String name, int facultyID) throws RemoteException {

  }

  @Override
  public void updateDepartment(Department department) throws RemoteException {

  }

  @Override
  public void updateFaculty(Faculty faculty) throws RemoteException {

  }

  @Override
  public Department removeDepartment(Department department) throws RemoteException {
    return null;
  }

  @Override
  public Faculty removeFaculty(Faculty faculty) throws RemoteException {
    return null;
  }

  @Override
  public void createElection(String name, String description, Date startDate, Date endDate, int type) throws RemoteException {

  }

  @Override
  public void updateElection(Election election) throws RemoteException {

  }

  @Override
  public void createList(Election electionID, int[] candidatesIDs) throws RemoteException {

  }

  @Override
  public void updateList(Election electionID, int[] candidatesIDs) throws RemoteException {

  }

  @Override
  public List removeList(CandidateList candidateList) throws RemoteException {
    return null;
  }

  @Override
  public void createVotingTable(Election election, int machineID, int[] votingTerminalsIDs, String location) throws RemoteException {

  }

  @Override
  public VotingTable removeVotingTable() throws RemoteException {
    return null;
  }

  @Override
  public void createVotingTerminal(int status) {

  }

  @Override
  public void identifyUser(String field, String res) {

  }

  @Override
  public void authenticateUser(String name, String password) {

  }

  @Override
  public void vote(int userID, int electionID, int candidateListID) {

  }

  @Override
  public List getVotingInfo(User user, Election election) throws RemoteException {
    return null;
  }

  @Override
  public void getElectionResults() {

  }
}
