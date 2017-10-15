import Servers.RMIServer.*;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class Admin {

  public static void main(String args[]) {

    //System.getProperties().put("java.security.policy", "policy.all");
    //System.setSecurityManager(new RMISecurityManager());

    try {
      RMIInterface r = (RMIInterface) LocateRegistry.getRegistry(7000).lookup("ivotas");
      // r.remote_print("print do client para o servidor...");
      menu();
    } catch (Exception e) {
      System.out.println("Exception in main: " + e);
    }
  }

  public static void menu() {
    while(true) {
      System.out.println("Please choose an option:\n" +
              "1 - Register Person\n" +
              "2 - Manage Departament or Faculty\n" +
              "3 - Create Election \n" +
              "4 - Manage Candidate List of an Election\n" +
              "5 - Manage Voting Table\n" +
              "6 - Change Election's properties\n" +
              "7 - Know where a User has voted\n" +
              "8 - See details of past elections\n" +
              "9 to quit");
      int option = getValidInteger(9);
      switch (option) {
        case 1:
          break;
        case 2:
          break;
        case 3:
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

}