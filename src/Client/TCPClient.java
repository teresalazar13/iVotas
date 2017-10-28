package Client;

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

      // Thread that reads from keyboard and write to the server
      VotingTerminalMenu votingTerminalMenu = new VotingTerminalMenu("votingTerminalMenu", socket);

      // the main thread loops reading from the server and writing to System.out
      String messageFromServer;
      Map<String, String> protocolValues;

      while((messageFromServer = inFromServer.readLine()) != null) {
        // set current username to log in
        synchronized (votingTerminalMenu.getT()) {
          protocolValues = client.parseProcolMessage(messageFromServer);
          votingTerminalMenu.setCurrentUsername(protocolValues.get("user"));
          votingTerminalMenu.getT().notify();
        }

        // check if login was sucessful
        messageFromServer = inFromServer.readLine();

        boolean loginState;
        protocolValues = client.parseProcolMessage(messageFromServer);
        if ("timeout".equals(protocolValues.get("type"))) {
          System.out.println("You were idle for too long, if you want to vote go back to the voting table");
          votingTerminalMenu.getT().stop();
          votingTerminalMenu = new VotingTerminalMenu("votingTerminalMenu", socket);
          continue;
        }

        loginState = Boolean.parseBoolean(protocolValues.get("logged"));

        while (!loginState) {
          synchronized (votingTerminalMenu.getT()) {
            votingTerminalMenu.getT().notify();
          }

          messageFromServer = inFromServer.readLine();
          if ("timeout".equals(protocolValues.get("type"))) {
            break;
          }
          protocolValues = client.parseProcolMessage(messageFromServer);
          loginState = Boolean.parseBoolean(protocolValues.get("logged"));
        }
        if ("timeout".equals(protocolValues.get("type"))) {
          System.out.println("You were idle for too long, if you want to vote go back to the voting table");
          votingTerminalMenu.getT().stop();
          votingTerminalMenu = new VotingTerminalMenu("votingTerminalMenu", socket);
          continue;
        }

        // get election info
        messageFromServer = inFromServer.readLine();
        protocolValues = client.parseProcolMessage(messageFromServer);
        String cleanElection = protocolValues.get("election").replace("Election{", "");
        Map<String, String> electionInfo = client.parseElectionString(cleanElection);
        ArrayList<String> candidateListsName = client.parseCandidateLists(electionInfo.get("candidateLists"));


        // update terminal object
        synchronized (votingTerminalMenu.getT()) {
          votingTerminalMenu.setLoginState(true);
          votingTerminalMenu.setElectionInfo(electionInfo);
          votingTerminalMenu.setCandidateListsName(candidateListsName);
          votingTerminalMenu.getT().notify();
        }

        // get vote status
        messageFromServer = inFromServer.readLine();
        protocolValues = client.parseProcolMessage(messageFromServer);

        if ("timeout".equals(protocolValues.get("type"))) {
          System.out.println("You were idle for too long, if you want to vote go back to the voting table");
          votingTerminalMenu.getT().stop();
          votingTerminalMenu = new VotingTerminalMenu("votingTerminalMenu", socket);
          continue;
        }
        if ("success".equals(protocolValues.get("vote"))) {
          votingTerminalMenu.setVoteState(true);
        }
        synchronized (votingTerminalMenu.getT()) {
          votingTerminalMenu.getT().notify();
        }

      }
    } catch (IOException e) {
      if(inFromServer == null)
        System.out.println("\nUsage: java Client.TCPClient <host> <port>\n");
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

  private Map<String, String> parseElectionString(String cleanElectionString) {
    // The protocol message is going to be parsed into key-value map
    Map<String, String> electionValues = new LinkedHashMap<>();

    // Clean protocol message of whitespaces
    String [] cleanElectionWords = cleanElectionString.split("[=-]");

    for (int i = 0; i < cleanElectionWords.length; i+=2) {
      electionValues.put(cleanElectionWords[i], cleanElectionWords[i+1]);
    }

    return electionValues;
  }

  private ArrayList<String> parseCandidateLists(String candidateLists) {
    // The protocol message is going to be parsed into key-value map
    ArrayList<String> candidateListNames = new ArrayList<>();

    // Clean protocol message of whitespaces
    candidateLists = candidateLists.replace("{", "");
    candidateLists = candidateLists.replace("}", "");
    candidateLists = candidateLists.replace("[", "");
    candidateLists = candidateLists.replace("]", "");

    String [] cleanCandidateLists = candidateLists.split("CandidateList");

    for (int i = 1; i < cleanCandidateLists.length; i++) {
      String [] cleanCandidateList = cleanCandidateLists[i].split("[:*]");

      for (int j = 0; j < cleanCandidateList.length; j+=4) {
        candidateListNames.add(cleanCandidateList[j+1]);
      }
    }

    return candidateListNames;
  }
}

// Thread responsible for reading from keyboard and writing to the server
class VotingTerminalMenu implements Runnable {
  private String currentUsername;
  private boolean voteState;
  private boolean loginState;
  private boolean interrupt;
  private Map<String, String> electionInfo;
  private ArrayList<String> candidateListsName;
  private String threadName;
  private PrintWriter outToServer;
  private Socket clientSocket;
  private Thread t;

  public VotingTerminalMenu(String threadName, Socket clientSocket) {
    this.currentUsername = null;
    this.loginState = false;
    this.voteState= false;
    this.interrupt = true;
    this.electionInfo = null;
    this.candidateListsName = null;
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
        }

        // Ask for password and send it to the server
        String messageToServer;
        while (!this.loginState) {
          messageToServer = this.menu();
          this.outToServer.println(messageToServer);

          synchronized (this.getT()) {
            this.getT().wait();
          }
        }

        // Choose the list to vote
        String listNameToVote = votingMenu();
        this.outToServer.println("type | vote ; " +
                "username | " + this.currentUsername + " ; " +
                "choice | " + listNameToVote + " ;"
        );

        synchronized (this.getT()) {
          this.getT().wait();
        }

        if (!this.voteState) {
          System.out.println("You cannot vote, you have already voted or can't vote on this election/list");
        }

        // clean vars received
        this.setLoginState(false);
        this.setVoteState(false);
        this.setInterrupt(false);
        this.setCurrentUsername(null);
        this.setElectionInfo(null);
        this.setCandidateListsName(null);
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
    System.out.println(messageToServer);

    return messageToServer;
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
      if (maximum <= option || option < 0) {
        System.out.println("Please write an integer between 0 and " + (maximum-1));
      }
      else {
        return option;
      }
    }
  }
  private String votingMenu() {
    System.out.println("-----" + this.electionInfo.get("name") + "-----");
    System.out.println(this.electionInfo.get("description"));

    for (int i = 0; i < this.candidateListsName.size(); i++) {
      System.out.println("[" + i + "]" + "--> " + this.candidateListsName.get(i) );
    }

    System.out.println("[" + this.candidateListsName.size() + "]" + "--> Blank Vote");

    System.out.println("List to vote: ");
    int list = getValidInteger(this.candidateListsName.size()+1);

    if (list == this.candidateListsName.size()) {
      return "blank";
    }

    return this.candidateListsName.get(list);
  }

  public Thread getT() { return t; }

  public String getCurrentUsername() { return currentUsername; }
  public void setCurrentUsername(String currentUsername) { this.currentUsername = currentUsername; }

  public boolean isLoginState() { return loginState; }
  public void setLoginState(boolean loginState) { this.loginState = loginState; }

  public Map<String, String> getElectionInfo() { return electionInfo; }
  public void setElectionInfo(Map<String, String> electionInfo) { this.electionInfo = electionInfo; }

  public ArrayList<String> getCandidateListsName() { return candidateListsName; }
  public void setCandidateListsName(ArrayList<String> candidateListsName) { this.candidateListsName = candidateListsName; }

  public boolean isVoteState() { return voteState; }
  public void setVoteState(boolean voteState) { this.voteState = voteState; }

  public boolean isInterrupt() { return interrupt; }
  public void setInterrupt(boolean interrupt) { this.interrupt = interrupt; }
}
