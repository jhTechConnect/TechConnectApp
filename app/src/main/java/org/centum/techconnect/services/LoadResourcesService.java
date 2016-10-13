package org.centum.techconnect.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.java.model.FlowChart;

import org.centum.techconnect.activities.MainActivity;
import org.centum.techconnect.resources.ResourceHandler;
import org.centum.techconnect.resources.TechConnectNetworkHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will be used to run the loading of resources in the background
 * Goal is to replace the AsyncTask used previously (better practice)
 * Goal is to also replace the previous NetworkHelper by splitting up functions between different
 * IntentServices when needed
 * Created by doranwalsten on 10/11/16.
 */
public class LoadResourcesService extends IntentService {

    //Intent String Entires
    public static final String REQUEST_STRING = "loadResourceRequest";
    public static final String RESULT_STATUS = "loadResourceStatus";
    public static final String RESULT_MESSAGE = "loadResourceResponseMessage";


    //Just using the default constructor from super class for now
    public LoadResourcesService() {
        super("Load Resources");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        //Just really want to run the same stuff from the AsyncTask
        Log.d(LoadResourcesService.class.getName(), "Loading resources...");
        TechConnectNetworkHelper helper = new TechConnectNetworkHelper(getApplicationContext());//Just need application context
        FlowChart[] devices;
        String resultMessage = "success";
        ResultType result = ResultType.SUCCESS;
        try {
            //Try to download any devices to the App
            helper.getCatalog(true); //Used to generate the list of devices, can change later
            List<FlowChart> dev = helper.getDevices();
            devices = new FlowChart[dev.size()]; //Get the array of devices
            devices = dev.toArray(devices);
            //If this succeeds, should in theory be able to load resources

            if (!loadResources(dev)) {
                //If this fails at some point, we know there are some resources missing
                result = ResultType.RES_ERROR;
                resultMessage = "Some resources could not be loaded";
            }

            if (ResourceHandler.get() != null) {
                ResourceHandler.get().setDevices(devices);
            }

        } catch (IOException e) { //Occurs with error in getting the charts, mainly that the http request failed
            e.printStackTrace();
            resultMessage = e.getMessage();
            result = ResultType.RES_ERROR;
        }

        //Here, we develop a BroadcastIntent to provide to the main activity
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.ResponseReceiver.PROCESS_RESPONSE);
        broadcastIntent.putExtra(RESULT_STATUS, result);
        broadcastIntent.putExtra(RESULT_MESSAGE, resultMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    public enum ResultType {
        SUCCESS, RES_ERROR
    }

    public boolean loadResources(List<FlowChart> devices) {
        boolean success = true;
        for (FlowChart f : devices) {
            for (String resourcePath : f.getAllRes()) {
                if (ResourceHandler.get().hasStringResource(resourcePath)) {
                    Log.d(TechConnectNetworkHelper.class.getName(), "ResourceHandler has \"" + resourcePath + "\"");
                } else {
                    String file = null;
                    try {
                        //Now, all files will have the full URL already
                        file = downloadFile(resourcePath);
                    } catch (IOException e) { //Need to catch here in order to continue trying to download other res
                        success = false;
                        e.printStackTrace();
                        Log.e(TechConnectNetworkHelper.class.getName(), "Failed to load: " + resourcePath);
                        file = null;
                    }
                    ResourceHandler.get().addStringResource(resourcePath, file);
                }
            }
        }
        return success;
    }

    /**
     * Downloads an image.
     *
     * @param fileUrl
     * @return
     * @throws IOException
     */
    private String downloadFile(String fileUrl) throws IOException {
        Log.d(TechConnectNetworkHelper.class.getName(), "Attempting to download " + fileUrl);
        String fileName = "i" + (int) Math.round(Integer.MAX_VALUE * Math.random());
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl.replace(" ", "%20")).openConnection();

        FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
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
}
