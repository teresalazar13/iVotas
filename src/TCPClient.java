import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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
        synchronized (votingTerminalMenu.getT()) {
          System.out.println("Aqui");
          // set current username to log in
          votingTerminalMenu.setCurrentUsername(messageFromServer);
          System.out.println(messageFromServer);
          System.out.println(votingTerminalMenu.getCurrentUsername());
          votingTerminalMenu.getT().notify();

          /*
          // check if login was sucessful
          messageFromServer = inFromServer.readLine();
          Map<String, String> protocolValues = client.parseProcolMessage(messageFromServer);
          votingTerminalMenu.setLoginState(Boolean.parseBoolean(protocolValues.get("logged")));
          votingTerminalMenu.getT().notify();*/
        }
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
}

// Thread responsible for reading from keyboard and writing to the server
class VotingTerminalMenu implements Runnable {
  private String currentUsername;
  private boolean loginState;
  private String threadName;
  private PrintWriter outToServer;
  private Socket clientSocket;
  private Thread t;

  public VotingTerminalMenu(String threadName, Socket clientSocket) {
    this.currentUsername = null;
    this.loginState = false;
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
      synchronized (this.getT()) {
        try {
          System.out.println("waiting");
          this.getT().wait();
          System.out.println("resumed");

          // Ask or password and send it to the server
          String messageToServer = this.menu();
          this.outToServer.println(messageToServer);
          /*
          this.getT().wait();

          // Check login state
          Boolean loginState = this.loginState;
          System.out.println(loginState);

          this.setLoginState(false);
          */
          this.setCurrentUsername(null);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
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
