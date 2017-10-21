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
            "expireDate", 1 );
    ArrayList<User> users = new ArrayList<User>();
    users.add(user1);
    users.add(user2);
    users.add(user3);

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

    writeFile(users,"Users");
    writeFile(departments,"Departments");
    writeFile(faculties,"Faculties");
    writeFile(elections,"Elections");
    writeFile(candidateLists,"CandidateLists");

    ArrayList<User> usersFromFile = (ArrayList<User>) readFile("User");
    ArrayList<Department> departmentsFromFile = (ArrayList<Department>) readFile("Department");
    ArrayList<Faculty> facultiesFromFile = (ArrayList<Faculty>) readFile("Faculty");
    ArrayList<Election> electionsFromFile = (ArrayList<Election>) readFile("Election");
    ArrayList<CandidateList> candidateListsFromFile = (ArrayList<CandidateList>) readFile("CandidateList");

    this.users = usersFromFile;
    this.departments = departmentsFromFile;
    this.faculties = facultiesFromFile;
    this.elections = electionsFromFile;
    this.candidateLists = candidateListsFromFile;
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