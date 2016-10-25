package org.techconnect.views;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.centum.techconnect.R;
import org.techconnect.model.Comment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 10/25/2016.
 */

public class CommentsResourcesTabbedView extends LinearLayout implements TabLayout.OnTabSelectedListener {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.tabContentContainer)
    FrameLayout tabContentContainer;

    private CommentThreadView commentThreadView;
    private ResourcesView resourcesView;

    private List<Comment> comments = new ArrayList<>(0);
    private List<String> resources = new ArrayList<>(0);

    public CommentsResourcesTabbedView(Context context) {
        super(context);
    }

    public CommentsResourcesTabbedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentsResourcesTabbedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setItems(List<Comment> comments, List<String> resources) {
        this.comments = comments;
        this.resources = resources;
        updateViews();
    }

    public void updateViews() {
        tabContentContainer.removeAllViews();
        commentThreadView = (CommentThreadView) LayoutInflater.from(getContext()).inflate(R.layout.comment_thread_view, tabContentContainer, false);
        resourcesView = (ResourcesView) LayoutInflater.from(getContext()).inflate(R.layout.resources_view, tabContentContainer, false);
        commentThreadView.setComments(comments);
        resourcesView.setResources(resources);
        tabContentContainer.addView(commentThreadView);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabContentContainer.removeAllViews();
        if (tab.getPosition() == 0) {
            tabContentContainer.addView(commentThreadView);
        } else {
            tabContentContainer.addView(resourcesView);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
