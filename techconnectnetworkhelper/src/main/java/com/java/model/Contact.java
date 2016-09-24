package com.java.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Phani on 4/28/2016.
 */
public class Contact {

    private String name, title, phone, location;

    public static Contact fromJSON(JSONObject obj) throws JSONException {
        Contact c = new Contact();
        c.setName(obj.getString("name"));
        c.setLocation(obj.getString("location"));
        c.setPhone(obj.getString("phone"));
        c.setTitle(obj.getString("title"));
        return c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
