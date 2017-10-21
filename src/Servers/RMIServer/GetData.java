package Servers.RMIServer;

import Data.*;
import java.io.IOException;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GetData {

  ArrayList<User> users;
  ArrayList<Faculty> faculties;
  ArrayList<Department> departments;
  ArrayList<Election> elections;
  ArrayList<CandidateList> candidateLists;
  ArrayList<VotingTable> votingTables;

  public GetData() throws IOException, ClassNotFoundException {

    /*Department department1 = new Department("EngenhariaInformatica");
    ArrayList<Department> departments = new ArrayList<Department>();
    departments.add(department1);

    Faculty faculty1 = new Faculty("FCTUC");
    ArrayList<Faculty> faculties = new ArrayList<Faculty>();
    faculties.add(faculty1);

    User user1= new User("Teresa", "123", department1, faculty1, "9140975", "Rua X", "444",
            "expireDate", 1 );
    ArrayList<User> users = new ArrayList<User>();
    users.add(user1);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

    long startDate = 0;
    long endDate = 0;
    try {
      startDate = simpleDateFormat.parse("10/10/2017 11:30:10").getTime();
      endDate = simpleDateFormat.parse("10/10/2017 18:30:10").getTime();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }

    Election election1 = new Election("NEI", "Nucleo Estudantes Informatica", startDate, endDate, 1, department1);
    ArrayList<Election> elections = new ArrayList<Election>();
    elections.add(election1);

    ArrayList<CandidateList> candidateLists = new ArrayList<CandidateList>();
    CandidateList candidateList1 = new CandidateList("NEIlista1", users);
    candidateLists.add(candidateList1);
    election1.addCandidateList(candidateList1);

    ArrayList<VotingTable> votingTables = new ArrayList<VotingTable>();
    ArrayList<VotingTerminal> votingTerminals = new ArrayList<VotingTerminal>();
    VotingTable votingTable = new VotingTable(election1, department1, votingTerminals);
    votingTables.add(votingTable);

    writeFile(users,"Users");
    writeFile(departments,"Departments");
    writeFile(faculties,"Faculties");
    writeFile(elections,"Elections");
    writeFile(candidateLists,"CandidateLists");
    writeFile(candidateLists,"VotingTables"); */

    ArrayList<User> usersFromFile = (ArrayList<User>) readFile("User");
    ArrayList<Department> departmentsFromFile = (ArrayList<Department>) readFile("Department");
    ArrayList<Faculty> facultiesFromFile = (ArrayList<Faculty>) readFile("Faculty");
    ArrayList<Election> electionsFromFile = (ArrayList<Election>) readFile("Election");
    ArrayList<CandidateList> candidateListsFromFile = (ArrayList<CandidateList>) readFile("CandidateList");
    ArrayList<VotingTable> votingTablesFromFile = (ArrayList<VotingTable>) readFile("VotingTable");

    this.users = usersFromFile;
    this.departments = departmentsFromFile;
    this.faculties = facultiesFromFile;
    this.elections = electionsFromFile;
    this.candidateLists = candidateListsFromFile;
    this.votingTables = votingTablesFromFile;
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


}
