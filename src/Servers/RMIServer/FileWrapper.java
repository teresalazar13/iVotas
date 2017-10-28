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

  public FileWrapper() throws IOException, ClassNotFoundException {

    // -------------DEPARTAMENTOS----------------
    Department department1 = new Department("EngenhariaInformatica");
    Department department2 = new Department("EngenhariaMecanica");
    Department department3 = new Department("Gestao");
    Department department4 = new Department("Economia");
    ArrayList<Department> departments = new ArrayList<Department>();
    departments.add(department1);
    departments.add(department2);
    departments.add(department3);
    departments.add(department4);

    // -------------FACULDADES----------------
    Faculty faculty1 = new Faculty("FCTUC");
    faculty1.addDepartment(department1);
    faculty1.addDepartment(department2);
    Faculty faculty2 = new Faculty("FEUC");
    faculty2.addDepartment(department3);
    faculty2.addDepartment(department4);
    ArrayList<Faculty> faculties = new ArrayList<Faculty>();
    faculties.add(faculty1);
    faculties.add(faculty2);

    // -------------UTILIZADORES----------------
    // 10 eleitores FCTUC
    User user1= new User("Teresa", "123", department1, faculty1, "9140975123", "Rua A", "11111111",
            "01/18", 1 );
    User user2= new User("Miguel", "123", department1, faculty1, "91409751234", "Rua B", "11111112",
            "02/18", 1 );
    User user3= new User("Pedro", "123", department1, faculty1, "9140975125", "Rua C", "11111113",
            "03/18", 1 );
    User user4= new User("Maria", "123", department1, faculty1, "9140975126", "Rua D", "11111114",
            "04/18", 1 );
    User user5= new User("Paula", "123", department1, faculty1, "9140975127", "Rua E", "11111115",
            "05/18", 1 );
    User user6= new User("Joao", "123", department1, faculty1, "9140975128", "Rua F", "11111116",
            "06/18", 2 );
    User user7= new User("Guilherme", "123", department2, faculty1, "9140975129", "Rua G", "11111117",
            "07/18", 1 );
    User user8= new User("Nuno", "123", department2, faculty1, "9140975130", "Rua H", "11111118",
            "08/18", 2 );
    User user9= new User("Machado", "123", department2, faculty1, "9140975131", "Rua I", "11111119",
            "09/18", 3 );
    User user10= new User("Rafael", "123", department2, faculty1, "9140975132", "Rua J", "22222221",
            "10/18", 3 );
    // 10 eleitores FEUC
    User user11= new User("Rafaela", "123", department3, faculty2, "9140975133", "Rua K", "22222222",
            "11/18", 1 );
    User user12= new User("Diogo", "123", department3, faculty2, "9140975134", "Rua L", "22222223",
            "12/18", 1 );
    User user13= new User("Andre", "123", department3, faculty2, "9140975135", "Rua M", "22222224",
            "01/19", 2 );
    User user14= new User("Ana", "123", department3, faculty2, "9140975136", "Rua N", "22222225",
            "02/19", 2 );
    User user15= new User("Ines", "123", department3, faculty2, "9140975137", "Rua O", "22222226",
            "03/19", 3 );
    User user16= new User("David", "123", department4, faculty2, "9140975138", "Rua P", "22222227",
            "04/19", 3 );
    User user17= new User("Francisco", "123", department4, faculty2, "9140975139", "Rua Q", "22222228",
            "05/19", 1 );
    User user18= new User("Vanessa", "123", department4, faculty2, "9140975140", "Rua R", "22222229",
            "06/19", 2 );
    User user19= new User("Henrique", "123", department4, faculty2, "9140975141", "Rua S", "22222210",
            "07/19", 3 );
    User user20= new User("Mario", "123", department4, faculty2, "9140975142", "Rua V", "22222212",
            "08/19", 1 );
    User user21= new User("Cesar", "123", department1, faculty1, "9140975143", "Rua A", "22222212",
            "01/18", 1 );
    User user22= new User("Napoleao", "123", department1, faculty1, "91409751244", "Rua B", "22222213",
            "02/18", 1 );
    User user23= new User("Winston", "123", department1, faculty1, "9140975145", "Rua C", "22222214",
            "03/18", 1 );
    User user24= new User("Pablo", "123", department1, faculty1, "9140975146", "Rua D", "22222215",
            "04/18", 1 );
    User user25= new User("Jordi", "123", department1, faculty1, "9140975147", "Rua E", "22222216",
            "05/18", 1 );
    User user26= new User("Pique", "123", department1, faculty1, "9140975148", "Rua F", "22222217",
            "06/18", 1 );
    User user27= new User("Rosa", "123", department3, faculty2, "91409751123", "Rua K", "22222222",
            "11/18", 1 );
    User user28= new User("Alba", "123", department3, faculty2, "9140975312", "Rua L", "22222223",
            "12/18", 1 );

    ArrayList<User> users = new ArrayList<User>();
    users.add(user1);
    users.add(user2);
    users.add(user3);
    users.add(user4);
    users.add(user5);
    users.add(user6);
    users.add(user7);
    users.add(user8);
    users.add(user9);
    users.add(user10);
    users.add(user11);
    users.add(user12);
    users.add(user13);
    users.add(user14);
    users.add(user15);
    users.add(user16);
    users.add(user17);
    users.add(user18);
    users.add(user19);
    users.add(user20);


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    long startDate = 0;
    long startDate2 = 0;
    long startDate3 = 0;
    long endDate = 0;
    long endDate2 = 0;
    long endDate3 = 0;
    try {
      startDate = simpleDateFormat.parse("28/10/2017 13:35:00").getTime();
      endDate = simpleDateFormat.parse("06/11/2017 15:20:00").getTime();
      startDate2 = simpleDateFormat.parse("10/04/2017 09:30:00").getTime();
      endDate2 = simpleDateFormat.parse("17/04/2017 19:00:00").getTime();
      startDate3 = simpleDateFormat.parse("02/03/2017 10:30:00").getTime();
      endDate3 = simpleDateFormat.parse("09/02/2017 19:00:00").getTime();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }

    // -------------LISTAS CANDIDATAS----------------
    ArrayList<CandidateList> candidateLists = new ArrayList<CandidateList>();
    // NEI
    ArrayList<User> usersC1 = new ArrayList<>();
    usersC1.add(user21);
    usersC1.add(user22);
    usersC1.add(user23);
    CandidateList candidateList = new CandidateList("LISTA A", usersC1);

    ArrayList<User> usersC2 = new ArrayList<>();
    usersC2.add(user24);
    usersC2.add(user25);
    usersC2.add(user26);
    CandidateList candidateList2 = new CandidateList("LISTA B", usersC2);

    // Conselho geral
    // Alunos
    ArrayList<User> usersC3 = new ArrayList<>();
    usersC3.add(user20);
    usersC3.add(user17);
    CandidateList candidateList3 = new CandidateList("LISTA C", usersC3);

    ArrayList<User> usersC4 = new ArrayList<>();
    usersC4.add(user5);
    usersC4.add(user7);
    CandidateList candidateList4 = new CandidateList("LISTA D", usersC4);

    // Professores
    ArrayList<User> usersC5 = new ArrayList<>();
    usersC5.add(user14);
    usersC5.add(user18);
    CandidateList candidateList5 = new CandidateList("LISTA E", usersC5);

    ArrayList<User> usersC6 = new ArrayList<>();
    usersC6.add(user8);
    usersC6.add(user13);
    CandidateList candidateList6 = new CandidateList("LISTA F", usersC6);

    // Funcionarios
    ArrayList<User> usersC7 = new ArrayList<>();
    usersC7.add(user19);
    usersC7.add(user16);
    usersC7.add(user15);
    CandidateList candidateList7 = new CandidateList("LISTA G", usersC7);

    ArrayList<User> usersC8 = new ArrayList<>();
    usersC8.add(user9);
    usersC8.add(user10);
    CandidateList candidateList8 = new CandidateList("LISTA H", usersC8);

    // NEG
    ArrayList<User> usersC9 = new ArrayList<>();
    usersC9.add(user11);
    usersC9.add(user12);
    CandidateList candidateList9 = new CandidateList("LISTA I", usersC9);

    ArrayList<User> usersC10 = new ArrayList<>();
    usersC10.add(user27);
    usersC10.add(user28);
    CandidateList candidateList10 = new CandidateList("LISTA J", usersC10);

    candidateLists.add(candidateList);
    candidateLists.add(candidateList2);
    candidateLists.add(candidateList3);
    candidateLists.add(candidateList4);
    candidateLists.add(candidateList5);
    candidateLists.add(candidateList6);
    candidateLists.add(candidateList7);
    candidateLists.add(candidateList8);
    candidateLists.add(candidateList9);
    candidateLists.add(candidateList10);

    // -------------ELECTIONS----------------
    Election election1 = new Election("NEI 20172018", "Nucleo Estudantes Informatica", startDate, endDate, 1, department1);
    election1.addCandidateList(candidateList);
    election1.addCandidateList(candidateList2);
    Election election2 = new Election("Conselho Geral 20172018", "Nucleo Estudante Informatica", startDate2, endDate2, 2);
    election2.addCandidateList(candidateList3);
    election2.addCandidateList(candidateList4);
    election2.addCandidateList(candidateList5);
    election2.addCandidateList(candidateList6);
    election2.addCandidateList(candidateList7);
    election2.addCandidateList(candidateList8);
    Election election3 = new Election("NEEG 20172018", "Nucleo Estudantes Gestao", startDate3, endDate3, 1, department3);
    election3.addCandidateList(candidateList9);
    election3.addCandidateList(candidateList10);
    ArrayList<Election> elections = new ArrayList<Election>();
    elections.add(election1);
    elections.add(election2);
    elections.add(election3);

    // -------------MESAS DE VOTO----------------
    ArrayList<VotingTable> votingTables = new ArrayList<VotingTable>();
    VotingTable votingTable = new VotingTable(0, election1, department1);
    VotingTable votingTable2 = new VotingTable(1, election1, department1);
    VotingTable votingTable3 = new VotingTable(2, election1, department1);
    VotingTable votingTable4 = new VotingTable(3, election1, department1);
    votingTables.add(votingTable);
    votingTables.add(votingTable2);
    votingTables.add(votingTable3);
    votingTables.add(votingTable4);

    // -------------VOTOS----------------
    ArrayList<Vote> votes = new ArrayList<>();
    // Estudantes conselho geral
    Vote vote1 = new Vote(user1, election2, candidateList4, department1);
    Vote vote2 = new Vote(user2, election2, candidateList4, department1);
    Vote vote3 = new Vote(user3, election2, candidateList4, department1);
    Vote vote4 = new Vote(user4, election2, candidateList4, department1);
    Vote vote5 = new Vote(user5, election2, department1);
    Vote vote7 = new Vote(user11, election2, candidateList3, department3);
    Vote vote8 = new Vote(user12, election2, candidateList3, department3);
    Vote vote9 = new Vote(user17, election2, candidateList3, department4);

    // Professores conselho geral
    Vote vote10 = new Vote(user6, election2, candidateList5, department1);
    Vote vote11 = new Vote(user8, election2, candidateList5, department2);
    Vote vote12 = new Vote(user13, election2, candidateList6, department3);
    Vote vote13 = new Vote(user14, election2, candidateList5, department3);
    Vote vote14 = new Vote(user18, election2, department4);

    // Funcionarios conselho geral
    Vote vote15 = new Vote(user9, election2, candidateList8, department2);
    Vote vote16 = new Vote(user10, election2, candidateList7, department2);
    Vote vote17 = new Vote(user15, election2, candidateList7, department3);
    Vote vote18 = new Vote(user16, election2, candidateList7, department4);
    Vote vote19 = new Vote(user19, election2, department4);

    // Estudantes NEG
    Vote vote20 = new Vote(user11, election3, candidateList9, department3);
    Vote vote21 = new Vote(user12, election3, candidateList10, department3);
    Vote vote22 = new Vote(user27, election3, candidateList9, department3);
    Vote vote23 = new Vote(user28, election3, department4);


    votes.add(vote1);
    votes.add(vote2);
    votes.add(vote3);
    votes.add(vote4);
    votes.add(vote5);
    votes.add(vote7);
    votes.add(vote8);
    votes.add(vote9);
    votes.add(vote10);
    votes.add(vote11);
    votes.add(vote12);
    votes.add(vote13);
    votes.add(vote14);
    votes.add(vote15);
    votes.add(vote16);
    votes.add(vote17);
    votes.add(vote18);
    votes.add(vote19);
    votes.add(vote20);
    votes.add(vote21);
    votes.add(vote22);
    votes.add(vote23);


    writeFile(users,"Users");
    writeFile(departments,"Departments");
    writeFile(faculties,"Faculties");
    writeFile(elections,"Elections");
    writeFile(candidateLists,"CandidateLists");
    writeFile(votingTables,"VotingTables");
    writeFile(votes,"Votes");

    ArrayList<User> usersFromFile = (ArrayList<User>) readFile("User");
    ArrayList<Department> departmentsFromFile = (ArrayList<Department>) readFile("Department");
    ArrayList<Faculty> facultiesFromFile = (ArrayList<Faculty>) readFile("Faculty");
    ArrayList<Election> electionsFromFile = (ArrayList<Election>) readFile("Election");
    ArrayList<CandidateList> candidateListsFromFile = (ArrayList<CandidateList>) readFile("CandidateList");
    ArrayList<VotingTable> votingTablesFromFile = (ArrayList<VotingTable>) readFile("VotingTable");
    ArrayList<Vote> votesFromFile = (ArrayList<Vote>) readFile("Vote");

    this.users = usersFromFile;
    this.departments = departmentsFromFile;
    this.faculties = facultiesFromFile;
    this.elections = electionsFromFile;
    this.candidateLists = candidateListsFromFile;
    this.votingTables = votingTablesFromFile;
    this.votes = votesFromFile;
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