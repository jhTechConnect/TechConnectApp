package org.techconnect.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.List;

/**
 * Created by doranwalsten on 6/9/17.
 */

public class LoadActiveSessionsAsyncTask extends AsyncTask<Void, Void, List<Session>> {

    Context context;

    public LoadActiveSessionsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<Session> doInBackground(Void... voids) {
        return TCDatabaseHelper.get(context).getActiveSessions();
    }
}
