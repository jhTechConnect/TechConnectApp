package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.model.Comment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 10/24/2016.
 */

public class CommentsView extends LinearLayout {

    @Bind(R.id.comments_headerTextView)
    TextView headerTextView;

    private List<Comment> comments;

    public CommentsView(Context context) {
        super(context);
    }

    public CommentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        updateViews();
    }

    private void updateViews() {
        if (comments.size() > 0) {
            removeView(headerTextView);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
