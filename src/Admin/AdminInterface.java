package Admin;

import java.rmi.Remote;

public interface AdminInterface extends Remote {
  void print_on_client(String s) throws java.rmi.RemoteException;
}
