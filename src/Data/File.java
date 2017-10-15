package Data;

import java.io.*;

public class File {
  private ObjectInputStream ois;
  private ObjectOutputStream oos;

  public boolean openRead(String filename){
    try {
      ois = new ObjectInputStream(new FileInputStream(filename));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public void openWrite(String filename) throws IOException {
    oos = new ObjectOutputStream(new FileOutputStream(filename));
  }

  public void writeObject(Object o) throws IOException {
    oos.writeObject(o);
  }

  public Object readObject() throws IOException, ClassNotFoundException {
    return ois.readObject();
  }

  public void closeRead() throws  IOException {
    ois.close();
  }

  public void closeWrite() throws IOException {
    oos.close();
  }
}
