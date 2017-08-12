package org.techconnect.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techconnect.R;
import org.techconnect.activities.PDFActivity;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.misc.Utils;

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
    private String parentChart = "";

    public ResourcesView(Context context) {
        super(context);
        //init();
    }

    public ResourcesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public ResourcesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init();
    }

    private void init() {
        inflate(getContext(), R.layout.resources_view, this);
        ButterKnife.bind(this);

    }
    public void setResources(List<String> resources) {
        this.resources = resources;
        updateViews();
    }

    private void updateViews() {
        removeAllViews();
        addView(resourcesHeaderTextView);
        if (resources.size() == 0) {
            resourcesHeaderTextView.setText(R.string.resources_none_msg);
        } else {
            resourcesHeaderTextView.setText(R.string.resources_general_msg);
        }

        for (final String att : resources) {
            final String name = Utils.formatAttachmentName(att);
            Button button = new Button(getContext());
            button.setTransformationMethod(null);
            button.setText(name);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Activate a Firebase event which reports parent flowchart and attachment
                    FirebaseEvents.logViewResource(getContext(),parentChart,att);
                    openAttachment(att,name);
                }
            });
            addView(button);
        }
    }

    private void openAttachment(String att, String name) {
        Intent intent = new Intent(getContext(), PDFActivity.class);
        intent.putExtra(PDFActivity.EXTRA_IS_FILE, true);
        if (ResourceHandler.get(getContext()).hasStringResource(att)) {
            intent.putExtra(PDFActivity.EXTRA_FILE, getContext()
                    .getFileStreamPath(ResourceHandler.get(getContext()).getStringResource(att))
                    .getAbsolutePath());
        } else {
            intent.putExtra(PDFActivity.EXTRA_FILE, "");
        }
        intent.putExtra(PDFActivity.EXTRA_FILENAME,name);
        getContext().startActivity(intent);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public String getParentChart() {
        return parentChart;
    }

    public void setParentChart(String parentChart) {
        this.parentChart = parentChart;
    }
}
