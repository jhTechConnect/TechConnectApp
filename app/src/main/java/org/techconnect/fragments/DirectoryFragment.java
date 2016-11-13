package org.techconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import org.techconnect.adapters.UserListAdapter;
import org.techconnect.asynctasks.SearchUsersAsyncTask;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.UserListItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DirectoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.user_ListView)
    ListView userListView;
    @Bind(R.id.progress)
    ProgressBar progressBar;

    private UserListAdapter adapter;
    private boolean isLoading = false;

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
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = ((UserListItemView) view).getUser();
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onRefresh();
                return true;
            }
        });
        onRefresh();
        clearSearchImageView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            adapter.setUsers(new ArrayList<User>());
            progressBar.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
            isLoading = true;
            String query = searchEditText.getText().toString();
            if (query != null && !TextUtils.isEmpty(query)) {
                new SearchUsersAsyncTask(query, 10, 0) {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(List<User> users) {
                        if (users == null) {
                            adapter.setUsers(TCDatabaseHelper.get(getActivity()).getAllUsers());
                        } else {
                            adapter.setUsers(users);
                        }
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        userListView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    protected void onCancelled() {
                        isLoading = false;
                        userListView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }.execute();
            } else {
                adapter.setUsers(TCDatabaseHelper.get(getActivity()).getAllUsers());
                adapter.notifyDataSetChanged();
                isLoading = false;
                userListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_search_imageView) {
            searchEditText.setText(null);
            onRefresh();
        }
    }
}