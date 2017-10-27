package Admin;

import Data.*;
import Servers.RMIServer.AdminInterface;
import Servers.RMIServer.RMIInterface;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Admin extends UnicastRemoteObject implements AdminInterface, Serializable {
  private int port;
  private int mainPort;
  private int backupPort;
  private boolean notify;

  public Admin(int port, int mainPort, int backupPort) throws RemoteException {
    this.port = port;
    this.mainPort = mainPort;
    this.backupPort = backupPort;
    this.notify = false;
  }

  public static void main(String args[]) {

    // System.getProperties().put("java.security.policy", "policy.all");
    // System.setSecurityManager(new RMISecurityManager());

    if(args.length != 2) {
      System.out.println("java Admin 1 port backupPort");
      System.exit(0);
    }

    int mainPort = Integer.parseInt(args[0]);
    int backupPort = Integer.parseInt(args[1]);
    try {
      Admin a = new Admin(mainPort, mainPort, backupPort);
      connectRMIInterface(a);
    }
    catch (RemoteException e) {
      System.out.println("Error creating admin.");
    }
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
              "9 - Print Data\n" +
              "10 - Print Notifications\n" +
              "11 to quit", 1, 11);
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
          changeElectionsProperties(r, a);
          break;
        case 7:
          knowWhereUserVoted(r, a);
          break;
        case 8:
          pastElections(r, a);
          break;
        case 9:
          printData(r, a);
          break;
        case 10:
          printNotifications(r, a);
          break;
        case 11:
          System.out.println("Bye!");
          System.exit(0);
        default:
          return;
      }
    }
  }

  public static void createUser(RMIInterface r, Admin a) {
    int type = getValidInteger(
            "What type of User do you want to Create?\n" +
                    "1 - Student\n" +
                    "2 - Teacher\n" +
                    "3 - Staff\n" +
                    "4 back", 1,4);
    if (type == 4) return;

    String name = getValidString("Name: ");
    System.out.println("Password: ");
    Scanner sc = new Scanner(System.in);
    String password = sc.nextLine();
    String departmentName = getValidString("Department: ");
    String facultyName = getValidString("Faculty: ");
    String contact = Integer.toString(getValidInteger("Contact: ", 1, 999999999));
    String address = getValidString("Address: ");
    String cc = getValidString("CC: ");
    String expireDate = getValidExpireDate("Expire date: ");

    try {
      int success = r.createUser(name, password, departmentName, facultyName, contact, address, cc, expireDate, type);
      if (success == 1)
        System.out.println("User successfully created");
      else if (success == 2)
        System.out.println("There isn't a department with the name " + departmentName);
      else
        System.out.println("There isn't a faculty with the name " + facultyName);
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
          System.out.println("Faculty successfully created.");
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
        try {
          boolean success = r.createDepartment(name, facultyName);
          if (success)
            System.out.println("Deparment successfully created.");
          else
            System.out.println("Error. There isn't a faculty with the name " + facultyName);
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception creating Department " + e);
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
        int success = r.createElection(name, description, startDate, endDate, type);
        if (success == 1)
          System.out.println("Election successfully created");
        else
          System.out.println("Error creating election. You can't create an election in the past");
      }
      else {
        String departmentName = getValidString("Department: ");
        int success = r.createStudentsElection(name, description, startDate, endDate, type, departmentName);
        if (success == 1)
          System.out.println("Election successfully created");
        else if (success == 2)
          System.out.println("Error: there isn't a department with that name. Election wasn't created.");
        else
          System.out.println("Error creating election. You can't create an election in the past");
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
      System.out.println("Remote Exception creating candidate List.");
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
      if (users.size() == 0) {
        System.out.println("Error. You can't create a candidate list without any users.");
        return;
      }
      if (electionType == 1)
        r.createCandidateList(name, users, election);
      else
        r.createCandidateListCouncil(name, users, election, usersType);
      System.out.println("Candidate list successfully created.");
    }
    catch(RemoteException e) {
      System.out.println("Remote Exception creating candidate List.");
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

  public static void changeElectionsProperties(RMIInterface r, Admin a) {
    String electionName = getValidString("Name of election to change: ");
    int option = getValidInteger("What do you want to change: \n" +
            "1 - name\n" +
            "2 - description\n" +
            "3 - start date\n" +
            "4 - end date\n" +
            "5 - back", 1, 5);
    Object object = null;
    switch (option) {
      case 1:
        object = getValidString("New name: ");
        break;
      case 2:
        object = getValidString("New description: ");
        break;
      case 3:
        object = createDate();
        break;
      case 4:
        object = createDate();
      case 5:
        break;
    }
    if (option != 5) {
      try {
        int success = r.updateElection(electionName, object, option);
        if(success == 1) {
          System.out.println("Election successfully updated");
        }
        else if (success == 2)
          System.out.println("Error updating election. There isn't an election with that name.");
        else if(success == 3)
          System.out.println("Error updating election. You cant end an election before it started.");
        else
          System.out.println("Error updating election. You cant update election because it has already started or even ended.");
      }
      catch(RemoteException e) {
        System.out.println("Remote Exception updating Election");
        connectRMIInterface(a);
      }
    }
  }

  public static void knowWhereUserVoted(RMIInterface r, Admin a) {
    String userName = getValidString("Username: ");
    String electionName = getValidString("Name of election: ");
    try {
      System.out.println(r.knowWhereUserVoted(userName, electionName));
      connectRMIInterface(a);
    }
    catch(RemoteException e) {
      System.out.println("Remote exception knowing where user has voted.");
    }
  }

  public static void pastElections(RMIInterface r, Admin a) {
    try {
      System.out.println("Details of all past Elections");
      System.out.println(r.detailsOfPastElections());
    }
    catch(RemoteException e) {
      System.out.println("Remote exception getting details of past elections.");
      connectRMIInterface(a);
    }
  }

  public static void printNotifications(RMIInterface r, Admin a) {
    try {
      r.subscribe("localhost", a);
      System.out.println("Client sent subscription to server");
      a.setNotify(true);
      while(true) {
        try {
          try {
            r.remote_print("testing");
          }
          catch (RemoteException e) {
            connectRMIInterface(a);
          }
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          System.out.println("Error sleeping");
        }
      }
    }
    catch(RemoteException e) {
      System.out.println("Error subscribing.");
      connectRMIInterface(a);
    }
  }

  public void print_on_client(String s) throws RemoteException {
    if (this.notify) {
      System.out.println("> " + s);
    }
  }

  public static void updatePort(Admin a) {
    if (a.getPort() == a.mainPort) a.setPort(a.backupPort);
    else a.setPort(a.mainPort);
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

  public static void printData(RMIInterface r, Admin a) {
    int option = getValidInteger("What do you want to print?\n" +
            "1 - Users\n" +
            "2 - Faculties\n" +
            "3 - Departments\n" +
            "4 - Elections\n" +
            "5 - Candidate Lists\n" +
            "6 - Voting Tables\n" +
            "7 - Back\n", 1, 6);
    if (option == 7) return;
    try {
      System.out.println(r.prettyPrint(option));
    }
    catch (RemoteException e) {
      System.out.println("Error printing data.");
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
      if (maximum < option || option < minimum) {
        System.out.println("Please write an integer between " + minimum + " and " + maximum);
      }
      else {
        return option;
      }
    }
  }

  public static String getValidString(String field) {
    Scanner sc = new Scanner(System.in);
    System.out.println(field);
    while(true) {
      String res = sc.nextLine();
      if (res.matches("^[a-zA-Z\\s]*$"))
        return res;
      else
        System.out.println("Please write only letters and spaces");
    }
  }

  public static String getValidExpireDate(String field) {
    Scanner sc = new Scanner(System.in);
    System.out.println(field);
    while(true) {
      String res = sc.nextLine();
      if (res.matches("(?:0[1-9]|1[0-2])/[0-9]{2}"))
        return res;
      else
        System.out.println("Please write expire date in MM/YY format");
    }
  }

  public static long createDate() {
    int day = getValidInteger("Day: ", 1,31);
    int month = getValidInteger("Month: ", 1, 12);
    int year = getValidInteger("Year: ", 2017, 2020);
    int hour = getValidInteger("Hour: ", 0,23);
    int minute = getValidInteger("Minute: ", 0,59);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

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

  public void setNotify(boolean notify) {
    this.notify = notify;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}