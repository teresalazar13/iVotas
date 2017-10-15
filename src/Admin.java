import Servers.RMIServer.*;
import java.rmi.registry.LocateRegistry;

public class Admin {

  public static void main(String args[]) {

    //System.getProperties().put("java.security.policy", "policy.all");
    //System.setSecurityManager(new RMISecurityManager());

    try {
      RMIInterface r = (RMIInterface) LocateRegistry.getRegistry(7000).lookup("ivotas");
      String a = r.sayHello();
      System.out.println(a);
      r.remote_print("print do client para o servidor...");
    } catch (Exception e) {
      System.out.println("Exception in main: " + e);
    }

  }

}