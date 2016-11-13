package org.techconnect.asynctasks;

import android.os.AsyncTask;

import org.techconnect.model.User;
import org.techconnect.network.TCNetworkHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by doranwalsten on 11/13/16.
 */

public class SearchUsersAsyncTask extends AsyncTask<Void, List<User>, List<User>> {

    private String query;
    private int limit;
    private int skip;

    public SearchUsersAsyncTask(String query, int limit, int skip) {
        this.query = query;
        this.limit = limit;
        this.skip = skip;
    }

    /**
     * Returns search results, or null if it could not be completed.
     * @param voids
     * @return
     */
    @Override
    protected List<User> doInBackground(Void... voids) {
        try {
            return new TCNetworkHelper().searchUsers(query,limit,skip);
        }
        catch(IOException ex){
            return null;
        }
    }
}

