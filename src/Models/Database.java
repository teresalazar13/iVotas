package Models;

import Data.*;
import java.sql.*;


public class Database {
  // Connects to a postgres DB
  public Connection connectToDB() {
    Connection conn = null;

    try {
      Class.forName("org.postgresql.Driver");
      conn = DriverManager
              .getConnection("jdbc:postgresql://localhost:5432/ivotas",
                      "Machado", "");
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(0);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    System.out.println("Opened database successfully");
    return conn;
  }

  // Create needed tables
  public void createTables() {
    Connection dbConn = null;
    PreparedStatement preparedStatement = null;

    /*
    * "department_id  integer REFERENCES departments, " +
    * "faculty_id     integer REFERENCES faculties, " +
    * "expired_date   DATE        NOT NULL, " +
    * */
    String dropPreviousTables =
            "DROP TABLE IF EXISTS users";
    String createUsersTable =
            "CREATE TABLE users(" +
                    "user_id        SERIAL PRIMARY KEY, " + // serial primary para ele dar autoassign e autoincrement do id
                    "name           VARCHAR(50) NOT NULL, " +
                    "password       VARCHAR(50) NOT NULL, " +
                    "contact        VARCHAR(10) NOT NULL, " +
                    "address        VARCHAR(50) NOT NULL, " +
                    "cc             VARCHAR(10) NOT NULL, " +
                    "type           SMALLINT    NOT NULL, " +
                    "UNIQUE(cc)" +
                    ")";

    try {
      dbConn = connectToDB();

      // Drop existing tables
      preparedStatement = dbConn.prepareStatement(dropPreviousTables);
      preparedStatement.execute();

      // Create new tables
      preparedStatement = dbConn.prepareStatement(createUsersTable);
      preparedStatement.executeUpdate();

      System.out.println("Users table created");

      // close statement and db connection
      preparedStatement.close();
      dbConn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Inserts user into database
  public void insertUser(User user) {
    Connection dbConn = null;
    PreparedStatement preparedStatement = null;

    try {
      String command =
              "INSERT INTO users(" +
                      "name, password, contact, address, cc, type) " +
                      "VALUES(?, ?, ?, ?, ?, ?" +
                      ")";

      dbConn = connectToDB();

      // Prepare insert statement without values
      preparedStatement = dbConn.prepareStatement(command);

      // Values to be inserted
      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getContact());
      preparedStatement.setString(4, user.getAddress());
      preparedStatement.setString(5, Integer.toString(user.getCc()));
      preparedStatement.setInt(6, user.getType());

      // Execute insert statement
      preparedStatement.executeUpdate();

      // close statement and db connection
      preparedStatement.close();
      dbConn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  // Search user by username TODO: serach by any field
  public User searchUser(String name) {
    User user = null;
    Connection dbConn = null;
    PreparedStatement preparedStatement = null;

    try {
      // Query to be executed
      String command = "SELECT * FROM users " +
              "WHERE name=?";

      dbConn = connectToDB();

      preparedStatement = dbConn.prepareStatement(command);
      preparedStatement.setString(1, name);

      ResultSet results = preparedStatement.executeQuery();

      while (results.next()) {
        // build user from data, this still needs the info from dep and faculty
        user = new User(
                results.getString("name"),
                results.getString("password"),
                null,
                null,
                results.getString("contact"),
                results.getString("address"),
                Integer.parseInt(results.getString("cc")),
                Long.parseLong(results.getString("expireDate")),
                results.getInt("type")
        );
      }

      preparedStatement.close();
      dbConn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return user;
  }
}
