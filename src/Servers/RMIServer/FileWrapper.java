package Servers.RMIServer;

import Data.*;
import java.io.IOException;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FileWrapper {

  ArrayList<User> users;
  ArrayList<Faculty> faculties;
  ArrayList<Department> departments;
  ArrayList<Election> elections;
  ArrayList<CandidateList> candidateLists;
  ArrayList<VotingTable> votingTables;
  ArrayList<Vote> votes;
  ArrayList<ElectionResult> electionResults;


  public FileWrapper() throws IOException, ClassNotFoundException {

    Department department1 = new Department("EngenhariaInformatica");
    ArrayList<Department> departments = new ArrayList<Department>();
    departments.add(department1);

    Faculty faculty1 = new Faculty("FCTUC");
    ArrayList<Faculty> faculties = new ArrayList<Faculty>();
    faculties.add(faculty1);

    User user1= new User("Teresa", "123", department1, faculty1, "9140975", "Rua X", "444",
            "expireDate", 1 );
    User user2= new User("Test", "123", department1, faculty1, "9140975", "Rua X", "444",
            "expireDate", 1 );
    User user3= new User("Machado", "123", department1, faculty1, "9140975", "Rua X", "444",
            "expireDate", 2 );
    ArrayList<User> users = new ArrayList<User>();
    users.add(user1);
    users.add(user2);
    users.add(user3);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    long startDate = 0;
    long endDate = 0;
    try {
      startDate = simpleDateFormat.parse("24/9/2017 16:15:00").getTime();
      endDate = simpleDateFormat.parse("24/9/2017 16:20:00").getTime();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }

    ArrayList<CandidateList> candidateLists = new ArrayList<CandidateList>();
    CandidateList candidateList = new CandidateList("LISTAA", users);
    CandidateList candidateList2 = new CandidateList("LISTAB", users);
    candidateLists.add(candidateList);
    candidateLists.add(candidateList2);

    Election election1 = new Election("NEI", "Nucleo Estudante Informatica", startDate, endDate, 1, department1);
    election1.addCandidateList(candidateList);
    election1.addCandidateList(candidateList2);
    ArrayList<Election> elections = new ArrayList<Election>();
    elections.add(election1);

    ArrayList<VotingTable> votingTables = new ArrayList<VotingTable>();
    ArrayList<VotingTerminal> votingTerminals = new ArrayList<VotingTerminal>();
    VotingTable votingTable = new VotingTable(0, election1, department1, votingTerminals);
    votingTables.add(votingTable);

    ArrayList<Vote> votes = new ArrayList<>();
    Vote vote1 = new Vote(user1, election1, candidateList, department1);
    votes.add(vote1);

    ArrayList<ElectionResult> electionResults = new ArrayList<ElectionResult>();
    ArrayList<CandidateResults> candidatesResults = new ArrayList<CandidateResults>();
    CandidateResults candidateResults1 = new CandidateResults(candidateList2, 100, 100);
    candidatesResults.add(candidateResults1);
    ElectionResult electionResults1 = new ElectionResult(election1, candidatesResults, 0, 0);
    electionResults.add(electionResults1);

    writeFile(users,"Users");
    writeFile(departments,"Departments");
    writeFile(faculties,"Faculties");
    writeFile(elections,"Elections");
    writeFile(candidateLists,"CandidateLists");
    writeFile(votingTables,"VotingTables");
    writeFile(votes,"Votes");
    writeFile(electionResults,"ElectionResults");

    ArrayList<User> usersFromFile = (ArrayList<User>) readFile("User");
    ArrayList<Department> departmentsFromFile = (ArrayList<Department>) readFile("Department");
    ArrayList<Faculty> facultiesFromFile = (ArrayList<Faculty>) readFile("Faculty");
    ArrayList<Election> electionsFromFile = (ArrayList<Election>) readFile("Election");
    ArrayList<CandidateList> candidateListsFromFile = (ArrayList<CandidateList>) readFile("CandidateList");
    ArrayList<VotingTable> votingTablesFromFile = (ArrayList<VotingTable>) readFile("VotingTable");
    ArrayList<Vote> votesFromFile = (ArrayList<Vote>) readFile("Vote");
    ArrayList<ElectionResult> electionResultsFromFile = (ArrayList<ElectionResult>) readFile("ElectionResult");

    this.users = usersFromFile;
    this.departments = departmentsFromFile;
    this.faculties = facultiesFromFile;
    this.elections = electionsFromFile;
    this.candidateLists = candidateListsFromFile;
    this.votingTables = votingTablesFromFile;
    this.votes = votesFromFile;
    this.electionResults = electionResultsFromFile;
  }

  public void writeFile(Object classe, String className) throws IOException, ClassNotFoundException {
    String filename = "ObjectFiles/" + className + ".dat";
    File file = new File();
    file.openWrite(filename);
    file.writeObject(classe);
    file.closeWrite();
  }

  private static ArrayList readFile(String className) throws IOException, ClassNotFoundException {
    String filename;
    if (className.substring(className.length() - 1).equals("y")) {
      filename = "ObjectFiles/" + className.substring(0, className.length() - 1) + "ies" + ".dat";
    }
    else {
      filename = "ObjectFiles/" + className + "s" + ".dat";
    }
    File file = new File();
    file.openRead(filename);
    ArrayList <Object> array = null;
    try {
      array = (ArrayList <Object>) file.readObject();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    file.closeRead();
    return array;
  }

  public ArrayList<User> getUsers() {
    return users;
  }

  public ArrayList<Faculty> getFaculties() {
    return faculties;
  }

  public ArrayList<Department> getDepartments() {
    return departments;
  }

  public ArrayList<Election> getElections() {
    return elections;
  }

  public ArrayList<CandidateList> getCandidateLists() {
    return candidateLists;
  }

  public ArrayList<VotingTable> getVotingTables() {
    return votingTables;
  }
}