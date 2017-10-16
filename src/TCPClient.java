import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class establishes a TCP connection to a specified server, and loops
 * sending/receiving strings to/from the server.
 * <p>
 * The main() method receives two arguments specifying the server address and
 * the listening port.
 * <p>
 * The usage is similar to the 'telnet <address> <port>' command found in most
 * operating systems, to the 'netcat <host> <port>' command found in Linux,
 * and to the 'nc <hostname> <port>' found in macOS.
 *
 * @author Raul Barbosa
 * @author Alcides Fonseca
 * @version 1.1
 */

class TCPClient {
  public static void main(String[] args) {
    Socket socket;
    PrintWriter outToServer;
    BufferedReader inFromServer = null;

    try {
      // connect to the specified address:port (default is localhost:12345)
      if(args.length == 2)
        socket = new Socket(args[0], Integer.parseInt(args[1]));
      else
        socket = new Socket("localhost", 12345);
        
      // create streams for writing to and reading from the socket
      inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      outToServer = new PrintWriter(socket.getOutputStream(), true);

      // read from keyboard and write to the server
      new Thread() {
        public void run() {
          while(!socket.isClosed()) {
            String searchString = votingTableMenu();
            System.out.println(searchString);
            outToServer.println(searchString);
          }
        }
      }.start();

      // the main thread loops reading from the server and writing to System.out
      String messageFromServer;
      while((messageFromServer = inFromServer.readLine()) != null)
        System.out.println(messageFromServer);
    } catch (IOException e) {
      if(inFromServer == null)
        System.out.println("\nUsage: java TCPClient <host> <port>\n");
      System.out.println(e.getMessage());
    } finally {
      try { inFromServer.close(); } catch (Exception e) {}
    }
  }

  private static int getValidInteger(int maximum) {
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

  public static String votingTableMenu() {
    Scanner sc = new Scanner(System.in);
    String toSearch;
    String tcpString = "type | search ; ";

    while(true) {
      System.out.println("Identify user by:\n" +
              "1 - Name\n" +
              "2 - Department\n" +
              "3 - Faculty \n" +
              "4 - Contact\n" +
              "5 - Address\n" +
              "6 - cc\n" +
              "7 - expireDate\n" +
              "8 to quit");

      int option = getValidInteger(9);
      switch (option) {
        case 1:
          tcpString += "name | ";
          System.out.println("Name: ");
          break;
        case 2:
          tcpString += "department | ";
          System.out.println("Department: ");
          break;
        case 3:
          tcpString += "faculty | ";
          System.out.println("Faculty: ");
          break;
        case 4:
          tcpString += "contact | ";
          System.out.println("Contact: ");
          break;
        case 5:
          tcpString += "address| ";
          System.out.println("Address: ");
          break;
        case 6:
          tcpString += "cc | ";
          System.out.println("CC: ");
          break;
        case 7:
          tcpString += "expireDate | ";
          System.out.println("Expire Date: ");
          break;
        case 8:
          return "Terminate";
        default:
          break;
      }

      toSearch = sc.nextLine();
      tcpString += toSearch + " ; ";

      return tcpString;
    }
  }
}
