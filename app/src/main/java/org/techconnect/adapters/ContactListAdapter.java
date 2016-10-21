package org.techconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.centum.techconnect.R;
import org.techconnect.networkhelper.model.Contact;
import org.techconnect.views.ContactListItemView;

/**
 * Created by Phani on 4/28/2016.
 */
public class ContactListAdapter extends ArrayAdapter<Contact> {

    public ContactListAdapter(Context context, Contact[] objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Contact c = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item_view, parent, false);
        }
        ((ContactListItemView) view).setContact(c);
        return view;
    }
}
