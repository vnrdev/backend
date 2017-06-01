package be.kdg.prog4.reflection;

import java.sql.*;

/**
 * Created on 20/08/2015.
 */

public class DatabaseConnection {
    static Connection connection;
    static Statement statement;

    public static Connection getConnection() {
        return connection;
    }

    private static void loadDriver(){
        try {
            Class.forName("org.hsqldb.jdbcDriver");

        } catch (ClassNotFoundException e) {
            System.err.println("Fatal error: cannot load"
                    + " database driver");
            System.exit(1);
        }
    }

    public static void makeConnection(){
        loadDriver();
        try {
            connection =
                    DriverManager.getConnection(
                            "jdbc:hsqldb:file:dbData/test1",
                            "sa", "sa");

        } catch (SQLException e) {
            System.err.println("Fatal error: cannot create"
                    + " a connection to the database");
            System.exit(1);
        }
    }

    public static void createStmt(Connection connection){
        try {
            statement = connection.createStatement();

        } catch (SQLException e) {
            System.err.println("Error: cannot create statement");
        }
    }

    public static void closeConnection(){
        try {
            //statement.close();
            connection.close();

        } catch (SQLException e) {
            // verdere foutafhandeling
            System.out.println("fout: "+e.getMessage());
        } finally {
            // tracht alsnog te sluiten (extra try/catch)
        }
    }
}
