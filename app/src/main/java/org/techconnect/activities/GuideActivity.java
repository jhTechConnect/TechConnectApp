package org.techconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.centum.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.model.FlowChart;
import org.techconnect.services.TCService;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.CommentsResourcesTabbedView;
import org.techconnect.views.ThumbFeedbackView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static String EXTRA_CHART = "org.techconnect.guideactivity.flowchart";
    public static String EXTRA_ALLOW_REFRESH = "org.techconnect.guideactivity.allow_refresh";

    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.header_imageView)
    ImageView headerImageView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.description_textView)
    TextView descriptionTextView;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    CommentsResourcesTabbedView commentsResourcesTabbedView;
    ThumbFeedbackView thumbFeedbackView;
    private FlowChart flowChart;
    private boolean inDB = true;
    private boolean downloadingChart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commentsResourcesTabbedView = (CommentsResourcesTabbedView) getLayoutInflater()
                .inflate(R.layout.comments_resources_tabbed_view, contentLinearLayout, false);
        thumbFeedbackView = (ThumbFeedbackView) getLayoutInflater().inflate(R.layout.view_thumbfeedback,contentLinearLayout,false);
        //Add Thumb up/down view prior to the tabbed view
        contentLinearLayout.addView(thumbFeedbackView);
        View ruler = new View(this);
        ruler.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        contentLinearLayout.addView(ruler,
                new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
        contentLinearLayout.addView(commentsResourcesTabbedView);
        swipeRefreshLayout.setOnRefreshListener(this);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getScrollY() == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHART)) {
            flowChart = getIntent().getParcelableExtra(EXTRA_CHART);
            FirebaseEvents.logViewGuide(this, flowChart);
            checkDBForFlowchart();
        }
        if (getIntent() != null && getIntent().hasExtra(EXTRA_ALLOW_REFRESH)) {
            swipeRefreshLayout.setEnabled(getIntent().getBooleanExtra(EXTRA_ALLOW_REFRESH, true));
        }

        //Use the flowchart to determine the number of up vs. down votes
        thumbFeedbackView.setUpCount(flowChart.getScore());
    }

    private void checkDBForFlowchart() {
        inDB = false;
        if (flowChart != null) {
            if (TCDatabaseHelper.get(this).hasChart(flowChart.getId())) {
                inDB = true;
            }
        }
        updateViews();
    }

    @OnClick(R.id.button)
    protected void onFabAction() {
        if (!downloadingChart) {
            if (inDB) {
                onPlay();
            } else {
                onRefresh();
            }
        }
    }

    private void updateViews() {
        if (flowChart != null) {
            setTitle(flowChart.getName());
        }
        if (inDB) {
            button.setText(R.string.start_session);
        } else {
            button.setText(R.string.download);
        }
        descriptionTextView.setText(flowChart.getDescription());
        commentsResourcesTabbedView.setItems(flowChart, flowChart.getResources(), flowChart.getId());
        updateHeaderImage();
    }

    private void updateHeaderImage() {
        if (flowChart.getImage() != null && !TextUtils.isEmpty(flowChart.getImage())) {
            if (ResourceHandler.get(this).hasStringResource(flowChart.getImage())) {
                // Load offline image
                Picasso.with(this)
                        .load(getFileStreamPath(
                                ResourceHandler.get(this).getStringResource(flowChart.getImage())))
                        .placeholder(R.drawable.progress_animation)
                        .into(headerImageView);
            } else {
                // Load online image
                Picasso.with(this)
                        .load(flowChart.getImage())
                        .placeholder(R.drawable.progress_animation)
                        .into(headerImageView);
            }
        } else {
            headerImageView.setVisibility(View.GONE);
        }
    }

    private void onPlay() {
        if (flowChart != null) {
            Intent intent = new Intent(this, PlayGuideActivity.class);
            intent.putExtra(PlayGuideActivity.EXTRA_CHART_ID, flowChart.getId());
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.header_imageView)
    protected void onHeaderImageAction() {
        Intent intent = new Intent(this, ImageViewActivity.class);
        if (ResourceHandler.get(this).hasStringResource(flowChart.getImage())) {
            intent.putExtra(ImageViewActivity.EXTRA_PATH, getFileStreamPath(
                    ResourceHandler.get(this).getStringResource(flowChart.getImage())).getAbsolutePath());
        } else {
            intent.putExtra(ImageViewActivity.EXTRA_URL, flowChart.getImage());
        }
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (flowChart != null && !downloadingChart) {
            swipeRefreshLayout.setRefreshing(true);
            downloadingChart = true;
            button.setText(R.string.updating);
            button.setEnabled(false);
            TCService.startLoadCharts(this, new String[]{flowChart.getId()}, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    downloadingChart = false;
                    button.setEnabled(true);
                    checkDBForFlowchart();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Guide Activity", "Pause");
        //Check to see whether the chart feedback changed during activity and update server
        //Use TCNetworkHelper.postFeedback(flowchart.getId(), vote, AuthManager.get(this).getAuth())
        // to post feedback. This function is likely going to change as we update the endpoints
    }
}
