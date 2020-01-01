package io.adamgomez.user.models;

import io.adamgomez.common.services.DBConnection;
import io.adamgomez.common.utils.StatementGenerator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Login {

    private String id;
    private User user;
    private String token;
    private String date;
    private String expired;

    private static String LOGIN = "login";
    private static String ID = "id";
    private static String USERID = "user_id";
    private static String TOKEN = "token";
    private static String DATE = "date";
    private static String EXPIRED = "expired";

    public Login(User user) {
        this.user = user;
        this.expired = "false";
    }

    public boolean insertIntoDatabase(DBConnection connection) {
        generateLoginToken();
        if ((this.user == null) || (this.token == null) || (this.date == null)) {
            return false;
        } else {
            ResultSet rs = connection.executeQuery(generateInsertLoginStatement());
            return true;
        }
    }

    public String generateInsertLoginStatement() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        columns.add(USERID);
        columns.add(TOKEN);
        columns.add(DATE);
        columns.add(EXPIRED);
        values.add("\"" + this.user.getId() + "\"");
        values.add("\"" + this.token + "\"");
        values.add("\"" + this.date + "\"");
        values.add("\"" + this.expired + "\"");
        return StatementGenerator.insert(LOGIN, columns, values);
    }

    private String generateLoginToken() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            this.date = Long.toString(System.currentTimeMillis());
            String preHashString = this.date + this.user.getId();

            byte[] messageDigest = md.digest(preHashString.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            this.token = hashText;
            return this.token;
        } catch (NoSuchAlgorithmException nsae) {
            System.out.println("Error: Could not create token");
            return null;
        }
    }

    public static boolean validateToken(DBConnection connection, String token) {
        ResultSet rs = connection.executeQuery(generateValidateTokenStatement(token));
        try {
            rs.last();
            return rs.getRow() != 0;
        } catch(SQLException sqle) {
            System.out.println("Error: Unable to access results");
            return false;
        }
    }

    public static boolean expireToken(DBConnection connection, String token) {
        ResultSet rs = connection.executeQuery(generateExpireTokenStatement(token));
        return true;
    }

    public static String getUserIdFromToken(DBConnection connection, String token) {
        ResultSet rs = connection.executeQuery(generateGetUserIdStatement(token));
        try {
            rs.last();
            return rs.getString(USERID);
        } catch(SQLException sqle) {
            System.out.println("Error: Unable to access results");
            return null;
        }
    }

    private static String generateExpireTokenStatement(String token) {
        ArrayList<String> set = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        set.add(EXPIRED + "=" + "\'true\'");
        conditions.add(TOKEN + "=" + "\'" + token + "\'");
        return StatementGenerator.update(LOGIN, set, conditions);
    }

    private static String generateValidateTokenStatement(String token) {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        columns.add(TOKEN);
        conditions.add(TOKEN + "=" + "\'" + token + "\'");
        conditions.add(EXPIRED + "=" + "\'false\'");
        return StatementGenerator.select(columns, LOGIN, conditions);
    }

    private static String generateGetUserIdStatement(String token) {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        columns.add(USERID);
        conditions.add(TOKEN + "=" + "\'" + token + "\'");
        conditions.add(EXPIRED + "=" + "\'false\'");
        return StatementGenerator.select(columns, LOGIN, conditions);
    }

    public String getTokenJSON() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ \"cookie\": { \"key\": \"token\", \"value\": \"");
        sb.append(this.token);
        sb.append("\" } }");
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
