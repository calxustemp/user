package uk.co.adamsantiago.user.models;

import static uk.co.adamsantiago.user.Constants.*;
import org.json.simple.JSONObject;

public class User {

    private String firstName;
    private String lastName;
    private String email;

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIRST_NAME, firstName);
        jsonObject.put(LAST_NAME, lastName);
        jsonObject.put(EMAIL, email);
        return jsonObject;
    }
}