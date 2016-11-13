package org.techconnect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.centum.techconnect.R;
import org.techconnect.adapters.UserListAdapter;
import org.techconnect.asynctasks.SearchUsersAsyncTask;
import org.techconnect.model.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DirectoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, TextWatcher {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.search_linearLayout)
    LinearLayout searchLinearLayout;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.user_ListView)
    ListView userListView;

    UserListAdapter adapter;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        ButterKnife.bind(this, view);
        Log.d("Directory Setup", "View Initialized");
        adapter = new UserListAdapter();
        userListView.setAdapter(adapter);
        updateUsers(null); //Want the full list of users initially
        searchEditText.addTextChangedListener(this);
        clearSearchImageView.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);
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
        searchLinearLayout.setVisibility(View.VISIBLE);
        updateUsers(searchEditText.getText().toString());
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_search_imageView) {
            searchEditText.setText(null);
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
        //Here, instead search online
        updateUsers(editable.toString());
    }

    private void updateUsers(String filter) {

        new SearchUsersAsyncTask(filter, 10, 0) {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (users == null) {
                    Log.e("Directory Setup", "Null list of users");
                }
                System.out.println(users.size());
                adapter.setUsers(users);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

}