package org.techconnect.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phani on 4/16/2016.
 * <p/>
 * Manages downloaded resources for offline capability.
 * Essentially maintains a mapping of URL --> local file
 */
public class ResourceHandler {

    public static final String RESOURCE_HANDLER_PREFS = "org.techconnect.resourcehandler";
    private static ResourceHandler instance = null;
    private SharedPreferences prefs;
    private Map<String, ArrayList<String>> resourceChartMap = new HashMap<>();
    private Context context;

    private ResourceHandler(Context context) {
        prefs = context.getSharedPreferences(RESOURCE_HANDLER_PREFS, Context.MODE_PRIVATE);
        this.context = context;
    }

    public static ResourceHandler get(Context context) {
        if (instance == null) {
            instance = new ResourceHandler(context);
        }
        return instance;
    }

    public boolean hasStringResource(String tag) {
        return prefs.getString(tag, null) != null;
    }

    public String getStringResource(String tag) {
        return prefs.getString(tag, null);
    }

    public void addStringResource(String tag, String s, String id) {
        //If not in library, need to add
        if (!resourceChartMap.containsKey(s)) {
            ArrayList<String> charts = new ArrayList<>();
            charts.add(id);
            resourceChartMap.put(s,charts);
        } else { // If in library, need to add new chart ID
            if (!resourceChartMap.get(s).contains(id)) {
                resourceChartMap.get(s).add(id);
            } else {
                Log.e(getClass().toString(),"Adding same resource twice from same chart");
            }
        }
        prefs.edit().putString(tag, s).apply();
    }

    public void addChartToMap(String tag, String id) {
        if (!resourceChartMap.get(getStringResource(tag)).contains(id)) {
            resourceChartMap.get(getStringResource(tag)).add(id);
            Log.d(getClass().toString(), "ResourceHandler added reference to map");
        } else {
            Log.d(getClass().toString(), "Duplicate chart ID");
        }
    }
    /**
     * We want to remove the String Resource from flowchart id. HOWEVER, this should only delete the
     * actual file from the phone if the resource is not in use by any other flowchart
     * @param tag
     */
    public void removeStringResource(String tag, String id) {
        String s = getStringResource(tag);
        ArrayList<String> charts = resourceChartMap.get(s);
        if (charts != null) {
            charts.remove(id);
            SharedPreferences.Editor editor = prefs.edit();
            if (charts.size() == 0) {
                //want to delete the file from the phone
                File file = new File(context.getFilesDir(), s);
                if (!file.delete()) {
                    Log.e(getClass().toString(), "Error in deleting resource");
                } else {
                    Log.d(getClass().toString(), String.format("ResourceHandler deleted %s", tag));
                    resourceChartMap.remove(s);
                    editor.remove(tag);
                }
            } else {
                resourceChartMap.put(s, charts);
            }
            editor.commit();
        } else {
            Log.e(getClass().toString(), String.format("%s, %s: This is a problem", tag, s));
        }
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
