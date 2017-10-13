import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface RMIInterface extends Remote {

    void createUser(String name, String password, int departmentID, int facultyID, String contact,
                           String address, String ccID, String expireDate, int type) throws RemoteException;

    void createFaculty(String name) throws RemoteException;
    void createDepartment(String name, int facultyID) throws RemoteException;

    void updateDepartment(Department department) throws RemoteException;
    void updateFaculty(Faculty faculty) throws RemoteException;

    Department removeDepartment(Department department) throws RemoteException;
    Faculty removeFaculty(Faculty faculty) throws RemoteException;

    void createElection(String name, String description, Date startDate, Date endDate, int type) throws RemoteException;
    void updateElection(Election election) throws RemoteException;

    void createList(Election election) throws RemoteException;
    void updateList(Election election, CandidateList candidateList) throws RemoteException;
    List removeList(CandidateList candidateList) throws RemoteException;

    void addVotingTable(Election election, int machineID, ArrayList<VotingTerminal> votingTerminals, String location) throws RemoteException;
    VotingTable removeVotingTable() throws RemoteException;

    void createVotingTerminal(int status);

    void identifyUser(String field, String res);

    void authenticateUser(String name, String password);

    void vote(User user, CandidateList candidateList, Election election);

    List getVotingInfo(User user, Election election) throws RemoteException;
    void getElectionResults();
}
