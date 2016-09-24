package org.centum.techconnect.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.TechConnectNetworkHelper;
import com.java.model.FlowChart;
import com.java.model.Graph;
import com.java.model.Vertex;

import org.centum.techconnect.resources.ResourceHandler;

import java.io.IOException;

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
            TechConnectNetworkHelper helper = new TechConnectNetworkHelper();
            helper.login("dwalste@jhu.edu","dwalsten");
            FlowChart test_chart = helper.getChart("testchart99999999");
            helper.logout();
            return new Object[]{test_chart};
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
            FlowChart flow = (FlowChart) objects[0];
            System.out.println(flow.getId());
            System.out.println(flow.getOwner());
            Graph g = flow.getGraph();
            for (Vertex v : g.getVertices()) {
                System.out.println(v.getId());
            }
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
