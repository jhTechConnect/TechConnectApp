package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centum.techconnect.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 10/24/2016.
 */

public class ResourcesView extends LinearLayout {

    @Bind(R.id.resources_headerTextView)
    TextView resourcesHeaderTextView;

    private List<String> resources;

    public ResourcesView(Context context) {
        super(context);
    }

    public ResourcesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResourcesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
        updateViews();
    }

    private void updateViews() {
        if (resources.size() == 0) {
            resourcesHeaderTextView.setText(R.string.resources_none_msg);
        } else {
            resourcesHeaderTextView.setText(R.string.resources_general_msg);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
