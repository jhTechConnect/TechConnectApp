package org.techconnect.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.techconnect.R;
import org.techconnect.adapters.SessionCursorAdapter;
import org.techconnect.asynctasks.PostQuestionAsyncTask;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactAnExpertActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //Loader Types
    public static final int SESSION_ACTIVE_LOADER = 2;

    @Bind(R.id.sessionListView)
    ListView sessionListView;
    @Bind(R.id.progressBar)
    ProgressBar progress;
    @Bind(R.id.emptyTextView)
    TextView emptyTextView;
    @Bind(R.id.negativeButton)
    Button negativeButton;
    @Bind(R.id.positiveButton)
    Button positiveButton;

    SessionCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_an_expert);
        ButterKnife.bind(this);

        negativeButton.setOnClickListener(this);
        positiveButton.setOnClickListener(this);
        mAdapter = new SessionCursorAdapter(this, true);
        sessionListView.setAdapter(mAdapter);
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if (((SessionListItemView) view).isChecked()) {
                   ((SessionListItemView) view).setChecked(false);
               } else {
                   ((SessionListItemView) view).setChecked(true);
               }
               view.invalidate();
           }
        });
        getSupportLoaderManager().initLoader(SESSION_ACTIVE_LOADER, null, this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.positiveButton) {
            //Determine which sessions have been clicked within the listview
            ArrayList<String> ids = new ArrayList<String>();
            for (int i = 0; i < sessionListView.getChildCount(); i++) {
                SessionListItemView v = (SessionListItemView) sessionListView.getChildAt(i);
                if (v.isChecked()) {
                    ids.add(v.getSession().getId());//Add to array of IDs to include include
                }
            }
            //Use PostQuestionAsyncTask to open gmail
            String[] ids_array = new String[ids.size()];
            ids_array = ids.toArray(ids_array);
            new PostQuestionAsyncTask(this).execute(ids_array);
            finish();
        } else if (view.getId() == R.id.negativeButton) {
            //Cancl, terminate activity
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SESSION_ACTIVE_LOADER) {
            Log.d("SessionList Activity", "Initiate Cursor Loader for DATE");
            return TCDatabaseHelper.get(this).getActiveSessionsCursorLoader();
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mAdapter.notifyDataSetChanged();
        Runnable r;
        r = new Runnable() {

            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                if (mAdapter.getCount() > 0) {
                    sessionListView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                } else {
                    sessionListView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 500);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        progress.setVisibility(View.VISIBLE);
        sessionListView.setVisibility(View.GONE);
    }
}
