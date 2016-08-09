package org.centum.techconnect.resources;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import org.centum.techconnect.model.Contact;
import org.centum.techconnect.model.Device;
import org.centum.techconnect.model.Flowchart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static final String URL = "https://s3.amazonaws.com/tech-connect/";
    public static final String JSON_FOLDER = "json/";
    public static final String RESOURCE_FOLDER = "resources/";
    private static final String INDEX_FILE = "index.json";
    private static final String PACKAGE_DIR = "TechConnect";
    private static final String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
            + File.separator + PACKAGE_DIR + File.separator;
    private Context context;

    public NetworkHelper(Context context) {
        this.context = context;
    }


    public Contact[] loadCallDirectoryContacts(boolean useCached) throws IOException, JSONException {
        List<Contact> contacts = new LinkedList<>();
        String indexJSON = getJSON(INDEX_FILE);
        JSONObject index = new JSONObject(indexJSON);
        String dirFile = index.getString("callDir");
        String callJSON = getJSON(dirFile);
        JSONArray jsonContacts = new JSONArray(callJSON);
        for (int i = 0; i < jsonContacts.length(); i++) {
            contacts.add(Contact.fromJSON(jsonContacts.getJSONObject(i)));
        }
        return contacts.toArray(new Contact[contacts.size()]);
    }

    @NonNull
    private String getJSON(String name) throws IOException {
        String indexJSON;
        if (!ResourceHandler.get().hasStringResource(name)) {
            String filePath = copyOrDownloadFile(JSON_FOLDER + name);
            if (filePath != null) {
                ResourceHandler.get().addStringResource(name, filePath);
            }
        }
        indexJSON = loadFileAsStr(context.getFileStreamPath(ResourceHandler.get().getStringResource(name)).getAbsolutePath());
        return indexJSON;
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
        JSONObject index = new JSONObject(getJSON(INDEX_FILE));
        JSONArray devicesids = index.getJSONArray("deviceids");
        for (int i = 0; i < devicesids.length(); i++) {
            deviceList.add(Device.fromJSON(index.getJSONObject(devicesids.getString(i))));
        }
        //Now load all of the flowcharts for the device/deviceproblems
        for (Device device : deviceList) {
            device.getEndUserRole().setFlowchart(loadFlowchart(device.getEndUserRole().getJsonFile(), ENTRY_ID, useCached));
            device.getTechRole().setFlowchart(loadFlowchart(device.getTechRole().getJsonFile(), ENTRY_ID, useCached));
        }

        //Load images & resources
        Set<String> toLoad = new HashSet<>();
        Set<Flowchart> visited = new HashSet<>();
        Queue<Flowchart> toVisit = new LinkedList<>();
        for (Device device : deviceList) {
            toLoad.addAll(Arrays.asList(device.getResources()));
            if (device.getEndUserRole().getFlowchart() != null) {
                toVisit.add(device.getEndUserRole().getFlowchart());
            }
            if (device.getTechRole().getFlowchart() != null) {
                toVisit.add(device.getTechRole().getFlowchart());
            }
        }

        while (toVisit.size() > 0) {
            Flowchart flow = toVisit.remove();
            visited.add(flow);
            if (flow.hasImages()) {
                toLoad.addAll(Arrays.asList(flow.getImageURLs()));
            }
            toLoad.addAll(Arrays.asList(flow.getAttachments()));

            // Add unvisited children
            for (int i = 0; i < flow.getNumChildren(); i++) {
                Flowchart child = flow.getChildByIndex(i);
                if (!visited.contains(child) && child != null) {
                    toVisit.add(child);
                }
            }
        }

        for (String resourcePath : toLoad) {
            String url = URL + RESOURCE_FOLDER + resourcePath;
            if (ResourceHandler.get().hasStringResource(resourcePath)) {
                Log.d(NetworkHelper.class.getName(), "ResourceHandler has \"" + resourcePath + "\"");
            } else {
                String file = null;
                try {
                    file = copyOrDownloadFile(RESOURCE_FOLDER + resourcePath);
                } catch (IOException e) {
                    //Image can't be loaded, eh ignore it for now.
                    //TODO somehow inform user of failed image loading
                    e.printStackTrace();
                    Log.e(NetworkHelper.class.getName(), "Failed to load: " + url);
                    file = null;
                }
                ResourceHandler.get().addStringResource(resourcePath, file);
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
    private Flowchart loadFlowchart(String filename, String entry, boolean useCached) throws JSONException {
        Map<String, JSONObject> elements = deepLoadElements(filename, useCached);
        Map<String, Flowchart> flowchartsByID = new HashMap<>();
        //Create maps
        for (String key : elements.keySet()) {
            JSONObject obj = elements.get(key);
            Flowchart chart = Flowchart.fromJSON(obj, key);
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
    private Map<String, JSONObject> deepLoadElements(String filename, boolean useCached) throws JSONException {
        //Map of jsonname/id to JSONObject
        Map<String, JSONObject> loadedElements = new HashMap<>();
        Set<String> loadedJSONs = new HashSet<>();
        Queue<String> toLoadJSON = new LinkedList<>();
        toLoadJSON.add(filename);

        while (toLoadJSON.size() > 0) {
            String jsonFile = toLoadJSON.poll();
            Map<String, JSONObject> elements = loadElements(jsonFile, useCached);
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
    private Map<String, JSONObject> loadElements(String jsonName, boolean useCached) throws JSONException {
        Map<String, JSONObject> elements = new HashMap<>();
        String json;
        try {
            json = getJSON(jsonName);
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

    private String loadFileAsStr(String fileUri) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileUri));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    private String copyOrDownloadFile(String path) throws IOException {
        String file = copyFile(DOWNLOAD_DIR + path);
        if (file == null) {
            file = downloadFile(URL + path);
        }
        return file;
    }

    /**
     * Downloads an image.
     *
     * @param fileUrl
     * @return
     * @throws IOException
     */
    private String downloadFile(String fileUrl) throws IOException {
        Log.d(NetworkHelper.class.getName(), "Attempting to download " + fileUrl);
        String fileName = "i" + (int) Math.round(Integer.MAX_VALUE * Math.random());
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl.replace(" ", "%20")).openConnection();

        FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        InputStream inputStream = connection.getInputStream();

        int readBytes;
        byte buffer[] = new byte[1024];
        if (inputStream != null) {
            while ((readBytes = inputStream.read(buffer)) > -1) {
                fileOutputStream.write(buffer, 0, readBytes);
            }
            inputStream.close();
        }

        connection.disconnect();
        fileOutputStream.flush();
        fileOutputStream.close();

        Logger.getLogger(getClass().getName()).log(Level.INFO, "Downloaded file: " + fileUrl + " --> " + fileName);
        return fileName;
    }

    /***
     * @param fileUrl
     * @return
     * @throws IOException
     */
    private String copyFile(String fileUrl) {
        String fileName = "i" + (int) Math.round(Integer.MAX_VALUE * Math.random());

        File file = new File(fileUrl);
        if (!file.exists()) {
            return null;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            InputStream inputStream = new FileInputStream(file);

            int readBytes;
            byte buffer[] = new byte[204800];
            while ((readBytes = inputStream.read(buffer)) > -1) {
                fileOutputStream.write(buffer, 0, readBytes);
            }
            inputStream.close();

            fileOutputStream.flush();
            fileOutputStream.close();

            Logger.getLogger(getClass().getName()).log(Level.INFO, "Copied file: " + fileUrl + " --> " + fileName);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
