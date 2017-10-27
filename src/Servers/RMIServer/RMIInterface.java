package Servers.RMIServer;

import Admin.AdminInterface;
import Data.*;
import java.rmi.*;
import java.util.ArrayList;


public interface RMIInterface extends Remote {

  /** Remote Print */
  void remote_print(String s) throws RemoteException;

  /** Creates a user. Checks if department and faculties with departmentName and facultyName exist. */
  int createUser(String name, String password, String departmentName, String facultyName, String contact, String address, String cc, String expireDate, int type) throws RemoteException;

  /** Creates faculty given the faculty name */
  void createFaculty(String name) throws RemoteException;

  /** Creates department given the department name and faculty name. Returns true if there is a faculty with
   * the name facultyName. Returns false otherwise. */
  boolean createDepartment(String name, String facultyName) throws RemoteException;

  /** Creates election of Conselho Geral.
   * Returns 1 if operation is successful. Returns 2 if start Date < current Date. */
  int createElection(String name, String description, long startDate, long endDate, int type) throws RemoteException;

  /** Creates election of Nucleo de Estudantes.
   * Returns 1 if operation is successful.
   * Returns 2 if department with departmentName doesn't exist.
   * Returns 3 if start date < current date. */
  int createStudentsElection(String name, String description, long startDate, long endDate, int type, String departmentName) throws RemoteException;

  /** Creates candidate list for Nucleo de Estudantes. */
  void createCandidateList(String name, ArrayList<User> users, Election election) throws RemoteException;

  /** Creates candidate list for Conselho Geral. */
  void createCandidateListCouncil(String name, ArrayList<User> users, Election election, int usersType) throws RemoteException;

  /** Creates voting table.
   * Returns 1 if operation is successful.
   * Retuns 2 if election with electionName doesn't exist.
   * Return 3 if department with departmentName doesn't exist */
  int createVotingTable(String electionName, String departmentName) throws RemoteException;

  /** Updates Department Name */
  void updateDepartmentName(Department department, String name) throws RemoteException;

  /** Updates Department in Faculty */
  void updateFacultyDepartment(Faculty faculty, Department department) throws RemoteException;

  /** Updates Department Name in Faculty */
  void updateFacultyDepartmentName(Department department, String name) throws RemoteException;

  /** Updates Faculty Name */
  void updateFacultyName(Faculty faculty, String name) throws RemoteException;

  /** Updates Election given type attribute to change and the change itself.
   * type 1 - change name
   * type 2 - changes description
   * type 3 - change start date
   * type 4 - change end date.
   * Returns 1 if operation is successful.
   * Returns 2 if there are no elections with the name electionName
   * Returns 3 if new end Date < current start date
   * Returns 4 if election already started or ended. */
  int updateElection(String electionName, Object toChange, int type) throws RemoteException;

  /** Removes department */
  void removeDepartment(Department department) throws RemoteException;

  /** Removes faculty **/
  void removeFaculty(Faculty faculty) throws RemoteException;

  /** Given a username and election name, prints where user voted in that elections.
   * If there isn't a user with the name username a error message appears.
   * If there isn't an election with the name electionName a error message appears. */
  String knowWhereUserVoted(String userName, String electionName) throws RemoteException;

  /** Prints details of past election: number of votes and percentage by candidate list,
   * number and percentage of blank votes and number and percentage of null votes */
  String detailsOfPastElections() throws RemoteException;

  /** Returns user given a username
   * If there isn't a user with the name username returns null */
  User getUserByName(String userName) throws RemoteException;

  /** Returns department given a department name
   * If there isn't a department with the name departmentName returns null */
  Department getDepartmentByName(String departmentName) throws RemoteException;

  /** Returns faculty given a faculty name
   * If there isn't a faculty with the name facultyName returns null */
  Faculty getFacultyByName(String facultyName) throws RemoteException;

  /** Returns election given a election Name
   * If there isn't a election with the name electionName returns null */
  Election getElectionByName(String electionName) throws RemoteException;

  /** Returns candidate List given a candidate List name
   * If there isn't a candidateList with the name listName returns null */
  CandidateList getCandidateListByName(String listName) throws RemoteException;

  /** Returns vote given a user and election */
  Vote getVoteByUserAndElection(User user, Election election) throws RemoteException;

  /** Gets voting table by iD */
  VotingTable getVotingTableById(int id) throws RemoteException;

  /** Prints data in a user friendly format */
  String prettyPrint(int option) throws RemoteException;

  /** Searches user by all types of attributes */
  User searchUser(String field, String res) throws RemoteException;

  /** Given a name and password authenticates user. Return true if operation is successful and
   * false otherwise. */
  boolean authenticateUser(String name, String password) throws RemoteException;

  /** Giver a user, election, candidate list and department, votes */
  void vote(User user, Election election, CandidateList candidateList, Department department) throws RemoteException;

  /** Return true if vote is valid and false otherwise. */
  boolean voteIsValid(User user, VotingTable votingTable, CandidateList candidateList) throws RemoteException;

  /** Given and admin, subscribes to RMI */
  void subscribe(AdminInterface client) throws RemoteException;

  /** Given and admin, cancels subscription to RMI */
  void unsubscribe(AdminInterface c) throws RemoteException;

  /** Given a string, sends it to all clients who are subscribing to RMI */
  void notifyAdmins(String s) throws RemoteException;

  /** Test */
  void test() throws RemoteException;
}
