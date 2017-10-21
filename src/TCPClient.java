import Data.Department;
import Data.Election;
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

      // read from keyboard and write to the server
      VotingTerminalMenu votingTerminalMenu = new VotingTerminalMenu("votingTerminalMenu", socket);

      // the main thread loops reading from the server and writing to System.out
      String messageFromServer;
      while((messageFromServer = inFromServer.readLine()) != null) {
        // set current username to log in
        synchronized (votingTerminalMenu.getT()) {
          votingTerminalMenu.setCurrentUsername(messageFromServer);
          System.out.println(messageFromServer);
          System.out.println(votingTerminalMenu.getCurrentUsername());
          votingTerminalMenu.getT().notify();
        }

        // check if login was sucessful
        System.out.println("waiting");
        messageFromServer = inFromServer.readLine();
        System.out.println("Resumed");

        synchronized (votingTerminalMenu.getT()) {
          Map<String, String> protocolValues = client.parseProcolMessage(messageFromServer);
          votingTerminalMenu.setLoginState(Boolean.parseBoolean(protocolValues.get("logged")));
          votingTerminalMenu.getT().notify();
        }

        // get election info
        System.out.println("waiting");
        messageFromServer = inFromServer.readLine();
        System.out.println(messageFromServer);
        System.out.println("Resumed");
      }
    } catch (IOException e) {
      if(inFromServer == null)
        System.out.println("\nUsage: java TCPClient <host> <port>\n");
        System.out.println(e.getMessage());
    } finally {
      try {
        inFromServer.close();
      } catch (Exception e) {}
    }
  }

  private Map<String, String> parseProcolMessage(String protocolMessage) {
    // The protocol message is going to be parsed into key-value map
    Map<String, String> protocolValues = new LinkedHashMap<>();

    // Clean protocol message of whitespaces
    String cleanProtocolMessage = protocolMessage.replaceAll("\\s+", "");
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
      userMap.put(userAttributes[i], userAttributes[i+1]);
    }

    // build user
    User user = new User(
            userMap.get("name"),
            userMap.get("password"),
            new Department(userMap.get("department")),
            new Faculty(userMap.get("faculty")),
            userMap.get("contact"),
            userMap.get("address"),
            userMap.get("cc"),
            userMap.get("expireDate"),
            Integer.parseInt(userMap.get("type"))
    );

    return user;
  }


}

// Thread responsible for reading from keyboard and writing to the server
class VotingTerminalMenu implements Runnable {
  private String currentUsername;
  private boolean loginState;
  private Election election;
  private String threadName;
  private PrintWriter outToServer;
  private Socket clientSocket;
  private Thread t;

  public VotingTerminalMenu(String threadName, Socket clientSocket) {
    this.currentUsername = null;
    this.loginState = false;
    this.election = null;
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
      try {
        synchronized (this.getT()) {
            this.getT().wait();

            // Ask or password and send it to the server
            String messageToServer = this.menu();
            this.outToServer.println(messageToServer);
            this.getT().wait();

            // Check login state
            boolean loginState = this.loginState;
            if (loginState) {

            } else {

            }

            // clean vars received
            this.setLoginState(false);
            this.setCurrentUsername(null);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private String menu() {
    String messageToServer = "type | login ; username | "  + this.currentUsername + " ; password | ";
    String password;
    Scanner sc = new Scanner(System.in);

    System.out.println("Welcome " + this.currentUsername);
    System.out.println("----------Login----------\n" +
            "Password: ");
    password = sc.nextLine();
    messageToServer += password + " ; ";

    return messageToServer;
  }

  public Thread getT() { return t; }

  public String getCurrentUsername() { return currentUsername; }
  public void setCurrentUsername(String currentUsername) { this.currentUsername = currentUsername; }

  public boolean isLoginState() { return loginState; }
  public void setLoginState(boolean loginState) { this.loginState = loginState; }
}
