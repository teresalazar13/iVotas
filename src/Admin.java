import Data.*;
import Servers.RMIServer.*;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Admin {

  // ASK - pode haver facs, deps ou users com nomes iguais?
  // ASK - A lista de candidatos tem que ser composta por pessoas User ou basta Strings? ao criar assim a lista e necessario ir verificando se o user existe?
  // ASK - o que e que deve ser possivel configurar
  // ASK - Que tipo de testes temos que ter?
  // ASK - Pode haver listas de candidatos sem candidatos?
  // TODO - Funcoes 1, 2 -> verificar do lado do servidor tudo com boolean de resposta
  // TODO - Funcoes synchronized
  // TODO - Terminal
  // TODO - Votar nao pode ser perdido com excecao -> votar mais que uma vez nao
  // TODO - Mudar portos fixos
  // TODO - Adicionar mais dados default a BD

  private int port;

  public Admin() {}

  public Admin(int port) {
    this.port = port;
  }

  public static void main(String args[]) {

    // System.getProperties().put("java.security.policy", "policy.all");
    // System.setSecurityManager(new RMISecurityManager());

    Admin a = new Admin(7000);
    connectRMIInterface(a);
  }

  public static void menu(RMIInterface r, Admin a) {
    while(true) {
      int option = getValidInteger(
              "Please choose an option:\n" +
              "1 - Register Person\n" +
              "2 - Manage Department or Faculty\n" +
              "3 - Create Election\n" +
              "4 - Create Candidate List of an Election\n" +
              "5 - Create Voting Table\n" +
              "6 - Change Election's properties\n" +
              "7 - Know where a User has voted\n" +
              "8 - See details of past elections\n" +
              "9 to quit", 1, 9);
      switch (option) {
        case 1:
          createUser(r, a);
          break;
        case 2:
          management(r, a);
          break;
        case 3:
          createElection(r, a);
          break;
        case 4:
          createCandidateList(r, a);
          break;
        case 5:
          createVotingTable(r, a);
          break;
        case 6:
          try {
            r.remote_print("XXXXXXXX");
          } catch (Exception e) {
            System.out.println("Fail on Server");
            connectRMIInterface(a);
          }
          break;
        case 7:
          break;
        case 8:
          break;
        default:
          return;
      }
    }
  }

  public static void createUser(RMIInterface r, Admin a) {
    int option = getValidInteger(
            "What type of User do you want to Create?\n" +
                    "1 - Student\n" +
                    "2 - Teacher\n" +
                    "3 - Staff\n" +
                    "4 back", 1,4);
    if (option == 4) return;
    String name = getValidString("Name: ");
    String password = getValidString("Password: ");
    String departmentName = getValidString("Department: ");
    String facultyName = getValidString("Faculty: ");
    String contact = getValidString("Contact: ");
    String address = getValidString("Address: ");
    String cc = getValidString("CC: ");
    String expireDate = getValidString("Expire date: ");
    int type = option;

    Department department = null;
    Faculty faculty = null;
    try {
      department = r.getDepartmentByName(departmentName);
      if (department == null) {
        System.out.println("There isn't a department with that name.");
        return;
      }
    }
    catch (RemoteException e) {
      System.out.println("Main Server crashed. Connecting to Backup Server..." );
      connectRMIInterface(a);
      return;
    }

    try {
      faculty = r.getFacultyByName(facultyName);
      if (faculty == null) {
        System.out.println("There isn't a faculty with that name.");
        return;
      }
    }
    catch (RemoteException e) {
      System.out.println("Remote Exception, " + e);
      connectRMIInterface(a);
    }

    try {
      r.createUser(name, password, department, faculty, contact, address, cc, expireDate, type);
    }
    catch (RemoteException e) {
      System.out.println("Remote Exception, " + e);
      connectRMIInterface(a);
    }
  }

  public static void management(RMIInterface r, Admin a) {
    int option = getValidInteger("What do you want to manage?\n" +
            "1 - Faculty\n" +
            "2 - Department\n" +
            "3 back", 1, 3);
    if (option == 3) return;

    int option2 = getValidInteger("What do you do?\n" +
            "1 - Create\n" +
            "2 - Update\n" +
            "3 - Remove\n" +
            "4 back", 1,4);
    if (option2 == 4) return;

    String name = getValidString("Name: ");

    if (option == 1) {
      if (option2 == 1) {
        try {
          r.createFaculty(name);
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception, " + e);
          connectRMIInterface(a);
        }
      }
      else if (option2 == 2) {
        Faculty faculty = null;
        try {
          faculty = r.getFacultyByName(name);
          if (faculty != null) {
            String newName = getValidString("New name: ");
            try {
              r.updateFacultyName(faculty, newName);
            }
            catch (RemoteException e) {
              System.out.println("Remote Exception in updating Faculty, " + e);
              connectRMIInterface(a);
            }
          }
          else {
            System.out.println("There isnt a faculty with that name.");
          }
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception in updating Faculty, " + e);
          connectRMIInterface(a);
        }
      }

      else if (option2 == 3) {
        Faculty faculty = null;
        try {
          faculty = r.getFacultyByName(name);
          if (faculty != null) {
            r.removeFaculty(faculty);
          }
          else {
            System.out.println("There isnt a faculty with that name.");
          }
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception in removing Faculty, " + e);
          connectRMIInterface(a);
        }
      }
    }

    else {
      if (option2 == 1) {
        String facultyName = getValidString("Faculty name: ");
        Faculty faculty;
        try {
          faculty = r.getFacultyByName(facultyName);
          if (faculty != null) {
            try {
              r.createDepartment(name, faculty);
            } catch (RemoteException e) {
              System.out.println("Remote Exception " + e);
              connectRMIInterface(a);
            }
          }
          else {
            System.out.println("There isn't a faculty with that name.");
          }
        }
        catch (RemoteException e) {
          System.out.println("Error getting faculty by name " + e);
          connectRMIInterface(a);
        }
      }

      else if (option2 == 2) {
        Department department = null;
        try {
          department = r.getDepartmentByName(name);
          if(department != null) {
            String newName = getValidString("New name: ");
            try {
              r.updateDepartmentName(department, newName);
              r.updateFacultyDepartmentName(department, newName);
            }

            catch (RemoteException e) {
              System.out.println("Remote Exception in updating Department, " + e);
              connectRMIInterface(a);
            }
          }
          else {
            System.out.println("There isn't a department with that name");
          }
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception in updating Department, " + e);
          connectRMIInterface(a);
        }
      }

      else if (option2 == 3) {
        Department department = null;
        try {
          department = r.getDepartmentByName(name);
          if (department != null) {
            r.removeDepartment(department);
          }
          else {
            System.out.println("There isnt a department with that name.");
          }
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception in removing Department, " + e);
          connectRMIInterface(a);
        }
      }
    }
  }

  public static void createElection(RMIInterface r, Admin a) {
    String name = getValidString("Name: ");
    String description = getValidString("Description: ");
    System.out.println("Start date:");
    long startDate = createDate();
    System.out.println("End date:");
    long endDate = createDate();
    if (endDate < startDate) {
      System.out.println("You can't end an election before it started. Error");
      return;
    }
    int type = getValidInteger("What type of election do you want to create?\n" +
            "1 - Nucleo de Estudantes\n" +
            "2 - Conselho Geral\n" +
            "3 back", 1, 2);
    try {
      if (type == 2) {
        r.createElection(name, description, startDate, endDate, type);
        System.out.println("Election successfully created");
      }
      else {
        String departmentName = getValidString("Department: ");
        if (r.createStudentsElection(name, description, startDate, endDate, type, departmentName)) {
          System.out.println("Election successfully created");
        }
        else {
          System.out.println("Error: there isn't a department with that name. Election wasn't created.");
        }
      }

    }
    catch(RemoteException e) {
      System.out.println("Remote Exception creating election");
      connectRMIInterface(a);
    }
  }

  public static void createCandidateList(RMIInterface r, Admin a) {
    String name = getValidString("Name of Candidate List: ");

    String electionName = getValidString("Name of Election List: ");
    Election election;
    try {
      election = r.getElectionByName(electionName);
      if (election == null) {
        System.out.println("There isn't an election with that name.");
        return;
      }
    }
    catch(RemoteException e) {
      System.out.println("Remote Exception creating candidate List");
      connectRMIInterface(a);
      return;
    }

    int electionType = election.getType();
    int usersType = 0;

    if (electionType == 2) {
      usersType = getValidInteger("Candidate List for:\n" +
              "1 - Students\n" +
              "2 - Teachers\n" +
              "3 - Staff\n", 1, 3);
    }

    ArrayList<User> users = new ArrayList<>();
    while(true) {
      String candidateName = getValidString("Name of User from Candidate List (STOP to stop): ");
      if (candidateName.equals("STOP")) {
        break;
      }
      try {
        User user = r.getUserByName(candidateName);
        if (user != null) {

          // Nucleo de Estudantes
          if (electionType == 1) {
            if (user.getType() != 1) {
              System.out.println("User has to be a student");
            }
            else {
              if (!election.getDepartment().getName().equals(user.getDepartment().getName())) {
                System.out.println("User has to be in department " + election.getDepartment().getName());
              }
              else {
                users.add(user);
              }
            }
          }

          // Conselho Geral
          else {
            if (usersType == user.getType())
              users.add(user);
            else
              System.out.println("User has to be type " + usersType +  ". This user is type " + user.getType());
          }
        }
        else {
          System.out.println("There isn't a user with that name");
        }
      }
      catch(RemoteException e) {
        System.out.println("Remote Exception creating Candidate List");
        connectRMIInterface(a);
      }
    }
    try {
      if (electionType == 1)
        r.createCandidateList(name, users, election);
      else
        r.createCandidateListCouncil(name, users, election, usersType);
      System.out.println("Candidate list successfully created.");
    }
    catch(RemoteException e) {
      System.out.println("Remote Exception creating candidate List");
      connectRMIInterface(a);
    }
  }

  public static void createVotingTable(RMIInterface r, Admin a) {
    String election = getValidString("Election name: ");
    String department = getValidString("Department name: ");
    try {
      int success = r.createVotingTable(election, department);
      if (success == 1) {
        System.out.println("Successfully created new Voting Table");
      }
      else if(success == 2) {
        System.out.println("Error creating Voting Table. There isn't an Election with that name.");
      }
      else {
        System.out.println("Error creating Voting Table. There isn't a Department with that name.");
      }
    }
    catch(RemoteException e) {
      System.out.println("Remote Exception creating voting Table");
      connectRMIInterface(a);
    }
  }

  public static void updatePort(Admin a) {
    if (a.getPort() == 7000) a.setPort(8000);
    else a.setPort(7000);
  }

  public static void connectRMIInterface(Admin a) {
    System.out.println("Trying to connect to port " + a.port);
    try {
      RMIInterface r = (RMIInterface) LocateRegistry.getRegistry(a.port).lookup("ivotas");
      r.remote_print("New client");
      System.out.println("Successfully connected to port " + a.port);
      menu(r, a);
    } catch (Exception e) {
      System.out.println("Failed to connect to port " + a.port);
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException es) {
        System.out.println("Error sleep: " + es.getMessage());
      }
      updatePort(a);
      connectRMIInterface(a);
    }
  }

  public static int getValidInteger(String field, int minimum, int maximum) {
    System.out.println(field);
    Scanner sc = new Scanner(System.in);
    while (true) {
      while (!sc.hasNextInt()) {
        System.out.println("Please write an integer");
        sc.next();
      }
      int option = sc.nextInt();
      if (maximum < option || option <= 0) {
        System.out.println("Please write an integer between " + minimum + " and " + maximum);
      }
      else {
        return option;
      }
    }
  }

  public static long createDate() {
    int day = getValidInteger("Day: ", 1,31);
    int month = getValidInteger("Month: ", 1, 12);
    int year = getValidInteger("Year: ", 2017, 2020);
    int hour = getValidInteger("Hour: ", 0,23);
    int minute = getValidInteger("Minute: ", 0,31);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

    long date = 0;
    try {
      date = simpleDateFormat.parse(day + "/" + month + "/" + year + " " +
              hour + ":" + minute + ":00").getTime();
      System.out.println(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static String getValidString(String field) {
    Scanner sc = new Scanner(System.in);
    System.out.println(field);
    String res = sc.next();
    return res;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}