package org.centum.techconnect.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.resources.TechConnectNetworkHelper;
import com.java.model.FlowChart;

import com.java.resources.ResourceHandler;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Phani on 8/6/2016.
 * Modified by Doran on 9/20/2016 for new REST API
 */
public class LoadResourcesAsyncTask extends AsyncTask<Void, Void, Object[]> {

    private final Context context;
    private ExecutionCompleteListener listener;

    public LoadResourcesAsyncTask(Context context, ExecutionCompleteListener listener) {
        this.context = context;
        this.listener = listener;
    }


    @Override
    protected Object[] doInBackground(Void... voids) {
        Log.d(LoadResourcesAsyncTask.class.getName(), "Loading resources...");
        try {
            TechConnectNetworkHelper helper = new TechConnectNetworkHelper(context);
            helper.login("dwalste1@jhu.edu","dwalsten");
            //Try to download any devices to the App
            ArrayList<FlowChart> devices = new ArrayList<FlowChart>();
            helper.getCatalog(true); //Used to generate the list of devices, can change later
            devices = (ArrayList<FlowChart>) helper.getDevices(); //Get the array of devices
            helper.logout();
            return new Object[]{devices};
        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (JSONException f) {
            f.printStackTrace();
        }
        */
        return null;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        System.out.println("Success!");
        if (objects != null && ResourceHandler.get() != null) {
            ArrayList<FlowChart> flow = (ArrayList<FlowChart>) objects[0];
            FlowChart[] devices = new FlowChart[flow.size()];
            devices = flow.toArray(devices);
            System.out.println(flow.get(0).getName());
            System.out.println(flow.get(0).getDescription());
            //Now, want to add these objects to the ResourceHandler
            ResourceHandler.get().setDevices(devices);
        }
        if (listener != null) {
            listener.onFinished(objects == null);
        }
    }

    /*
    @Override
    protected Object[] doInBackground(Void... voids) {
        Log.d(LoadResourcesAsyncTask.class.getName(), "Loading resources...");
        try {
            NetworkHelper helper = new NetworkHelper(context);
            Device[] devices = helper.loadDevices(true);
            Contact[] contacts = helper.loadCallDirectoryContacts(true);
            return new Object[]{devices, contacts};
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        if (objects != null && ResourceHandler.get() != null) {
            ResourceHandler.get().setDevices((Device[]) objects[0]);
            ResourceHandler.get().setContacts((Contact[]) objects[1]);
        }
        if (listener != null) {
            listener.onFinished(objects == null);
        }
    }
    */
    public interface ExecutionCompleteListener {
        void onFinished(boolean error);
    }
}
