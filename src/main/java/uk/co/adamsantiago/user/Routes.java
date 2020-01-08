package uk.co.adamsantiago.user;

import uk.co.adamsantiago.common.services.DBConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import spark.Request;
import spark.Response;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;


public class Routes {

    public static void main(String[] args) {
        get("/user", (req, res) -> {
            DBConnection connection = new DBConnection();
            connection.close();
            res.status(200);
            return "";
        });
        post("/user", (req, res) -> {
            DBConnection connection = new DBConnection();
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
}
