package be.kdg.prog4.reflection;

import javax.xml.crypto.Data;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 20/08/2015.
 */

public class MyInvocationHandler implements InvocationHandler {
    Class c = null;
    int counter;

    public MyInvocationHandler(Class c) {
        this.c = c;
        counter = 1;
    }

    private String getTableName(){
        String tableName = "";
        Annotation annotation = c.getAnnotation(Storable.class);
        //if storable.table() is leeg -> gebruik naam entiteit
        if (annotation instanceof Storable) {
            Storable storable = (Storable) annotation;
            tableName = storable.table();
            if (tableName.equals("")||tableName == null){
                tableName = c.getClass().getSimpleName();
            }
            System.out.println("name: " + tableName);
        }
        return tableName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class insertedClass = null;
        Object object = null;

        String methodName = method.getName();

        if (methodName.equals("createTable")) {
            createTable();
        }

        if (methodName.equals("create")) {
            return create(args);
        }

        if (methodName.equals("retrieve")) {
            return retrieve(args[0]);
        }

        if (methodName == "update") {
            update(args);
        }

        if (methodName == "delete"){
            delete(args);
        }
        return null;
    }

    private void delete(Object[] args) throws SQLException, IllegalAccessException {
        Object object;
        object=args[0];
        Field[] fields = object.getClass().getDeclaredFields();
        fields[0].setAccessible(true);
        fields[1].setAccessible(true);
        fields[2].setAccessible(true);

        PreparedStatement delete = DatabaseConnection.connection.prepareStatement(
                "DELETE FROM "+getTableName()+" WHERE naam = ?");
        delete.setString(1, fields[0].get(object).toString());
        //object.method()  field.get/set(object)
        System.out.println(delete);

        delete.execute();
        delete.close();
    }


    private void update(Object[] args) throws IllegalAccessException, SQLException {
        Object object;
        System.out.println("person updated");
        DatabaseConnection.createStmt(DatabaseConnection.getConnection());

        object = args[0];
        Field[] fields = object.getClass().getDeclaredFields();
        //TODO: aanpassen zodat elk object dat kan gebruiken
        fields[0].setAccessible(true);
        fields[1].setAccessible(true);
        fields[2].setAccessible(true);

        //TODO: aanpassen zodat elk object dat kan gebruiken
        String update = String.format("UPDATE %s SET naam='%s', interesse='%s', leeftijd='%s' WHERE naam='%s'",
                getTableName(),
                fields[0].get(object).toString(),
                fields[1].get(object).toString(),
                fields[2].get(object).toString(),
                fields[0].get(object).toString());
        System.out.println(update);
        DatabaseConnection.statement.executeUpdate(update);

        DatabaseConnection.statement.close();
    }

    private Object retrieve(Object arg) throws SQLException, InstantiationException, IllegalAccessException {
        Object object;
        System.out.println("person retrieved");
        DatabaseConnection.createStmt(DatabaseConnection.getConnection());

        String searchValue = arg.toString();
        System.out.println("searchValue: " + searchValue);

        ResultSet result;
        //try {
        String query = "SELECT naam, interesse, leeftijd FROM " + getTableName() + " WHERE naam = " + "'" + searchValue + "'";
        System.out.println(query);
        result = DatabaseConnection.statement.executeQuery(query);

        if (!result.isBeforeFirst()) {
            object = null;
            System.out.println("object was null");
        } else {
            object = c.newInstance();
            Field[] fields = c.getDeclaredFields();

            while (result.next()) {
                fields[0].setAccessible(true);
                fields[1].setAccessible(true);
                fields[2].setAccessible(true);

                fields[0].set(object, result.getString("naam"));
                fields[1].set(object, result.getString("interesse"));
                fields[2].set(object, result.getInt("leeftijd"));
            }
        }

        DatabaseConnection.statement.close();

        return object;
    }

    private Object create(Object[] args) throws IllegalAccessException, SQLException {
        Object object;
        System.out.println("person created");
        DatabaseConnection.createStmt(DatabaseConnection.getConnection());
        //insertedClass = args[0].getClass();
        object = args[0];
        System.out.println("CREATE string: " + object.toString());

        Field[] fields = c.getDeclaredFields();
        fields[0].setAccessible(true);
        fields[1].setAccessible(true);
        fields[2].setAccessible(true);

        String insertString = String.format("INSERT INTO %s VALUES(%d,'%s','%s','%s')", getTableName(), counter,
                fields[0].get(object),
                fields[1].get(object),
                fields[2].get(object));
        counter++;

        System.out.println("insertstring: " + insertString);
        DatabaseConnection.statement.executeUpdate(insertString);

        return object;
    }

    private void createTable() {
        System.out.println("table created");
        DatabaseConnection.createStmt(DatabaseConnection.getConnection());

        Field[] fields = c.getDeclaredFields();
        try {
            DatabaseConnection.statement.executeUpdate("DROP TABLE IF EXISTS " + getTableName());

            String create = "CREATE TABLE " + getTableName() +
                    "(sleutel INT NOT NULL PRIMARY KEY,";

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (i == fields.length - 1) {
                    create += fields[i].getName() + " VARCHAR(10) NOT NULL)";
                } else {
                    create += fields[i].getName() + " VARCHAR(10) NOT NULL, ";
                }
            }
            System.out.println(create);
            DatabaseConnection.statement.executeUpdate(create);
        } catch (SQLException e) {
            System.err.println("Error: cannot execute statement");
            System.out.println(e.getMessage());
        }
    }

}
