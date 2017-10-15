import java.io.IOException;
import java.util.ArrayList;
import Data.*;

public class Data {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        Department department1 = new Department("Engenharia Informatica");
        ArrayList<Department> departments = new ArrayList<Department>();
        departments.add(department1);

        Faculty faculty1 = new Faculty("FCTUC", departments);
        ArrayList<Faculty> faculties = new ArrayList<Faculty>();
        faculties.add(faculty1);

        User user1= new User("Teresa", "123", department1, faculty1, "9140975", "Rua X", "444",
                "expireDate", 1 );
        ArrayList<User> users = new ArrayList<User>();
        users.add(user1);

        writeFile(users,"Users");
        ArrayList<User> usersFromFile = (ArrayList<User>) readFile("User");

        writeFile(departments,"Departments");
        ArrayList<Department> departmentsFromFile = (ArrayList<Department>) readFile("Department");

        writeFile(faculties,"Faculties");
        ArrayList<Faculty> facultiesFromFile = (ArrayList<Faculty>) readFile("Faculty");

        System.out.println(usersFromFile);
        System.out.println(departmentsFromFile);
        System.out.println(facultiesFromFile);
    }

    private static void writeFile(Object classe, String className) throws IOException, ClassNotFoundException {
        String filename = "ObjectFiles/" + className + ".dat";
        File file = new File();
        file.openWrite(filename);
        file.writeObject(classe);
        file.closeWrite();
    }

    public static ArrayList readFile(String className) throws IOException, ClassNotFoundException {
        String filename;
        if (className.substring(className.length() - 1).equals("y")) {
            filename = "ObjectFiles/" + className.substring(0, className.length() - 1) + "ies" + ".dat";
        }
        else {
            filename = "ObjectFiles/" + className + "s" + ".dat";
        }
        File file = new File();
        file.openRead(filename);
        ArrayList <Object> array = null;
        try {
            array = (ArrayList <Object>) file.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        file.closeRead();
        return array;
    }


}
