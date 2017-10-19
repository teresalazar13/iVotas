import Data.*;
import Servers.RMIServer.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Admin {

  // ASK - Update faculty é so mudar o nome? ou mudar departamentos tambem?
  // TODO - Funcoes synchronized
  // TODO - Terminal
  // TODO - votar nao pode ser perdido com excecao -> votar mais que uma vez nao
  // TODO - Mudar portos fixos

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
      System.out.println("Please choose an option:\n" +
              "1 - Register Person\n" +
              "2 - Manage Department or Faculty\n" +
              "3 - Create Election\n" +
              "4 - Manage Candidate List of an Election\n" +
              "5 - Manage Voting Table\n" +
              "6 - Change Election's properties\n" +
              "7 - Know where a User has voted\n" +
              "8 - See details of past elections\n" +
              "9 to quit");
      int option = getValidInteger(9);
      switch (option) {
        case 1:
          createUser(r, a);
          break;
        case 2:
          management(r, a);
          break;
        case 3:
          try {
            r.remote_print("XXXXXXXX");
          } catch (Exception e) {
            System.out.println("Fail on Server");
            connectRMIInterface(a);
          }
          break;
        case 4:
          break;
        case 5:
          break;
        case 6:
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
    System.out.println("What type of User do you want to Create?\n" +
            "1 - Student\n" +
            "2 - Teacher\n" +
            "3 - Staff\n" +
            "4 back");
    int option = getValidInteger(4);
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
    System.out.println("What do you want to manage?\n" +
            "1 - Faculty\n" +
            "2 - Department\n" +
            "3 back");
    int option = getValidInteger(3);
    if (option == 3) return;
    System.out.println("What do you do?\n" +
            "1 - Create\n" +
            "2 - Update\n" +
            "3 - Remove\n" +
            "4 back");
    int option2 = getValidInteger(4);
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
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception in updating Faculty, " + e);
          connectRMIInterface(a);
        }
        String newName = getValidString("New name: ");
        try {
          r.updateFacultyName(faculty, newName);
        }
        catch (RemoteException e) {
          System.out.println("Remote Exception in updating Faculty, " + e);
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

  public static int getValidInteger(int maximum) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Option: ");
    while (true) {
      while (!sc.hasNextInt()) {
        System.out.println("Please write an integer");
        sc.next();
      }
      int option = sc.nextInt();
      if (maximum < option || option <= 0) {
        System.out.println("Please write an integer between 1 and " + maximum);
      }
      else {
        return option;
      }
    }
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