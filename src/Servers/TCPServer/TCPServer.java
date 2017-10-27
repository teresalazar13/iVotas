package Servers.TCPServer;

import Data.*;
import Servers.RMIServer.RMIInterface;

import java.net.*;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TCPServer {
  private int port;
  private int mainRmiPort;
  private int backupRmiPort;
  private VotingTable votingTable;
  private CopyOnWriteArrayList <Connection> votingTerminals;
  private boolean status;

  public TCPServer(int port, int mainRmiPort, int backupRmiPort, VotingTable votingTable, CopyOnWriteArrayList<Connection> votingTerminals, boolean status) {
    this.port = port;
    this.mainRmiPort = mainRmiPort;
    this.backupRmiPort = backupRmiPort;
    this.votingTable = votingTable;
    this.votingTerminals = votingTerminals;
    this.status = status;
  }

  // arg[0] -> server port
  // arg[1] -> main rmi port
  // arg[2] -> backup rmi
  // arg[3] -> voting table
  // arg[4] ->ip na rede
  public static void main(String args[]) {
    System.setProperty("java.rmi.server.hostname", "192.168.1.78");
    VotingTable votingTable = null;
    TCPServer tableServer = null;
    RMIInterface rmi = null;

    ArrayList<String> votingTableMenuMessages = new ArrayList<>();
    CopyOnWriteArrayList <Connection> threads = new CopyOnWriteArrayList<>();
    tableServer = new TCPServer(Integer.parseInt(args[1]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), null, threads, true);

    rmi = tableServer.connectRMIInterface(args[4]);

    // get voting table by id
    // TODO-> deal with method rmi, the server could go down??
    try {
      votingTable = rmi.getVotingTableById(Integer.parseInt(args[3]));
      tableServer.setVotingTable(votingTable);
      rmi.notifyAdmins("New voting table with id " + votingTable.getId() + " of election " + votingTable.getElection().getName());
    } catch (IOException | NullPointerException e) {
      System.out.println("Unable to retrieve voting table");
      return;
    }

    try {
      System.out.println("Listening on port" + args[2]);

      ServerSocket listenSocket = new ServerSocket(Integer.parseInt(args[0]));
      System.out.println("LISTEN SOCKET = " + listenSocket);

      System.out.println(rmi);
      Menu menu = new Menu(votingTableMenuMessages, threads, rmi, tableServer, votingTable.getId(), args[4]);

      // thread to accept new client connections
      while(true) {
        Socket clientSocket = listenSocket.accept();
        System.out.println("CLIENT_SOCKET (created at accept()) = " + clientSocket);
        Connection thread = new Connection(clientSocket, tableServer.votingTerminals.size(), args[4], tableServer, votingTableMenuMessages, threads, rmi);
        tableServer.votingTerminals.add(thread);
      }
    } catch(IOException e) {
      System.out.println("Listen: " + e.getMessage());
    }
  }

  private void updatePort(TCPServer server) {
    if (server.getPort() == server.getMainRmiPort()) server.setPort(server.getBackupRmiPort());
    else server.setPort(server.getMainRmiPort());
  }
  private RMIInterface connectRMIInterface(String ip) {
    RMIInterface rmi = null;
    boolean passed = false;

    while (true) {
      try {
        // primeiro arg é o ip
        rmi = (RMIInterface) LocateRegistry.getRegistry(ip, this.getPort()).lookup("ivotas");
        //r.addAdmin(a);
        rmi.remote_print("New client");
        System.out.println("Successfully connected to port " + this.getPort());
        passed = true;
      } catch (Exception e) {
        System.out.println("Failed to connect to port " + this.getPort());
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException es) {
          System.out.println("Error sleep: " + es.getMessage());
        }
        this.updatePort(this);
      }

      if (passed) {
        break;
      }
    }

    return rmi;
  }

  public boolean isStatus() { return status; }
  public void setStatus(boolean status) { this.status = status; }

  public VotingTable getVotingTable() { return votingTable; }
  public void setVotingTable(VotingTable votingTable) { this.votingTable = votingTable; }

  public int getMainRmiPort() { return mainRmiPort; }
  public void setMainRmiPort(int mainRmiPort) { this.mainRmiPort = mainRmiPort; }

  public int getBackupRmiPort() { return backupRmiPort; }
  public void setBackupRmiPort(int backupRmiPort) { this.backupRmiPort = backupRmiPort; }

  public int getPort() { return port; }
  public void setPort(int port) { this.port = port; }
}

// Thread to handle comm with client
class Connection extends Thread {
  private int thread_number;
  String ip;
  private Socket clientSocket;
  private BufferedReader bufferedReader;
  private PrintWriter outToServer;
  private ArrayList<String> votingTableMenuMessages;
  private CopyOnWriteArrayList<Connection> threads;
  private RMIInterface rmi;
  private TCPServer tableServer;

  public Connection(Socket aClientSocket, int number, String ip, TCPServer tableServer, ArrayList<String> votingTableMessages, CopyOnWriteArrayList<Connection> threads, RMIInterface rmi) {
    this.thread_number = number;
    this.ip = ip;
    this.tableServer = tableServer;
    this.threads = threads;
    this.votingTableMenuMessages = votingTableMessages;
    this.rmi = rmi;

    try {
      this.clientSocket = aClientSocket;
      this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      this.outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
      this.start();
    } catch (IOException e) {
      System.out.println("Connection: " + e.getMessage());
    }
  }

  public void run() {
      while (true) {
        try {
          // Wait for client
          synchronized (this) {
            this.wait();
          }

          // Vars
          String message;
          String clientResponse = null;
          Map<String, String> protocolValues;

          // Identify user at table
          synchronized (this) {
            message = votingTableMenuMessages.get(0);
            votingTableMenuMessages.remove(0);
            this.getOut().println(message);
          }

          synchronized (this) {
            // set timeout at 120s
            this.clientSocket.setSoTimeout(120000);
            clientResponse = bufferedReader.readLine();
          }
          protocolValues = parseProtocolMessage(clientResponse);

          // Check if login is valid
          boolean validLogin = loginIsValid(protocolValues);

          // Send sucess login message
          message = "type | status ; logged | " + validLogin;
          this.getOut().println(message);

          // Send election info to client
          message = "type | voting ; election | " + this.tableServer.getVotingTable().getElection().toStringClient();
          this.getOut().println(message);

          // Read user voting option
          clientResponse = bufferedReader.readLine();

          // Socket back to normal
          this.clientSocket.setSoTimeout(0);

          protocolValues = parseProtocolMessage(clientResponse);

          // Vote search fields
          User user = this.searchUserByName(protocolValues.get("username"));
          CandidateList voteList;

          // Check if vote is valid
          if ("blank".equals(protocolValues.get("choice"))) {
            message = this.voteIsValid(user, null, this.tableServer);
          } else {
            voteList = this.searchCandidateListByName(protocolValues.get("choice"));
            message = this.voteIsValid(user, voteList, this.tableServer);
          }

          this.getOut().println(message);
        } catch (InterruptedException e) {
          System.out.println("Exception in locking thread");
          this.close();
          break;
        } catch(SocketTimeoutException e) {
          this.getOut().println("type | timeout");
        } catch (Exception e) {
          System.out.println(e.getMessage());
          System.out.println("Client disconnected");
          this.close();
          break;
        }
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

  public PrintWriter getOut() {
    return outToServer;
  }

  private  void updateTerminalsIndexes() {
    int size = this.threads.size();

    for (int i = 0; i < this.threads.size(); i++) {
      this.threads.get(i).thread_number = i;
    }
  }

  private static void updatePort(TCPServer server) {
    if (server.getPort() == server.getMainRmiPort()) server.setPort(server.getBackupRmiPort());
    else server.setPort(server.getMainRmiPort());
  }

  private RMIInterface connectRMIInterface(TCPServer server) {
    System.out.println("Trying to connect to port " + server.getPort());

    RMIInterface rmi = null;
    boolean noExceptions = false;

    while (true) {
      try {
        rmi = (RMIInterface) LocateRegistry.getRegistry(this.ip, server.getPort()).lookup("ivotas");
        //r.addAdmin(a);
        rmi.remote_print("New client");
        System.out.println("Successfully connected to port " + server.getPort());
        noExceptions = true;
      } catch (Exception e) {
        System.out.println("Failed to connect to port " + server.getPort());
        try {
          TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException es) {
          System.out.println("Error sleep: " + es.getMessage());
        }
        updatePort(server);
      }

      if (noExceptions) {
        break;
      }
    }

    return rmi;
  }

  private boolean authUser(Map<String, String> protocolValues) {
    boolean validLogin = false;
    boolean noExceptions = false;

    while (true) {
      try {
        validLogin = this.rmi.authenticateUser(protocolValues.get("username"), protocolValues.get("password"));
        noExceptions = true;
      } catch (RemoteException | NullPointerException e) {
        System.out.println("Waiting...");
        this.rmi = this.connectRMIInterface(this.tableServer);
      }

      if (noExceptions) {
        break;
      }
    }

    return validLogin;
  }

  // Check if username and password are valid
  private boolean loginIsValid(Map<String, String> protocolValues) {
    String message, clientResponse;
    boolean validLogin = false;

    // While there's exceptions iterate, end if it's n IOexception
    try {
      validLogin = this.authUser(protocolValues);
      while (!validLogin) {
        message = "type | status ; logged | " + validLogin;
        this.getOut().println(message);

        clientResponse = this.bufferedReader.readLine();
        protocolValues = parseProtocolMessage(clientResponse);
        validLogin = this.authUser(protocolValues);
      }
    } catch (RemoteException e) {
      System.out.println("Waiting...");
      System.out.println(e.getMessage());
      this.connectRMIInterface(tableServer);
    } catch (IOException e) {
      this.close();
    }

    return validLogin;
  }

  private User searchUserByName(String username) {
    boolean noExceptions = false;
    User user = null;

    while (true) {
      try {
        user = this.rmi.getUserByName(username);
        noExceptions = true;
      } catch (RemoteException | NullPointerException e) {
        System.out.println("Waiting...");
        this.rmi = this.connectRMIInterface(this.tableServer);
      }

      if (noExceptions) {
        break;
      }
    }

    return user;
  }

  private CandidateList searchCandidateListByName(String name)
  {
    boolean noExceptions = false;
    CandidateList candidateList = null;

    while (true) {
      try {
        candidateList = this.rmi.getCandidateListByName(name);
        System.out.println(rmi);
        noExceptions = true;
      } catch (RemoteException | NullPointerException e) {
        System.out.println("Waiting...");
        this.rmi = this.connectRMIInterface(this.tableServer);
      }

      if (noExceptions) {
        break;
      }
    }

    return candidateList;
  }

  private String voteIsValid(User user, CandidateList voteList, TCPServer votingTable) {
    boolean noExceptions = false;
    String voteIsValid = "type | status ; vote | ";

    while (true) {

      try {
        if (rmi.voteIsValid(user, this.tableServer.getVotingTable(), voteList)) {
          voteIsValid += "success ;";
          this.rmi.vote(user, this.tableServer.getVotingTable().getElection(), voteList, this.tableServer.getVotingTable().getDepartment());
        } else {
          voteIsValid += "failed ;";
        }

        noExceptions = true;
      } catch (RemoteException | NullPointerException e) {
        System.out.println("Waiting...");
        this.rmi = this.connectRMIInterface(this.tableServer);
      }

      if (noExceptions) {
        break;
      }
    }

    return voteIsValid;
  }

  private void close() {
    try {
      this.bufferedReader.close();
      this.outToServer.close();
      this.threads.remove(this.thread_number);
      this.updateTerminalsIndexes();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}

// Thread to accept input and send it to rmi
class Menu extends Thread {
  private ArrayList<String> votingTableMenuMessages;
  private CopyOnWriteArrayList<Connection> votingTerminals;
  private RMIInterface rmi;
  private TCPServer tableServer;
  private int votingTableId;
  String ip;

  public Menu (ArrayList<String> votingTableMenuMessages, CopyOnWriteArrayList<Connection> votingTerminals, RMIInterface rmi, TCPServer tableServer, int votingTableId, String ip) {
    this.votingTableMenuMessages = votingTableMenuMessages;
    this.votingTerminals = votingTerminals;
    this.rmi = rmi;
    this.tableServer = tableServer;
    this.votingTableId = votingTableId;
    this.ip = ip;
    this.start();
  }

  public void run() {
    while (true) {
      ArrayList<String> search = this.votingTableMenu(votingTableId);
      User user = null;

      // search user by field in voting table
      user = this.searchUser(search);

      // Check if user was found
      if (user != null) {
        int lockedTerminalIndex = this.getLockedTerminal();

        if (lockedTerminalIndex != -1) {
          // send user to voting terminal
          synchronized (this.votingTerminals.get(lockedTerminalIndex)) {
            this.votingTableMenuMessages.add("type | search ; user | " + user.getName());
            this.votingTerminals.get(this.getLockedTerminal()).notify();
          }
        } else {
          System.out.println("All terminals are currently busy, return later");
        }
      } else {
        System.out.println("User not found");
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

  private ArrayList<String> votingTableMenu(int votingTableId) {
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
          try {
            rmi.notifyAdmins("Voting table with id " + votingTableId + " off");
          }
          catch (RemoteException e) {
            System.out.println("Remote exception notifying admins of voting table off.");
          }
          System.exit(0);
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

  private static void updatePort(TCPServer server) {
    if (server.getPort() == server.getMainRmiPort()) server.setPort(server.getBackupRmiPort());
    else server.setPort(server.getMainRmiPort());
  }

  private RMIInterface connectRMIInterface(TCPServer server) {
    System.out.println("Trying to connect to port " + server.getPort());

    RMIInterface rmi = null;
    boolean noExceptions = false;

    while (true) {
      try {
        rmi = (RMIInterface) LocateRegistry.getRegistry(this.ip, server.getPort()).lookup("ivotas");
        //r.addAdmin(a);
        rmi.remote_print("New client");
        System.out.println("Successfully connected to port " + server.getPort());
        noExceptions = true;
      } catch (Exception e) {
        System.out.println("Failed to connect to port " + server.getPort());
        try {
          TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException es) {
          System.out.println("Error sleep: " + es.getMessage());
        }
        updatePort(server);
      }

      if (noExceptions) break;
    }

    return rmi;
  }

  private User searchUser(ArrayList<String> searchParams) {
    boolean noExceptions = false;
    User user = null;

    while (true) {
      try {
        user = this.rmi.searchUser(searchParams.get(0), searchParams.get(1));
        System.out.println(rmi);
        noExceptions = true;
      } catch (RemoteException | NullPointerException e) {
        System.out.println("Waiting...");
        this.rmi = this.connectRMIInterface(this.tableServer);
      }

      if (noExceptions) {
        break;
      }
    }

    return user;
  }
}
