package Servers.RMIServer;

import Data.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FileWrapper {

  ArrayList<User> users;
  ArrayList<Faculty> faculties;
  ArrayList<Department> departments;
  ArrayList<Election> elections;
  ArrayList<CandidateList> candidateLists;
  ArrayList<VotingTable> votingTables;

  public FileWrapper() throws IOException, ClassNotFoundException {
    Department department1 = new Department("EngenhariaInformatica");
    Department department2 = new Department("EngenhariaMecanica");
    Department department3 = new Department("EngenhariaEletro");
    ArrayList<Department> departments = new ArrayList<Department>();
    departments.add(department1);
    departments.add(department2);
    departments.add(department3);

    Faculty faculty1 = new Faculty("FCTUC", departments);
    Faculty faculty2 = new Faculty("FMUC", departments);
    Faculty faculty3 = new Faculty("FDUC", departments);
    ArrayList<Faculty> faculties = new ArrayList<Faculty>();
    faculties.add(faculty1);
    faculties.add(faculty2);
    faculties.add(faculty3);

    User user1 = new User("Teresa", "123", department1, faculty1, "9140975", "Rua X", "444",
            "expireDate", 1 );
    User user2 = new User("Miguel", "1234", department2, faculty2, "91409756", "Rua Y", "555",
            "expireDate2", 2 );
    User user3 = new User("Teresa", "12345", department3, faculty3, "91409757", "Rua Z", "666",
            "expireDate3", 1 );
    ArrayList<User> users = new ArrayList<User>();
    users.add(user1);
    users.add(user2);
    users.add(user3);

    CandidateList candidateList1 = new CandidateList(users);
    CandidateList candidateList2 = new CandidateList(users);
    CandidateList candidateList3 = new CandidateList(users);
    ArrayList<CandidateList> cls = new ArrayList<>();
    cls.add(candidateList1);
    cls.add(candidateList2);
    cls.add(candidateList3);

    Date start = new Date(1,1,1,1,1);
    Date end = new Date(1,2,1,1,1);
    Election election = new Election("NEI", "Nucleo de estudantes de informatica", start, end, 1, cls, null);
    ArrayList<Election> elections = new ArrayList<>();
    elections.add(election);

    writeFile(users,"Users");
    ArrayList<User> usersFromFile = (ArrayList<User>) readFile("User");

    writeFile(departments,"Departments");
    ArrayList<Department> departmentsFromFile = (ArrayList<Department>) readFile("Department");

    writeFile(faculties,"Faculties");
    ArrayList<Faculty> facultiesFromFile = (ArrayList<Faculty>) readFile("Faculty");

    writeFile(cls,"CandidateLists");
    ArrayList<CandidateList> candidateListsFromFile = (ArrayList<CandidateList>) readFile("CandidateList");

    writeFile(elections,"Elections");
    ArrayList<Election> electionsFromFile = (ArrayList<Election>) readFile("Election");

    this.users = usersFromFile;
    this.departments = departmentsFromFile;
    this.faculties = facultiesFromFile;
    this.candidateLists = candidateListsFromFile;
    this.elections = electionsFromFile;
  }

  private static void writeFile(Object classe, String className) throws IOException, ClassNotFoundException {
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

  public ArrayList<Department> getDepartments() {
    return departments;
  }

  public ArrayList<Election> getElections() {
    return elections;
  }

  public ArrayList<VotingTable> getVotingTables() {
    return votingTables;
  }

  public ArrayList<User> getUsers() {
    return users;
  }

  public ArrayList<Faculty> getFaculties() {
    return faculties;
  }

  public ArrayList<CandidateList> getCandidateLists() {
    return candidateLists;
  }
}
