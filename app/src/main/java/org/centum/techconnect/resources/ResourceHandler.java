package org.centum.techconnect.resources;

import android.content.Context;
import android.content.SharedPreferences;

import com.java.model.FlowChart;

import org.centum.techconnect.model.Contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Phani on 4/16/2016.
 * <p/>
 * Manages downloaded resources for offline capability.
 */
public class ResourceHandler {

    private static ResourceHandler instance = null;
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences resourcePrefs;


    private FlowChart[] devices =  new FlowChart[0];//These are flowcharts with the device type
    private FlowChart[] problems = new FlowChart[0];//These are flowcharts with the problem type
    private FlowChart[] misc = new FlowChart[0];//These are flowcharts with the misc type

    //private Device[] devices = new Device[0];
    private Contact[] contacts = new Contact[0];

    private List<ResourceHandlerListener> listeners = new ArrayList<>();

    public ResourceHandler(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("ResourceHandler", Context.MODE_PRIVATE);
        resourcePrefs = context.getSharedPreferences("ResourceHandlerCached", Context.MODE_PRIVATE);
    }

    public static ResourceHandler get(Context context) {
        if (instance == null) {
            instance = new ResourceHandler(context);
        }
        return instance;
    }

    public static ResourceHandler get() {
        return instance;
    }

    public FlowChart[] getDevices() {
        return devices;
    }

    public void setDevices(FlowChart[] devices) {
        FlowChart[] oldDevices = this.devices;
        this.devices = devices;
        Arrays.sort(devices, new Comparator<FlowChart>() {
            @Override
            public int compare(FlowChart device, FlowChart t1) {
                return device.getName().compareTo(t1.getName());
            }
        });
        for (ResourceHandlerListener l : listeners) {
            l.onDevicesChanged(oldDevices, devices);
        }
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public void setContacts(Contact[] contacts) {
        Contact[] oldContacts = this.contacts;
        this.contacts = contacts;
        for (ResourceHandlerListener l : listeners) {
            l.onContactsChanged(oldContacts, contacts);
        }
    }

    public boolean hasStringResource(String tag) {
        return resourcePrefs.getString(tag, null) != null;
    }

    public String getStringResource(String tag) {
        return resourcePrefs.getString(tag, null);
    }

    public void addStringResource(String tag, String s) {
        resourcePrefs.edit().putString(tag, s).apply();
    }

    public void clear() {
        resourcePrefs.edit().clear().apply();
        setDevices(new FlowChart[0]);
        setContacts(new Contact[0]);
    }

    public void addListener(ResourceHandlerListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeListener(ResourceHandlerListener listener) {
        this.listeners.remove(listener);
    }
}
