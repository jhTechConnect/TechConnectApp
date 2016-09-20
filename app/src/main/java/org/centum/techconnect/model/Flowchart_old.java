package org.centum.techconnect.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Phani on 1/23/2016.
 *
 * The core flowchart object. Each object has the properties
 * loaded from the json files. The children are also stored,
 * creating a n-way tree.
 */
public class Flowchart_old {

    private String key;
    private String question;
    private String details;
    private String[] attachments;
    private String imageURLs[] = new String[0];
    private List<String> options = new LinkedList<>();
    private List<Flowchart_old> children = new LinkedList<>();

    public Flowchart_old(String question, String details, String[] attachments, String imageURLs[], String key) {
        this.question = question;
        this.details = details;
        this.attachments = attachments;
        this.key = key;
        this.imageURLs = imageURLs;
    }

    public static Flowchart_old fromJSON(JSONObject object, String key) throws JSONException {
        String[] attachmentsStr;
        if (object.isNull("attachment")) {
            attachmentsStr = new String[0];
        } else {
            JSONArray attachments = object.getJSONArray("attachment");
            attachmentsStr = new String[attachments.length()];
            for (int i = 0; i < attachments.length(); i++) {
                attachmentsStr[i] = attachments.getString(i);
            }
        }
        String imageURLs[] = new String[0];
        if (object.has("image")) {
            JSONArray arr = object.getJSONArray("image");
            imageURLs = new String[arr.length()];
            for (int i = 0; i < imageURLs.length; i++) {
                imageURLs[i] = arr.getString(i);
            }
        }
        return new Flowchart_old(object.getString("question"),
                object.getString("details"), attachmentsStr, imageURLs, key);
    }

    public void addChild(String option, Flowchart_old child) {
        options.add(option);
        children.add(child);
    }

    public int getNumChildren() {
        return children.size();
    }

    public String[] getOptions() {
        return options.toArray(new String[options.size()]);
    }

    public Flowchart_old getChild(String option) {
        return children.get(options.indexOf(option));
    }

    public Flowchart_old getChildByIndex(int i) {
        return children.get(i);
    }

    public String getQuestion() {
        return question;
    }

    public String getDetails() {
        return details;
    }

    public String[] getAttachments() {
        return attachments;
    }

    public String[] getImageURLs() {
        return imageURLs;
    }

    public boolean hasImages() {
        return imageURLs != null && imageURLs.length > 0;
    }

    public String getKey() {
        return key;
    }
}
