package io.adamgomez.user;

import io.adamgomez.user.models.Login;
import io.adamgomez.user.models.User;
import io.adamgomez.common.services.DBConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;
import spark.Response;


import static spark.Spark.post;
import static spark.Spark.get;

public class Routes {

    public static void main(String[] args) {
        post("/user", (req, res) -> {

            setHeaders(res);

            JSONObject jsonObject = parseJson(req);

            if (jsonObject == null) {
                return "Error: parsing JSON";
            }

            User user = generateUser(jsonObject);
            DBConnection connection = new DBConnection();

            if (!user.doesUserExist(connection)) {

                if (user.insertIntoDatabase(connection)) {
                    connection.close();
                    return "Success";
                }
                connection.close();
                return "Error";
            } else {
                connection.close();
                res.status(418);
                return "Error: User already exists";
            }
        });

        get("/user", (req, res) -> {

            setHeaders(res);

            JSONObject jsonObject = parseJson(req);

            if (jsonObject == null) {
                return "Error: parsing JSON";
            }

            User user = generateUser(jsonObject);

            DBConnection connection = new DBConnection();

            if(user.validateCredentials(connection)) {
                Login login = new Login(user);
                login.insertIntoDatabase(connection);
                connection.close();
                return "Login Successful";
            } else {
                connection.close();
                return "Login Failed";
            }

        });

        post("/login", (req, res) -> {

            setHeaders(res);

            JSONObject jsonObject = parseJson(req);

            if (jsonObject == null) {
                return "Error: parsing JSON";
            }

            User user = generateUser(jsonObject);

            DBConnection connection = new DBConnection();

            if(user.validateCredentials(connection)) {
                Login login = new Login(user);
                login.insertIntoDatabase(connection);
                connection.close();
                return login.getTokenJSON();
            } else {
                connection.close();
                res.status(401);
                return "Login Failed";
            }

        });
        get("/login", (req, res) -> {
            
            String token = req.queryParams("token");

            System.out.println(token);

            DBConnection connection = new DBConnection();

            String userId = Login.getUserIdFromToken(connection, token);

            if (userId != null) {
                User user = new User(userId, connection);
                connection.close();
                res.status(200);
                return user.getUserJSON();
            } else {
                connection.close();
                res.status(403);
                return "";
            }
        });
        get("/signout", (req, res) -> {

            String token = req.queryParams("token");

            DBConnection connection = new DBConnection();

            if (Login.expireToken(connection, token)) {
                connection.close();
                res.status(200);
                return "";
            } else {
                connection.close();
                res.status(403);
                return "";
            }
        });

    }

    private static JSONObject parseJson(Request req) {
        JSONParser parser = new JSONParser();
        String body = req.body();
        try {
            return (JSONObject)parser.parse(body);
        } catch(ParseException pe) {
            System.out.println("Could not parse request");
            return null;
        }
    }

    private static User generateUser(JSONObject jsonObject) {
        return new User((JSONObject)jsonObject.get("user"));
    }

    private static void setHeaders(Response res) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Methods", "POST");
    }

}
