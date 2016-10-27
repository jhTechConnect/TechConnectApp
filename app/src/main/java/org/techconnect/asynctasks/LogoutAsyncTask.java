package org.techconnect.asynctasks;

import android.os.AsyncTask;

import org.techconnect.model.UserAuth;
import org.techconnect.network.TCNetworkHelper;

import java.io.IOException;

/**
 * Created by Phani on 10/26/2016.
 */

public class LogoutAsyncTask extends AsyncTask<UserAuth, Void, Void> {
    @Override
    protected Void doInBackground(UserAuth... userAuths) {
        TCNetworkHelper hepler = new TCNetworkHelper();
        for (UserAuth auth : userAuths) {
            if (auth != null) {
                try {
                    hepler.logout(auth);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
