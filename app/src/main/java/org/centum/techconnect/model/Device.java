package org.centum.techconnect.model;

import org.centum.techconnect.resources.NetworkHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Phani on 1/22/2016.
 * <p/>
 * A device represents its roles, and the flowcharts for those roles.
 * Metadata about the device is also stored.
 */
public class Device {

    private String name;
    private String imageURL;
    private DeviceRole techRole;
    private DeviceRole endUserRole;
    private String[] resources;

    public static Device fromJSON(JSONObject obj) throws JSONException {
        Device device = new Device();
        device.name = obj.getString("name");
        device.imageURL = obj.has("image") ? obj.getString("image") : null;
        device.techRole = new DeviceRole(obj.getString("chart"));
        device.endUserRole = new DeviceRole(obj.getString("clin_chart"));
        JSONArray arr = obj.getJSONArray("resources");
        String res[] = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            res[i] = NetworkHelper.URL + NetworkHelper.RESOURCE_FOLDER + arr.getString(i);
        }
        device.resources = res;
        return device;
    }

    public DeviceRole getTechRole() {
        return techRole;
    }

    public DeviceRole getEndUserRole() {
        return endUserRole;
    }

    public String[] getResources() {
        return resources;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }
}
