package Servers.RMIServer;

import Admin.AdminInterface;
import Data.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface RMIInterface extends Remote {

  void remote_print(String s) throws RemoteException;

  int createUser(String name, String password, String departmentName, String facultyName, String contact, String address, String cc, String expireDate, int type) throws RemoteException;

  void createFaculty(String name) throws RemoteException;

  boolean createDepartment(String name, String facultyName) throws RemoteException;

  int createElection(String name, String description, long startDate, long endDate, int type) throws RemoteException;

  int createStudentsElection(String name, String description, long startDate, long endDate, int type, String departmentName) throws RemoteException;

  void createCandidateList(String name, ArrayList<User> users, Election election) throws RemoteException;

  void createCandidateListCouncil(String name, ArrayList<User> users, Election election, int usersType) throws RemoteException;

  int createVotingTable(String electionName, String departmentName) throws RemoteException;

  void updateDepartmentName(Department department, String name) throws RemoteException;

  void updateFacultyDepartment(Faculty faculty, Department department) throws RemoteException;

  void updateFacultyDepartmentName(Department department, String name) throws RemoteException;

  void updateFacultyName(Faculty faculty, String name) throws RemoteException;

  int updateElection(String electionName, Object toChange, int type) throws RemoteException;

  void removeDepartment(Department department) throws RemoteException;

  void removeFaculty(Faculty faculty) throws RemoteException;

  String knowWhereUserVoted(String userName, String electionName) throws RemoteException;

  String detailsOfPastElections() throws RemoteException;

  User getUserByName(String userName) throws RemoteException;

  Department getDepartmentByName(String departmentName) throws RemoteException;

  Faculty getFacultyByName(String facultyName) throws RemoteException;

  Election getElectionByName(String electionName) throws RemoteException;

  CandidateList getCandidateListByName(String listName) throws RemoteException;

  Vote getVoteByUserAndElection(User user, Election election) throws RemoteException;

  VotingTable getVotingTableById(int id) throws RemoteException;

  String prettyPrint(int option) throws RemoteException;

  User searchUser(String field, String res) throws RemoteException;

  boolean authenticateUser(String name, String password) throws RemoteException;

  void vote(User user, Election election, CandidateList candidateList, Department department) throws RemoteException;

  boolean voteIsValid(User user, VotingTable votingTable, CandidateList candidateList) throws RemoteException;

  void subscribe(String name, AdminInterface client) throws RemoteException;

  void notifyAdmins(String s) throws RemoteException;
}
