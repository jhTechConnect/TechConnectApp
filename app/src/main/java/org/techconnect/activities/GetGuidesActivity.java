package org.techconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.centum.techconnect.R;
import org.techconnect.adapters.FlowchartAdapter;
import org.techconnect.adapters.FlowchartCursorAdapter;
import org.techconnect.asynctasks.GetCatalogAsyncTask;
import org.techconnect.model.FlowChart;
import org.techconnect.views.GuideListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GetGuidesActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.session_info_layout)
    ViewGroup contentLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.try_again_button)
    Button tryAgainButton;
    @Bind(R.id.failedContentLayout)
    ViewGroup failedContentLayout;
    @Bind(R.id.guides_listView)
    ListView guidesListView;

    private ListAdapter adapter;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_guides);
        ButterKnife.bind(this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCatalog();
            }
        });
        failedContentLayout.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tryAgainButton.setOnClickListener(this);
        guidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GuideListItemView guideView = ((GuideListItemView) view);
                Intent intent = new Intent(GetGuidesActivity.this, GuideActivity.class);
                intent.putExtra(GuideActivity.EXTRA_CHART_ID, guideView.getFlowChart().getId());
                intent.putExtra(GuideActivity.EXTRA_CHART_NAME, guideView.getFlowChart().getName());
                intent.putExtra(GuideActivity.EXTRA_CHART_DESCRIPTION, guideView.getFlowChart().getDescription());
                intent.putExtra(GuideActivity.EXTRA_CHART_IMAGE, guideView.getFlowChart().getImage());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loaded) {
            loadCatalog();
        }
    }

    private void loadCatalog() {
        contentLayout.setVisibility(View.GONE);
        failedContentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        new GetCatalogAsyncTask() {
            @Override
            protected void onPostExecute(FlowChart[] flowCharts) {
                progressBar.setVisibility(View.GONE);
                if (flowCharts == null) {
                    failedContentLayout.setVisibility(View.VISIBLE);
                } else {
                    setCatalog(flowCharts);
                }
            }
        }.execute();
    }

    private void setCatalog(FlowChart[] flowCharts) {
        loaded = true;
        adapter = new FlowchartAdapter(this, flowCharts);
        guidesListView.setAdapter(adapter);
        failedContentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        ((FlowchartCursorAdapter) guidesListView.getAdapter()).getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.try_again_button) {
            loadCatalog();
        }
    }
}
