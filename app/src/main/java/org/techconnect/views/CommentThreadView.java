package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.model.Comment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 10/24/2016.
 */

public class CommentThreadView extends LinearLayout {

    @Bind(R.id.comments_headerTextView)
    TextView headerTextView;

    private List<Comment> comments = new ArrayList<>(0);

    public CommentThreadView(Context context) {
        super(context);
    }

    public CommentThreadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentThreadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        if (this.comments == null) {
            this.comments = new ArrayList<>(0);
        }
        updateViews();
    }

    private void updateViews() {
        if (comments.size() > 0) {
            removeView(headerTextView);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (Comment comment : comments) {
                CommentView view = (CommentView) inflater.inflate(R.layout.comment_view, this, false);
                view.setComment(comment);
                addView(view);
            }
        } else {
            removeAllViews();
            addView(headerTextView);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
