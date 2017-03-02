package org.techconnect.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.techconnect.sql.TCDatabaseHelper;

/**
 * Created by doranwalsten on 2/8/17.
 */

public class ExportResponsesAsyncTask extends AsyncTask <String, Void, Integer> {

    private Context context;

    public ExportResponsesAsyncTask(Context context) {
        this.context  = context;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        //Get generated string
        String resp = TCDatabaseHelper.get(context).writeResponsesToString(strings[0]);
        if (resp != null) {
            //Send email based on String arguments
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_TEXT, resp);
            emailIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(emailIntent, "Select App"));
            return 1;
        } else {
            return 0;
        }
    }
}
