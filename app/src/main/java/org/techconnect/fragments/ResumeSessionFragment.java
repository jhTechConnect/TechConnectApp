package org.techconnect.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.activities.ProfileActivity;
import org.techconnect.adapters.SessionCursorAdapter;
import org.techconnect.adapters.UserListAdapter;
import org.techconnect.asynctasks.SearchUsersAsyncTask;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.UserListItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResumeSessionFragment extends Fragment implements
        View.OnClickListener,
        TextWatcher {

    //All of the binds

    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.session_ListView)
    ListView sessionListView;

    private SessionCursorAdapter adapter;
    private boolean isLoading = false;

    public ResumeSessionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume_session, container, false);
        ButterKnife.bind(this, view);
        Log.d("Directory Setup", "View Initialized");
        adapter = new SessionCursorAdapter(getContext());
        sessionListView.setAdapter(adapter);
        searchEditText.addTextChangedListener(this);
        onRefresh();
        clearSearchImageView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.resume_session);
        }
        onRefresh();
    }

    public void onRefresh() {
        //Somehow reset the SQL cursor? TBD
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
}