package org.techconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.techconnect.R;
import org.techconnect.model.session.Session;
import org.techconnect.views.SessionListItemView;

import java.util.List;
import java.util.Map;

/**
 * Created by doranwalsten on 6/8/17.
 */

public class ContactExpertListAdapter extends BaseAdapter {

    private Map<String,String> sessionInfo;
    private String[] fields;
    private List<Session> sessions;

    LayoutInflater inflater;
    Context context;

    public ContactExpertListAdapter(Context context, List<Session> sessions) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.sessions = sessions;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Session getItem(int i) {
        return sessions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ContactExpertListAdapter.ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ContactExpertListAdapter.ViewHolder();
            view = inflater.inflate(R.layout.session_list_item_view,viewGroup,false);
            ((SessionListItemView) view).setChecklist(true);
            ((SessionListItemView) view).setSession(getItem(i));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ContactExpertListAdapter.ViewHolder) view.getTag();
            ((SessionListItemView) view).setChecklist(true);
            ((SessionListItemView) view).setSession(getItem(i));
        }
        return view;
    }

    private static class ViewHolder {
        private View view;
    }

    /*
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_activated_2,null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Here, take the info desired from the session and drop into the view where desired
        Session session = TCDatabaseHelper.get(context).getSessionFromCursor(cursor);
        ((TextView) view.findViewById(android.R.id.text1)).setText(String.format("%s,%s", session.getDeviceName(), session.getManufacturer()));
        ((TextView) view.findViewById(android.R.id.text2)).setText(String.format("%s,%s,%s",session.getModelNumber(),session.getSerialNumber(),
                new SimpleDateFormat("MM/dd/yyyy").format(new Date(session.getCreatedDate()))));

        ((TextView) view.findViewById(android.R.id.text1)).setTextSize(18);
        ((TextView) view.findViewById(android.R.id.text1)).setTypeface(Typeface.DEFAULT_BOLD);
    }
    */
}
