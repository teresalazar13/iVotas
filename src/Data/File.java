package Data;

import java.io.*;

public class File {
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public boolean openRead(String nomeDoFicheiro){
        try {
            ois = new ObjectInputStream(new FileInputStream(nomeDoFicheiro));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void openWrite(String nomeDoFicheiro) throws IOException {
        oos = new ObjectOutputStream(new FileOutputStream(nomeDoFicheiro));
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
