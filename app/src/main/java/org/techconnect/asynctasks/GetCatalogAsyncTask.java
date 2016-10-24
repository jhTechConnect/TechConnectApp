package org.techconnect.asynctasks;

import android.os.AsyncTask;

import org.techconnect.model.FlowChart;
import org.techconnect.network.TCNetworkHelper;

import java.io.IOException;

/**
 * Created by Phani on 10/23/2016.
 */

public class GetCatalogAsyncTask extends AsyncTask<Void, Void, FlowChart[]> {
    @Override
    protected FlowChart[] doInBackground(Void... voids) {
        try {
            return new TCNetworkHelper().getCatalog();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
