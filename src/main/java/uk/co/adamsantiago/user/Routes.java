package uk.co.adamsantiago.user;

import uk.co.adamsantiago.common.services.DBConnection;
import uk.co.adamsantiago.user.services.UserService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;


public class Routes {

    final static Logger logger = Logger.getLogger(Routes.class);

    public static void main(String[] args) {
        get("/user", (req, res) -> {
            DBConnection connection = new DBConnection();
            JSONObject getData = parseQueryParams(req);
            UserService.getUser(connection, getData);
            connection.close();
            res.status(200);
            return "";
        });
        post("/user", (req, res) -> {
            DBConnection connection = new DBConnection();
            JSONObject postData = parseJson(req);
            UserService.createUser(connection, postData);
            connection.close();
            res.status(200);
            return "";
        });
        put("/user", (req, res) -> {
            DBConnection connection = new DBConnection();
            connection.close();
            res.status(200);
            return "";
        });
        delete("/user", (req, res) -> {
            DBConnection connection = new DBConnection();
            connection.close();
            res.status(200);
            return "";
        });
    }
    private static JSONObject parseJson(Request req) {
        JSONParser parser = new JSONParser();
        String body = req.body();
        try {
            return (JSONObject)parser.parse(body);
        } catch(ParseException pe) {
            logger.error("Failed to parse data");
            return null;
        }
    }
    private static JSONObject parseQueryParams(Request req) {
        JSONObject jsonObject = new JSONObject();
        for (String queryParam : req.queryParams()) {
            jsonObject.put(queryParam, req.queryParams(queryParam));
        }
        return jsonObject;
    }
}
