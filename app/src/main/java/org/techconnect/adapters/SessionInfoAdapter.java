package org.techconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doranwalsten on 6/7/17.
 */

public class SessionInfoAdapter extends BaseAdapter {

    private Map<String,String> sessionInfo;
    private String[] fields;

    LayoutInflater inflater;
    Context context;

    public SessionInfoAdapter(Context context,HashMap<String,String> map, String[] fields) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        sessionInfo = map;
        this.fields = fields;
    }

    @Override
    public int getCount() {
        return sessionInfo.size();
    }

    @Override
    public String[] getItem(int i) {
        String key = fields[i];
        return new String[] {key, sessionInfo.get(key)};
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            String[] item = getItem(i);
            ((TextView) view.findViewById(android.R.id.text1)).setText(item[1]);
            ((TextView) view.findViewById(android.R.id.text2)).setText(item[0]);

            ((TextView) view.findViewById(android.R.id.text1)).setTextSize(18);
            ((TextView) view.findViewById(android.R.id.text2)).setTextSize(12);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            String[] item = getItem(i);
            ((TextView) view.findViewById(android.R.id.text1)).setText(item[1]);
            ((TextView) view.findViewById(android.R.id.text2)).setText(item[0]);

            ((TextView) view.findViewById(android.R.id.text1)).setTextSize(18);
            ((TextView) view.findViewById(android.R.id.text2)).setTextSize(12);
        }
        return view;
    }

    private static class ViewHolder {
        private View view;
    }
}
