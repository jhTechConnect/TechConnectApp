package org.techconnect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;

import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.List;

/**
 * Created by dwalsten on 11/23/16.
 */

public class SessionCursorAdapter extends CursorAdapter {

    public SessionCursorAdapter(Context context) {
        super(context, TCDatabaseHelper.get(context).getActiveSessionsCursor(), false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
