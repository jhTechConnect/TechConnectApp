package org.techconnect.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.centum.techconnect.R;
import org.techconnect.activities.GetGuidesActivity;
import org.techconnect.activities.GuideActivity;
import org.techconnect.adapters.FlowchartCursorAdapter;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.GuideListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GuidesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, TextWatcher, FilterQueryProvider {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.guides_listView)
    ListView guidesListView;
    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.download_floatingActionButton)
    FloatingActionButton downloadFab;
    @Bind(R.id.download_guides_button)
    Button downloadGuidesButton;
    @Bind(R.id.no_guides_layout)
    ViewGroup noGuidesLayout;
    @Bind(R.id.search_linearLayout)
    LinearLayout searchLinearLayout;

    private FlowchartCursorAdapter adapter;

    public GuidesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guides, container, false);
        ButterKnife.bind(this, view);
        adapter = new FlowchartCursorAdapter(getContext());
        adapter.setFilterQueryProvider(this);
        guidesListView.setAdapter(adapter);
        searchEditText.addTextChangedListener(this);
        clearSearchImageView.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        downloadFab.setOnClickListener(this);
        downloadGuidesButton.setOnClickListener(this);
        guidesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GuideListItemView guideView = ((GuideListItemView) view);
                Intent intent = new Intent(getContext(), GuideActivity.class);
                intent.putExtra(GuideActivity.EXTRA_CHART, guideView.getFlowChart());
                startActivity(intent);
            }
        });
        guidesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                int topRowVerticalPosition =
                        (guidesListView == null || guidesListView.getChildCount() == 0) ?
                                0 : guidesListView.getChildAt(0).getTop();
                refreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        noGuidesLayout.setVisibility(View.GONE);
        guidesListView.setVisibility(View.VISIBLE);
        searchLinearLayout.setVisibility(View.VISIBLE);
        adapter.changeCursor(TCDatabaseHelper.get(getContext()).getAllFlowchartsCursor(searchEditText.getText().toString()));
        if (adapter.getCount() == 0) {
            noGuidesLayout.setVisibility(View.VISIBLE);
            guidesListView.setVisibility(View.GONE);
            searchLinearLayout.setVisibility(View.GONE);
        }
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_search_imageView) {
            searchEditText.setText(null);
        } else if (view.getId() == R.id.download_floatingActionButton ||
                view.getId() == R.id.download_guides_button) {
            onDownloadGuides();
        }
    }

    private void onDownloadGuides() {
        startActivity(new Intent(getActivity(), GetGuidesActivity.class));
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
    public Cursor runQuery(CharSequence charSequence) {
        return TCDatabaseHelper.get(getContext()).getAllFlowchartsCursor(charSequence.toString());
    }
}
