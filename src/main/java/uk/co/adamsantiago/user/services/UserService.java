package uk.co.adamsantiago.user.services;

import uk.co.adamsantiago.common.utils.StatementGenerator;
import uk.co.adamsantiago.common.services.DBConnection;
import uk.co.adamsantiago.common.models.Insert;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import java.util.ArrayList;

public class UserService {

    final static Logger logger = Logger.getLogger(UserService.class);

    private final static String USER = "user";

    private final static String FIRST_NAME = "first_name";
    private final static String LAST_NAME = "last_name";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";

    public static boolean createUser(DBConnection connection, JSONObject postData) {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        String insertStatement = populateInsertData(postData);
        connection.executeQuery(insertStatement);
        return true;
    }

    private static String populateInsertData(JSONObject postData) {
        String firstName = (String)postData.get("first_name");
        String lastName = (String)postData.get("last_name");
        String email = (String)postData.get("email");
        String password = (String)postData.get("password");

        ArrayList<String> columns = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();

        columns.add(FIRST_NAME);
        columns.add(LAST_NAME);
        columns.add(EMAIL);
        columns.add(PASSWORD);
        values.add(firstName);
        values.add(lastName);
        values.add(email);
        values.add(password);
        return StatementGenerator.insert(USER, columns, values);
    }

}
