package uk.co.adamsantiago.user.services;

import uk.co.adamsantiago.common.utils.StatementGenerator;
import uk.co.adamsantiago.common.services.DBConnection;
import uk.co.adamsantiago.common.models.Insert;
import uk.co.adamsantiago.user.models.User; 
import static uk.co.adamsantiago.user.Constants.*;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserService {

    final static Logger logger = Logger.getLogger(UserService.class);

    public static boolean createUser(DBConnection connection, JSONObject postData) {
        String insertStatement = populateInsertData(postData);
        connection.executeQuery(insertStatement);
        return true;
    }

    public static String getUser(DBConnection connection, JSONObject getData) {
        String getStatement = populateGetData(getData);
        ResultSet rs = connection.executeQuery(getStatement);
        ArrayList<User> users = new ArrayList<User>();
        try {
            while(rs.next()) {
                String firstName = rs.getString(FIRST_NAME);
                String lastName = rs.getString(LAST_NAME);
                String email = rs.getString(EMAIL);
                User user = new User(firstName, lastName, email);
                users.add(user);
            }
        } catch(SQLException sqle) {
            logger.error("Failed to retrieve query results for query: " + getStatement);
            logger.debug(sqle.toString());
        }
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            jsonArray.add(user.toString());
        }
        return jsonArray.toString();
    }

    public static boolean updateUser(DBConnection connection, JSONObject putData) {
        String updateStatement = populateUpdateData(putData);
        connection.executeQuery(updateStatement);
        return true;
    }


    private static String populateInsertData(JSONObject postData) {
        String firstName = (String)postData.get(FIRST_NAME);
        String lastName = (String)postData.get(LAST_NAME);
        String email = (String)postData.get(EMAIL);
        String password = (String)postData.get(PASSWORD);

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

    private static String populateUpdateData(JSONObject putData) {
        String id = (String)putData.get(ID);
        String firstName = (String)putData.get(FIRST_NAME);
        String lastName = (String)putData.get(LAST_NAME);
        String email = (String)putData.get(EMAIL);
        String password = (String)putData.get(PASSWORD);

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

        return StatementGenerator.update(USER, columns, values, ID, id);
    }

    private static String populateGetData(JSONObject getData) {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> conditionColumns = new ArrayList<>();
        ArrayList<String> conditionValues = new ArrayList<>();

        columns.add(FIRST_NAME);
        columns.add(LAST_NAME);
        columns.add(EMAIL);

        String id = (String)getData.get(ID);
        if (id != null) {
            conditionColumns.add(ID);
            conditionValues.add(id);
        }

        return StatementGenerator.select(columns, USER, conditionColumns, conditionValues);
    }

}
