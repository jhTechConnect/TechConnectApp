package org.techconnect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;

import org.centum.techconnect.R;
import org.techconnect.model.session.Session;
import org.techconnect.model.session.SessionListener;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

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
        return LayoutInflater.from(context).inflate(R.layout.session_list_item_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SessionListItemView sessionListItemView = (SessionListItemView) view;
        sessionListItemView.setSession(TCDatabaseHelper.get(context).getSessionFromCursor(cursor));

    }
}
