package org.techconnect.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.centum.techconnect.R;
import org.techconnect.activities.SessionActivity;
import org.techconnect.adapters.SessionCursorAdapter;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResumeSessionFragment extends Fragment implements
        View.OnClickListener,
        TextWatcher,
        LoaderManager.LoaderCallbacks<Cursor> {

    //All of the binds

    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.session_ListView)
    ListView sessionListView;

    //Loader options
    private static final int SESSION_LOADER = 0;

    private SessionCursorAdapter adapter;
    private boolean isLoading = false;


    public ResumeSessionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_resume_session,container,false);
        ButterKnife.bind(this,view);
        Log.d("Directory Setup", "View Initialized");
        getLoaderManager().initLoader(SESSION_LOADER, null, this);
        adapter = new SessionCursorAdapter(this.getContext());
        sessionListView.setAdapter(adapter);
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SessionListItemView sessionView = ((SessionListItemView) view);
                Intent intent = new Intent(getContext(), SessionActivity.class);
                // Get the non-stub chart and open
                intent.putExtra(SessionActivity.EXTRA_SESSION,
                        sessionView.getSession()); //Maybe? Not sure if this is a good idea
                startActivity(intent);

            }
        });
        searchEditText.addTextChangedListener(this);
        onRefresh();
        clearSearchImageView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        Log.d("Resume Session","Resume Fragment");
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.resume_session);
        }
        onRefresh();
    }

    public void onRefresh() {
        Log.d("Resume Session","Refresh Session List");
        if (getActivity() != null) {
            getLoaderManager().restartLoader(SESSION_LOADER, null, this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_search_imageView) {
            searchEditText.setText(null);
            onRefresh();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        onRefresh();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SESSION_LOADER) {
            Log.d("Resume Session","Initiate Cursor Loader");
            return TCDatabaseHelper.get(this.getContext()).getActiveSessionsCursorLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
        Log.d("Resume Session",String.format("Update Adapter, %d",adapter.getCount()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}