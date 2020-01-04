package uk.co.adamsantiago.user.models;

import uk.co.adamsantiago.common.services.DBConnection;
import uk.co.adamsantiago.common.utils.StatementGenerator;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {

    private String id = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String password = "";

    private static String ID = "id";
    private static String FIRSTNAME = "first_name";
    private static String LASTNAME = "last_name";
    private static String EMAIL = "email";
    private static String PASSWORD = "password";
    private static String USER = "user";

    public User(JSONObject json) {
        this.firstName = (String)json.get("first_name");
        this.lastName = (String)json.get("last_name");
        this.email = (String)json.get("email");
        this.password = (String)json.get("password");
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, DBConnection connection) {
        this.id = id;
        ResultSet rs = connection.executeQuery(generateGetUserDetailsStatement());
        try {
            rs.last();
            this.firstName = rs.getString(FIRSTNAME);
            this.lastName = rs.getString(LASTNAME);
            this.email = rs.getString(EMAIL);
        } catch(SQLException sqle) {
            System.out.println("Error: Unable to access results");
        }
    }

    public boolean insertIntoDatabase(DBConnection connection) {
        if ((this.firstName == null) || (this.lastName == null) || (this.email == null) || (this.password == null)) {
            return false;
        } else {
            ResultSet rs = connection.executeQuery(generateInsertUserStatement());
            return true;
        }
    }

    public String generateInsertUserStatement() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        columns.add(FIRSTNAME);
        columns.add(LASTNAME);
        columns.add(EMAIL);
        columns.add(PASSWORD);
        values.add("\"" + this.firstName + "\"");
        values.add("\"" + this.lastName + "\"");
        values.add("\"" + this.email + "\"");
        values.add("\"" + this.password + "\"");
        return StatementGenerator.insert(USER, columns, values);
    }

    public String generateGetUserDetailsStatement() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        columns.add(FIRSTNAME);
        columns.add(LASTNAME);
        columns.add(EMAIL);
        conditions.add(ID + "=" + "\'" + this.id + "\'");
        return StatementGenerator.select(columns, USER, conditions);
    }

    public boolean doesUserExist(DBConnection connection) {
        ResultSet rs = connection.executeQuery(generateDoesUserExistStatement());
        try {
            rs.last();
            return rs.getRow() != 0;
        } catch(SQLException sqle) {
            System.out.println("Error: Unable to access results");
            return false;
        }
    }

    public String generateDoesUserExistStatement() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        columns.add(EMAIL);
        conditions.add(EMAIL + "=" + "\'" + this.email + "\'");
        return StatementGenerator.select(columns, USER, conditions);
    }

    public String generateValidateCredentialsStatement() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        columns.add(ID);
        conditions.add(EMAIL + "=" + "\'" + this.email + "\'");
        conditions.add(PASSWORD + "=" + "\'" + this.password + "\'");
        return StatementGenerator.select(columns, USER, conditions);
    }

    public boolean validateCredentials(DBConnection connection) {
        ResultSet rs = connection.executeQuery(generateValidateCredentialsStatement());
        try {
            rs.last();
            if (rs.getRow() != 0) {
                this.id = rs.getString(ID);
                return true;
            } else {
                return false;
            }
        } catch(SQLException sqle) {
            System.out.println("Error: Unable to access results");
            return false;
        }
    }

    public String getUserJSON() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ \"user\": { \"id\": \"");
        sb.append(this.id);
        sb.append("\", \"first_name\": \"");
        sb.append(this.firstName);
        sb.append("\", \"last_name\": \"");
        sb.append(this.lastName);
        sb.append("\", \"email\": \"");
        sb.append(this.email);
        sb.append("\" } }");
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
