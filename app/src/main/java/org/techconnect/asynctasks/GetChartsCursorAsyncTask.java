package org.techconnect.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.techconnect.sql.TCDatabaseHelper;

/**
 * Created by Phani on 11/12/2016.
 */

public class GetChartsCursorAsyncTask extends AsyncTask<String, Void, Cursor> {

    private Context context;

    public GetChartsCursorAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Cursor doInBackground(String... filter) {
        String searchStr;
        if (filter.length == 0) {
            searchStr = "";
        } else {
            searchStr = filter[0];
        }
        return TCDatabaseHelper.get(context).getAllFlowchartsCursor(searchStr);
    }
}
