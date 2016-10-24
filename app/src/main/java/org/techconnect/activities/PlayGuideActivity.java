package org.techconnect.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.centum.techconnect.R;
import org.techconnect.model.FlowChart;
import org.techconnect.model.session.Session;
import org.techconnect.model.session.SessionCompleteListener;
import org.techconnect.services.TCService;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.GuideFlowView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayGuideActivity extends AppCompatActivity implements SessionCompleteListener {

    public static final String EXTRA_CHART_ID = "org.techconnect.playguide.chartid";
    private static final int LAYOUT_INFO = 0;
    private static final int LAYOUT_FLOW = 1;
    private static final int LAYOUT_ERROR = 2;

    @Bind(R.id.flowContainer)
    FrameLayout flowContainer;
    @Bind(R.id.errorLayout)
    LinearLayout errorLayout;
    @Bind(R.id.session_info_layout)
    ScrollView sessionInfoLayout;

    @Bind(R.id.sync_button)
    Button syncButton;
    @Bind(R.id.department_editText)
    EditText departmentEditText;
    @Bind(R.id.model_editText)
    EditText modelEditText;
    @Bind(R.id.serial_editText)
    EditText serialEditText;
    @Bind(R.id.notes_editText)
    EditText notesEditText;

    private GuideFlowView flowView;
    private FlowChart flowChart = null;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_guide);
        ButterKnife.bind(this);
        flowView = (GuideFlowView) LayoutInflater.from(this).inflate(R.layout.guide_flow_view, flowContainer, false);
        flowContainer.addView(flowView);
        loadFlowchart();
    }

    @Override
    public void onBackPressed() {
        if (session != null && flowView.goBack()) {
            return;
        }
        if (session != null) {
            showEndSessionDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showEndSessionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("End Session")
                .setMessage("Are you sure you want to end the session?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onSessionComplete();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void saveSession() {
        // TODO save session
    }

    private void loadFlowchart() {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHART_ID)) {
            flowChart = TCDatabaseHelper.get(this).getChart(getIntent().getStringExtra(EXTRA_CHART_ID));
        }
        updateViews();
    }

    private void updateViews() {
        if (flowChart == null) {
            setVisibleLayout(LAYOUT_ERROR);
            syncButton.setEnabled(true);
        } else if (session == null) {
            setVisibleLayout(LAYOUT_INFO);
            setTitle(flowChart.getName());
        } else {
            setVisibleLayout(LAYOUT_FLOW);
            setTitle(flowChart.getName());
        }
    }

    @OnClick(R.id.start_button)
    protected void onStartSession() {
        if (flowChart != null) {
            session = new Session(flowChart);
            session.setDepartment(departmentEditText.getText().toString());
            session.setModelNumber(modelEditText.getText().toString());
            session.setSerialNumber(serialEditText.getText().toString());
            session.setNotes(notesEditText.getText().toString());
            session.setCreatedDate(System.currentTimeMillis());
            flowView.setSession(session, this);
            updateViews();
        }
    }

    @OnClick(R.id.sync_button)
    protected void onSync() {
        // Ideally this should never happen
        syncButton.setEnabled(false);
        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHART_ID)) {
            TCService.startLoadCharts(this,
                    new String[]{getIntent().getStringExtra(EXTRA_CHART_ID)},
                    new ResultReceiver(new Handler()) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            loadFlowchart();
                        }
                    });
        }
    }

    private void setVisibleLayout(int layout) {
        flowContainer.setVisibility(layout == LAYOUT_FLOW ? View.VISIBLE : View.GONE);
        sessionInfoLayout.setVisibility(layout == LAYOUT_INFO ? View.VISIBLE : View.GONE);
        errorLayout.setVisibility(layout == LAYOUT_ERROR ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSessionComplete() {
        saveSession();
        // TODO show some message
        finish();
    }
}

