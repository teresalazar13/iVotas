package Models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class File {
  // Com esta abordagem vamos ter que guardar todos os objects como array list nalguma classe
  // Parece-me melhor guardar essas arrays list no RMI assim implementas logo tudo

  /*
  *  Como ler users do file:
  *  ObjectInputStream iS = new ObjectInputStream(new FileInputStream("users"));
  *  users = (ArrayList<User>) File.rObject(iS);
  *  iS.close()
  * */
  public Object rObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
    return inputStream.readObject();
  }

  // Como usar, imaginemos que criamos escrever no object uma arrayList de users:
  // ObjectOutputStream oSU = new ObjectOutputStream(new FileOutputStream("users"));
  // agency.wObject(oSU, users);
  // oSU.close()
  public void wObject(ObjectOutputStream oS, Object obj) throws  IOException {
    oS.writeObject(obj);
  }
}
