import Data.Department;
import Data.Faculty;
import Data.User;

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
    TCPClient client = new TCPClient();

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
      Reader reader = new Reader("reader", socket);

      // the main thread loops reading from the server and writing to System.out
      String messageFromServer;
      while((messageFromServer = inFromServer.readLine()) != null) {
        Map<String, String> response = client.parseProtocolMessage(messageFromServer);
        User user = client.buildUser(response.get("user"));

        Object lock = new Object();
        synchronized (lock) {
          lock.notify();
        }
      }
    } catch (IOException e) {
      if(inFromServer == null)
        System.out.println("\nUsage: java TCPClient <host> <port>\n");
      System.out.println(e.getMessage());
    } finally {
      try { inFromServer.close(); } catch (Exception e) {}
    }
  }

  private Map<String, String> parseProtocolMessage(String protocolMessage) {
    // The protocol message is going to be parsed into key-value map
    Map<String, String> protocolValues = new LinkedHashMap<>();

    // Clean protocol message of whitespaces
    String cleanProtocolMessage = protocolMessage.replaceAll("\\s+", "");
    // Clean user toString garbage
    cleanProtocolMessage = cleanProtocolMessage.replace("User{", "");
    cleanProtocolMessage = cleanProtocolMessage.replace("}", "");

    // Split by | and ;
    String [] cleanProtocolWords = cleanProtocolMessage.split("\\||[;]");

    for (int i = 0; i < cleanProtocolWords.length; i+=2) {
      protocolValues.put(cleanProtocolWords[i], cleanProtocolWords[i+1]);
    }

    return protocolValues;
  }

  private User buildUser(String userString) {
    String [] userAttributes = userString.split("=|[,]");
    Map<String, String> userMap = new HashMap<>();

    // attributes in a map
    for (int i = 0; i < userAttributes.length; i+=2) {
      System.out.println(userAttributes[i]);
      System.out.println(userAttributes[i+1]);
      userMap.put(userAttributes[i], userAttributes[i+1]);
    }

    // build user
    User user = new User(
            userMap.get("name"),
            userMap.get("password"),
            new Department(userMap.get("department")),
            new Faculty(userMap.get("faculty"), null),
            userMap.get("contact"),
            userMap.get("address"),
            userMap.get("cc"),
            userMap.get("expireDate"),
            Integer.parseInt(userMap.get("type"))
    );


    return user;
  }

}

class Reader implements Runnable {
  private String threadName;
  private PrintWriter outToServer;
  private Socket clientSocket;
  private Thread t;

  public Reader(String threadName, Socket clientSocket) {
    this.threadName = threadName;

    try {
      this.clientSocket = clientSocket;
      this.outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.t = new Thread(this, threadName);
    this.t.start();
  }

  public void run() {
    while(!this.clientSocket.isClosed()) {
      String searchString = this.votingTableMenu();
      outToServer.println(searchString);

      System.out.println("waiting");
      Object lock = new Object();
      try {
        synchronized (lock) {
          lock.wait();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private int getValidInteger(int maximum) {
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

  private String votingTableMenu() {
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

  public Thread getT() { return t; }
}
