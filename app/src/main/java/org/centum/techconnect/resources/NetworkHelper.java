package org.centum.techconnect.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import org.centum.techconnect.model.Contact;
import org.centum.techconnect.model.Device;
import org.centum.techconnect.model.Flowchart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Phani on 1/14/2016.
 * <p/>
 * Responsible for downloading the resources from the defined URL.
 * The flowchart is also assembled from the JSON definitions here.
 */
public class NetworkHelper {

    public static final String ENTRY_ID = "q1";
    private static final String URL = "https://raw.githubusercontent.com/PhaniGaddipati/TechConnectApp/master/JSON/";
    private static final String INDEX_FILE = "index.json";
    private Context context;

    public NetworkHelper(Context context) {
        this.context = context;
    }


    public Contact[] loadCallDirectoryContacts(boolean useCached) throws IOException, JSONException {
        List<Contact> contacts = new LinkedList<>();
        JSONObject index = new JSONObject(downloadFileAsStr(URL + INDEX_FILE, useCached));
        String dirFile = index.getString("callDir");
        JSONArray jsonContacts = new JSONArray(downloadFileAsStr(URL + dirFile, useCached));
        for (int i = 0; i < jsonContacts.length(); i++) {
            contacts.add(Contact.fromJSON(jsonContacts.getJSONObject(i)));
        }
        return contacts.toArray(new Contact[contacts.size()]);
    }

    /**
     * Loads all of the devices, which loads the flowcharts, and resources.
     *
     * @param useCached
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public Device[] loadDevices(boolean useCached) throws IOException, JSONException {
        //Load the devices first
        List<Device> deviceList = new LinkedList<>();
        JSONObject index = new JSONObject(downloadFileAsStr(URL + INDEX_FILE, useCached));
        JSONArray devicesids = index.getJSONArray("deviceids");
        for (int i = 0; i < devicesids.length(); i++) {
            deviceList.add(Device.fromJSON(index.getJSONObject(devicesids.getString(i))));
        }
        //Now load all of the flowcharts for the device/deviceproblems
        for (Device device : deviceList) {
            device.getEndUserRole().setFlowchart(loadFlowchart(URL, device.getEndUserRole().getJsonFile(), ENTRY_ID, useCached));
            device.getTechRole().setFlowchart(loadFlowchart(URL, device.getTechRole().getJsonFile(), ENTRY_ID, useCached));
        }

        //Load images
        Set<String> toLoadURLs = new HashSet<>();
        Set<Flowchart> visited = new HashSet<>();
        Queue<Flowchart> toVisit = new LinkedList<>();
        for (Device device : deviceList) {
            if (device.getEndUserRole().getFlowchart() != null)
                toVisit.add(device.getEndUserRole().getFlowchart());
            if (device.getTechRole().getFlowchart() != null)
                toVisit.add(device.getTechRole().getFlowchart());
        }

        while (toVisit.size() > 0) {
            Flowchart flow = toVisit.remove();
            visited.add(flow);
            if (flow.hasImages())
                toLoadURLs.addAll(Arrays.asList(flow.getImageURLs()));

            // Add unvisited children
            for (int i = 0; i < flow.getNumChildren(); i++) {
                Flowchart child = flow.getChildByIndex(i);
                if (!visited.contains(child) && child != null) {
                    toVisit.add(child);
                }
            }
        }

        for (String url : toLoadURLs) {
            if (!ResourceHandler.get().hasStringResource(url)) {
                String file;
                try {
                    file = downloadImage(url);
                } catch (IOException e) {
                    //Image can't be loaded, eh ignore it for now.
                    //TODO somehow inform user of failed image loading
                    file = null;
                }
                ResourceHandler.get().addStringResource(url, file);
            }
        }

        return deviceList.toArray(new Device[deviceList.size()]);
    }

    /**
     * Loads a particular flowchart by filename.
     *
     * @param path
     * @param filename
     * @param entry
     * @param useCached
     * @return
     * @throws JSONException
     */
    private Flowchart loadFlowchart(String path, String filename, String entry, boolean useCached) throws JSONException {
        Map<String, JSONObject> elements = deepLoadElements(path, filename, useCached);
        Map<JSONObject, Flowchart> flowchartsByJSON = new HashMap<>();
        Map<String, Flowchart> flowchartsByID = new HashMap<>();
        //Create maps
        for (String key : elements.keySet()) {
            JSONObject obj = elements.get(key);
            Flowchart chart = Flowchart.fromJSON(obj, key);
            flowchartsByJSON.put(obj, chart);
            flowchartsByID.put(key, chart);
        }

        //Create links
        for (String key : flowchartsByID.keySet()) {
            Flowchart chart = flowchartsByID.get(key);
            JSONObject obj = elements.get(key);
            JSONArray opts = obj.getJSONArray("options");
            JSONArray next = obj.getJSONArray("next_question");
            for (int i = 0; i < next.length(); i++) {
                Flowchart child = flowchartsByID.get(next.getString(i));
                chart.addChild(opts.getString(i), child);
            }
        }

        return flowchartsByID.get(filename + "/" + entry);
    }

    /**
     * Loads all json objects referenced by the given file, traversing the entire tree.
     *
     * @param path
     * @param filename
     * @param useCached
     * @return
     * @throws JSONException
     */
    @NonNull
    private Map<String, JSONObject> deepLoadElements(String path, String filename, boolean useCached) throws JSONException {
        //Map of jsonname/id to JSONObject
        Map<String, JSONObject> loadedElements = new HashMap<>();
        Set<String> loadedJSONs = new HashSet<>();
        Queue<String> toLoadJSON = new LinkedList<>();
        toLoadJSON.add(filename);

        while (toLoadJSON.size() > 0) {
            String jsonFile = toLoadJSON.poll();
            Map<String, JSONObject> elements = loadElements(path, jsonFile, useCached);
            extendIDs(jsonFile, elements);
            Set<String> referencedJSONs = getReferencedJSONs(elements);
            loadedJSONs.add(jsonFile);
            loadedElements.putAll(elements);

            for (String json : referencedJSONs) {
                if (!loadedJSONs.contains(json)) {
                    toLoadJSON.add(json);
                }
            }
        }
        return loadedElements;
    }

    /**
     * Finds all json files referenced.
     *
     * @param elements
     * @return
     * @throws JSONException
     */
    private Set<String> getReferencedJSONs(Map<String, JSONObject> elements) throws JSONException {
        Set<String> jsons = new HashSet<>();
        for (String id : elements.keySet()) {
            JSONObject obj = elements.get(id);
            JSONArray nextQuestions = obj.getJSONArray("next_question");
            for (int i = 0; i < nextQuestions.length(); i++) {
                String nextID = nextQuestions.getString(i);
                jsons.add(nextID.substring(0, nextID.indexOf("/")));
            }
        }
        return jsons;
    }

    /**
     * Extends any question ids to include the file name. E.G
     * if an id "q1" is in "someJSON.json", the id will be changed to
     * "someJSON.json/q1"
     *
     * @param jsonFile
     * @param elements
     * @throws JSONException
     */
    private void extendIDs(String jsonFile, Map<String, JSONObject> elements) throws JSONException {
        //convert next_question into extended id
        for (String id : elements.keySet()) {
            JSONObject obj = elements.get(id);
            JSONArray nextQuestions = obj.getJSONArray("next_question");
            JSONArray newNextQuestions = new JSONArray();
            for (int i = 0; i < nextQuestions.length(); i++) {
                String oldNextID = nextQuestions.getString(i);
                String newNextID = oldNextID;
                if (oldNextID.endsWith(".json")) {
                    //implied entry id
                    newNextID = oldNextID + "/" + ENTRY_ID;
                } else if (!oldNextID.contains(".json")) {
                    //implied json file
                    newNextID = jsonFile + "/" + nextQuestions.getString(i);
                }
                newNextQuestions.put(newNextID);
            }
            obj.put("next_question", newNextQuestions);
        }
    }

    /**
     * Loads the objects of just the given file.
     *
     * @param path
     * @param jsonName
     * @param useCached
     * @return
     * @throws JSONException
     */
    private Map<String, JSONObject> loadElements(String path, String jsonName, boolean useCached) throws JSONException {
        Map<String, JSONObject> elements = new HashMap<>();
        String abspath = path + File.separator + jsonName;
        String json = null;
        try {
            json = downloadFileAsStr(abspath, useCached);
        } catch (IOException e) {
            e.printStackTrace();
            return elements;
        }
        JSONArray obj = new JSONArray(json);
        for (int i = 0; i < obj.length(); i++) {
            JSONObject el = obj.getJSONObject(i);
            elements.put(jsonName + "/" + el.getString("id"), el);
        }
        return elements;
    }

    /**
     * Downloads a file into a single string.
     *
     * @param urlS
     * @param useCached
     * @return
     * @throws IOException
     */
    private String downloadFileAsStr(String urlS, boolean useCached) throws IOException {
        if (useCached && ResourceHandler.get().hasStringResource(urlS)) {
            Logger.getLogger("NetworkHelper").log(Level.INFO, "Loading from cache: " + urlS);
            return ResourceHandler.get().getStringResource(urlS);
        } else {
            Logger.getLogger("NetworkHelper").log(Level.INFO, "Downloading: " + urlS);
            java.net.URL url = new URL(urlS);
            URLConnection conection = url.openConnection();
            conection.connect();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            input.close();
            ResourceHandler.get().addStringResource(urlS, builder.toString());
            return builder.toString();
        }
    }

    /**
     * Downloads an image.
     *
     * @param imageUrl
     * @return
     * @throws IOException
     */
    private String downloadImage(String imageUrl) throws IOException {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream out = null;

        connection = (HttpURLConnection) new URL(imageUrl).openConnection();
        is = connection.getInputStream();
        bitmap = BitmapFactory.decodeStream(is);
        if (connection != null)
            connection.disconnect();
        if (out != null) {
            out.flush();
            out.close();
        }
        if (is != null) {
            is.close();
        }

        String fileName = "i" + (int) Math.round(Integer.MAX_VALUE * Math.random());
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Loaded image: " + imageUrl + " --> " + fileName);
        return fileName;
    }

}
