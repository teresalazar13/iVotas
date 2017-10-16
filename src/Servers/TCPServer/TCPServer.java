package Servers.TCPServer;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TCPServer {
  public static void main(String args[]){
    int number = 0;
    CopyOnWriteArrayList <Connection> threads = new CopyOnWriteArrayList<Connection>();

    try {
      int serverPort = 6000;
      System.out.println("Listening on port 6000");

      ServerSocket listenSocket = new ServerSocket(serverPort);
      System.out.println("LISTEN SOCKET = " + listenSocket);

      while(true) {
        Socket clientSocket = listenSocket.accept();
        System.out.println("CLIENT_SOCKET (created at accept()) = " + clientSocket);
        number++;
        Connection thread = new Connection(clientSocket, number, threads);
        threads.add(thread);
      }
    } catch(IOException e) {
      System.out.println("Listen: " + e.getMessage());
    }
  }
}

// Thread to handle comm with client
class Connection extends Thread {
  private BufferedReader bufferedReader;
  private DataOutputStream out;
  private int thread_number;
  private CopyOnWriteArrayList<Connection> threads;

  public Connection (Socket aClientSocket, int number, CopyOnWriteArrayList<Connection> threads) {
    thread_number = number;
    this.threads = threads;

    try {
      Socket clientSocket = aClientSocket;
      bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      out = new DataOutputStream(clientSocket.getOutputStream());
      this.start();
    } catch(IOException e){
      System.out.println("Connection: " + e.getMessage());
    }
  }

  public void run(){
    try {
      while(true){
        String data = bufferedReader.readLine();
        System.out.println("T[" + thread_number + "] Received: " + data);

        for (int i = 0; i < threads.size(); i++) {
          System.out.println("thread " + i);
          threads.get(i).getOut().writeUTF(data);
        }
      }
    } catch(EOFException e){
      System.out.println("EOF: " + e);
    } catch(IOException e){
      System.out.println("IO: " + e);
    }
  }

  public DataOutputStream getOut() {
    return out;
  }
}
