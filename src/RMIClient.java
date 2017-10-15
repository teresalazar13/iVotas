import Servers.RMIServer.*;

import java.rmi.*;

public class RMIClient {

  public static void main(String args[]) {

    //System.getProperties().put("java.security.policy", "policy.all");
    //System.setSecurityManager(new RMISecurityManager());

    try {
      RMIInterface r = (RMIInterface) Naming.lookup("project");
      String a = r.sayHello();
      System.out.println(a);
      r.remote_print("print do client para o servidor...");
    } catch (Exception e) {
      System.out.println("Exception in main: " + e);
    }

  }

}