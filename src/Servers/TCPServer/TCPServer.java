package Servers.TCPServer;

import Data.*;
import Servers.RMIServer.FileWrapper;
import Servers.RMIServer.RMIInterface;

import java.net.*;
import java.io.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TCPServer {
  private Election election;
  private CopyOnWriteArrayList <Connection> votingTerminals;
  private String location;

  public TCPServer(Election election, CopyOnWriteArrayList<Connection> votingTerminals, String location) {
    this.election = election;
    this.votingTerminals = votingTerminals;
    this.location = location;
  }

  public static void main(String args[]) {
    FileWrapper fw;
    Election election = null;
    TCPServer votingTable = null;

    try {
      fw = new FileWrapper();
      election = fw.getElections().get(0);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    int number = 0;
    ArrayList<String> votingTableMenuMessages = new ArrayList<>();
    CopyOnWriteArrayList <Connection> threads = new CopyOnWriteArrayList<>();
    votingTable = new TCPServer(election, threads, "DEI");

    try {
      int serverPort = 6000;
      System.out.println("Listening on port 6000");

      ServerSocket listenSocket = new ServerSocket(serverPort);
      System.out.println("LISTEN SOCKET = " + listenSocket);

      Menu menu = new Menu(votingTableMenuMessages, threads);

      // thread to accept new client connections
      while(true) {
        Socket clientSocket = listenSocket.accept();
        System.out.println("CLIENT_SOCKET (created at accept()) = " + clientSocket);
        number++;
        Connection thread = new Connection(clientSocket, number, votingTable.election, votingTableMenuMessages, threads);
        votingTable.votingTerminals.add(thread);
      }
    } catch(IOException e) {
      System.out.println("Listen: " + e.getMessage());
    }
  }
}

// Thread to handle comm with client
class Connection extends Thread {
  private int thread_number;
  private Election election;
  private BufferedReader bufferedReader;
  private PrintWriter outToServer;
  private ArrayList<String> votingTableMenuMessages;
  private CopyOnWriteArrayList<Connection> threads;

  public Connection (Socket aClientSocket, int number, Election election, ArrayList<String> votingTableMessages, CopyOnWriteArrayList<Connection> threads) {
    this.thread_number = number;
    this.election = election;
    this.threads = threads;
    this.votingTableMenuMessages = votingTableMessages;

    try {
      Socket clientSocket = aClientSocket;
      this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      this.outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
      this.start();
    } catch(IOException e){
      System.out.println("Connection: " + e.getMessage());
    }
  }

  public void run(){
    try {
      RMIInterface rmi = (RMIInterface) Naming.lookup("ivotas");

      while (true) {
        synchronized (this) {
          this.wait();

          // Identify user at table
          String message = votingTableMenuMessages.get(0);
          votingTableMenuMessages.remove(0);
          this.getOut().println(message);

          // Read client login message
          String clientResponse = bufferedReader.readLine();
          Map<String, String> keyValues = parseProtocolMessage(clientResponse);

          // Check if login is valid
          boolean validLogin = rmi.authenticateUser(keyValues.get("username"), keyValues.get("password"));
          while(!validLogin) {
            message = "type | status ; logged | " + validLogin ;
            this.getOut().println(message);
            System.out.println(message);
            clientResponse = bufferedReader.readLine();
            keyValues = parseProtocolMessage(clientResponse);
            validLogin = rmi.authenticateUser(keyValues.get("username"), keyValues.get("password"));
          }

          message = "type | status ; logged | " + validLogin ;
          this.getOut().println(message);

          message = "type | voting ; election | " + this.election.toStringClient();
          this.getOut().println(message);
          clientResponse = bufferedReader.readLine();
          System.out.println(clientResponse);
          keyValues = parseProtocolMessage(clientResponse);

          User user = rmi.getUserByName(keyValues.get("username"));
          Election voteElection = rmi.getElectionByName(keyValues.get("election"));
          CandidateList voteList = rmi.getCandidateListByName(keyValues.get("choice"));

          // TODO -> change null to object Department
          rmi.vote(user, voteElection, voteList, null);
        }
      }
    } catch (InterruptedException | IOException | NotBoundException e) {
      e.printStackTrace();
    }
  }

  private Map<String, String> parseProtocolMessage(String protocolMessage) {
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

  private String identifyAction(RMIInterface rmi, String key, String field, String value) {
    // response will always return the status of the pretended action
    String response = "type | status ; ";

    if ("search".equals(key)) {
      response += "search | ";

      try {
        User user = rmi.searchUser(field, value);
        if (user != null) {
          response += "success ; user | " + user.getName() + " ; ";
        } else {
          response += "failure ; ";
        }
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    return response;
  }

  public PrintWriter getOut() {
    return outToServer;
  }
}

// Thread to accept input and send it to rmi
class Menu extends Thread {
  private ArrayList<String> votingTableMenuMessages;
  private CopyOnWriteArrayList<Connection> votingTerminals;

  public Menu (ArrayList<String> votingTableMenuMessages, CopyOnWriteArrayList<Connection> votingTerminals) {
    this.votingTableMenuMessages = votingTableMenuMessages;
    this.votingTerminals = votingTerminals;
    this.start();
  }

  public void run() {
    try {
      RMIInterface rmi = (RMIInterface) Naming.lookup("ivotas");

      while (true) {
        ArrayList<String> search = this.votingTableMenu();
        User user = rmi.searchUser(search.get(0), search.get(1));;

        // Check if user was found
        if (user != null) {
          int lockedTerminalIndex = this.getLockedTerminal();

          // send user to voting terminal
          synchronized (this.votingTerminals.get(lockedTerminalIndex)) {
            System.out.println(this.votingTerminals.size());
            this.votingTableMenuMessages.add(user.getName());
            this.votingTerminals.get(this.getLockedTerminal()).notify();
          }

        } else {
          System.out.println("User not found");
        }
      }

    } catch (NotBoundException | MalformedURLException | RemoteException e) {
      e.printStackTrace();
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
  private ArrayList<String> votingTableMenu() {
    Scanner sc = new Scanner(System.in);
    ArrayList<String> search = new ArrayList<>();

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
          search.add("name");
          System.out.println("Name:");
          break;
        case 2:
          search.add("department");
          System.out.println("Department:");
          break;
        case 3:
          search.add("faculty");
          System.out.println("Faculty:");
          break;
        case 4:
          search.add("contact");
          System.out.println("Contact:");
          break;
        case 5:
          search.add("address");
          System.out.println("Address");
          break;
        case 6:
          search.add("cc");
          System.out.println("CC");
          break;
        case 7:
          search.add("expireDate");
          System.out.println("Expire Date ");
          break;
        case 8:
          return null;
        default:
          break;
      }

      search.add(sc.nextLine());
      return search;
    }
  }

  private int getLockedTerminal() {
    for (int i = 0; i < votingTerminals.size(); i++) {
      String currentState = votingTerminals.get(i).getState().name();

      if ("WAITING".equals(currentState)) {
        return i;
      }
    }

    return -1;
  }
}