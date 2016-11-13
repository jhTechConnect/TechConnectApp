package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doranwalsten on 11/12/16.
 */
public class UserListItemView extends LinearLayout implements View.OnClickListener {
    @Bind(R.id.user_pic)
    ImageView user_pic;
    @Bind(R.id.user_name)
    TextView user_name;

    User user;

    public UserListItemView(Context context) {
        super(context);
    }

    public UserListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        user_pic.setImageResource(R.drawable.ic_done_black_48dp);
        updateViews();
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User u) {
        this.user = u;
    }

    public void updateViews() {
        if (this.user == null) {
            user_name.setText("Test Text");
        } else {
            user_name.setText(user.getName());
        }
    }


    @Override
    public void onClick(View view) {

    }
}
