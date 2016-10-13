package org.centum.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.centum.techconnect.R;
import com.java.model.Contact;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 4/28/2016.
 */
public class ContactListItemView extends FrameLayout {

    @Bind(R.id.name_textView)
    TextView nameTextView;
    @Bind(R.id.title_textView)
    TextView titleTextView;
    @Bind(R.id.loc_textView)
    TextView locationTextView;

    private Contact contact;

    public ContactListItemView(Context context) {
        super(context);
    }

    public ContactListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        nameTextView.setText("");
        titleTextView.setText("");
        locationTextView.setText("");
        if (contact != null) {
            nameTextView.setText(contact.getName());
            titleTextView.setText(contact.getTitle());
            locationTextView.setText(contact.getLocation());
        }
        this.contact = contact;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
