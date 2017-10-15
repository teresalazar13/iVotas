package Servers.RMIServer;

import Servers.RMIServer.RMIInterface;
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

            // m.change_text("Ola Boa Noite");
            // System.out.println(m);

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }

    }

}