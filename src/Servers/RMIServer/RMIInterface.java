package Servers.RMIServer;

import Data.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public interface RMIInterface extends Remote {

  void remote_print(String s) throws RemoteException;

  void createUser(String name, String password, Department department, Faculty faculty, String contact, String address, String cc, String expireDate, int type) throws RemoteException;

  void createFaculty(String name) throws RemoteException;

  void createDepartment(String name, Faculty faculty) throws RemoteException;

  void updateFacultyDepartment(Faculty faculty, Department department) throws RemoteException;

  void updateFacultyName(Faculty faculty, String name) throws RemoteException;

  void updateDepartmentName(Department department, String name) throws RemoteException;

  void updateFacultyDepartmentName(Department department, String name) throws RemoteException;

  int updateElection(String electionName, Object toChange, int type) throws RemoteException;

  void removeDepartment(Department department) throws RemoteException;

  void removeFaculty(Faculty faculty) throws RemoteException;

  void createElection(String name, String description, long startDate, long endDate, int type) throws RemoteException;

  boolean createStudentsElection(String name, String description, long startDate, long endDate, int type, String departmentName) throws RemoteException;

  void createCandidateList(String name, ArrayList<User> users, Election election) throws RemoteException;

  void createCandidateListCouncil(String name, ArrayList<User> users, Election election, int usersType) throws RemoteException;
  
  User searchUser(String field, String res) throws RemoteException;

  boolean authenticateUser(String name, String password) throws RemoteException;

  void vote(User user, Election election, CandidateList candidateList) throws RemoteException;

  List getVotingInfo(User user, Election election) throws RemoteException;

  void getElectionResults() throws RemoteException;

  User getUserByName(String userName) throws RemoteException;

  Department getDepartmentByName(String departmentName) throws RemoteException;

  Faculty getFacultyByName(String facultyName) throws RemoteException;

  Election getElectionByName(String electionName) throws RemoteException;

  Faculty getFacultyByDepartmentName(String department) throws RemoteException;

  CandidateList getCandidateListByName(String listName) throws RemoteException;
}
