package org.techconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.centum.techconnect.R;
import org.techconnect.networkhelper.model.FlowChart;
import org.techconnect.resources.ResourceHandler;
import org.techconnect.services.TechConnectService;
import org.techconnect.sql.TCDatabaseHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {

    public static String EXTRA_CHART_ID = "org.techconnect.guideactivity.flowchart";
    public static String EXTRA_CHART_NAME = "org.techconnect.guideactivity.name";
    public static String EXTRA_CHART_DESCRIPTION = "org.techconnect.guideactivity.description";
    public static String EXTRA_CHART_IMAGE = "org.techconnect.guideactivity.image";

    @Bind(R.id.download_fab)
    FloatingActionButton fab;
    @Bind(R.id.header_imageView)
    ImageView headerImageView;
    @Bind(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @Bind(R.id.downloaded_layout)
    LinearLayout downloadedLayout;
    @Bind(R.id.type_textView)
    TextView typeTextView;
    @Bind(R.id.score_textView)
    TextView scoreTextView;
    @Bind(R.id.version_textView)
    TextView versionTextView;
    @Bind(R.id.description_textView)
    TextView descriptionTextView;

    private FlowChart flowChart;
    private boolean inDB = true;
    private boolean downloadingChart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(this);
        headerImageView.setOnClickListener(this);
        reloadFlowchart();
    }

    private void onFabAction() {
        if (!downloadingChart) {
            if (inDB) {
                onPlay();
            } else {
                onDownload();
            }
        }
    }


    private void reloadFlowchart() {
        if (getIntent() != null) {
            String chartId = getIntent().getStringExtra(EXTRA_CHART_ID);
            flowChart = TCDatabaseHelper.get(this).getChart(chartId);
            if (flowChart == null) {
                // Not downloaded yet
                inDB = false;
                flowChart = new FlowChart();
                flowChart.setId(chartId);
                flowChart.setName(getIntent().getStringExtra(EXTRA_CHART_NAME));
                flowChart.setDescription(getIntent().getStringExtra(EXTRA_CHART_DESCRIPTION));
                flowChart.setImage(getIntent().getStringExtra(EXTRA_CHART_NAME));
            } else {
                inDB = true;
            }
            updateViews();
        }
    }

    private void updateViews() {
        if (flowChart != null) {
            setTitle(flowChart.getName());
        }
        if (inDB) {
            // This stuff is only available if it has been downloaded
            fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
            downloadedLayout.setVisibility(View.VISIBLE);
            typeTextView.setText(flowChart.getType().toString().toLowerCase());
            scoreTextView.setText(flowChart.getScore() + "");
            versionTextView.setText(flowChart.getVersion());
        } else {
            fab.setImageResource(R.drawable.ic_file_download_white_48dp);
            downloadedLayout.setVisibility(View.GONE);
        }
        descriptionTextView.setText(flowChart.getDescription());
        if (flowChart.getImage() != null && !TextUtils.isEmpty(flowChart.getImage())) {
            if (ResourceHandler.get(this).hasStringResource(flowChart.getImage())) {
                // Load offline image
                Picasso.with(this)
                        .load(getFileStreamPath(
                                ResourceHandler.get().getStringResource(flowChart.getImage())).getAbsolutePath())
                        .into(headerImageView);
            } else {
                // Load online image
                Picasso.with(this)
                        .load(flowChart.getImage())
                        .into(headerImageView);
            }
        }
    }

    private void onDownload() {
        if (flowChart != null) {
            downloadingChart = true;
            fab.setImageResource(R.drawable.ic_sync_white_48dp);
            fab.setEnabled(false);
            TechConnectService.startLoadCharts(this, new String[]{flowChart.getId()}, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    downloadingChart = false;
                    fab.setEnabled(true);
                    reloadFlowchart();
                }
            });
        }
    }

    private void onPlay() {

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

    @Override
    public void onClick(View view) {
        if (view.getId() == headerImageView.getId()) {
            onHeaderImageAction();
        } else if (view.getId() == fab.getId()) {
            onFabAction();
        }
    }

    private void onHeaderImageAction() {
        Intent intent = new Intent(this, ImageViewActivity.class);
        if (ResourceHandler.get().hasStringResource(flowChart.getImage())) {
            intent.putExtra(ImageViewActivity.EXTRA_PATH,
                    ResourceHandler.get(this).getStringResource(flowChart.getImage()));
        } else {
            intent.putExtra(ImageViewActivity.EXTRA_URL, flowChart.getImage());
        }
        startActivity(intent);
    }
}
