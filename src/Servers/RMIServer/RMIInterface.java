package Servers.RMIServer;

import Data.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface RMIInterface extends Remote {

  void remote_print(String s) throws RemoteException;

  void createUser(String name, String password, Department department, Faculty faculty, String contact, String address, String cc, String expireDate, int type) throws RemoteException;

  void createFaculty(String name) throws RemoteException;

  void createDepartment(String name, int facultyID) throws RemoteException;

  void updateDepartment(Department department) throws RemoteException;

  void updateFaculty(Faculty faculty) throws RemoteException;

  Department removeDepartment(Department department) throws RemoteException;

  Faculty removeFaculty(Faculty faculty) throws RemoteException;

  void createElection(String name, String description, Date startDate, Date endDate, int type) throws RemoteException;

  void updateElection(Election election) throws RemoteException;

  void createList(Election electionID, int[] candidatesIDs) throws RemoteException;

  void updateList(Election electionID, int[] candidatesIDs) throws RemoteException;

  List removeList(CandidateList candidateList) throws RemoteException;

  void createVotingTable(Election election, int machineID, int[] votingTerminalsIDs, String location) throws RemoteException;

  VotingTable removeVotingTable() throws RemoteException;

  void createVotingTerminal(int status) throws RemoteException;

  User searchUser(String field, String res) throws RemoteException;

  void authenticateUser(String name, String password) throws RemoteException;

  void vote(int userID, int electionID, int candidateListID) throws RemoteException;

  List getVotingInfo(User user, Election election) throws RemoteException;

  void getElectionResults() throws RemoteException;

  Department getDepartmentByName(String departmentName) throws RemoteException;

  Faculty getFacultyByName(String facultyName) throws RemoteException;

}
