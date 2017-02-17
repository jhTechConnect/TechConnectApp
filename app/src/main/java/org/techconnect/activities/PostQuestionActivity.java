package org.techconnect.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.centum.techconnect.R;
import org.techconnect.adapters.SessionCursorAdapter;
import org.techconnect.sql.TCDatabaseHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostQuestionActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //Loader Types
    public static final int SESSION_ACTIVE_LOADER = 2;

    @Bind(R.id.sessionListView)
    ListView sessionListView;
    @Bind(R.id.progressBar)
    ProgressBar progress;
    @Bind(R.id.negativeButton)
    Button negativeButton;
    @Bind(R.id.positiveButton)
    Button positiveButton;

    SessionCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        ButterKnife.bind(this);

        negativeButton.setOnClickListener(this);
        positiveButton.setOnClickListener(this);
        mAdapter = new SessionCursorAdapter(this,true);
        sessionListView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(SESSION_ACTIVE_LOADER,null,this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.positiveButton) {
            //Continue to email
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message");
            //Setup the email a bit further
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
                sessionListView.setVisibility(View.VISIBLE);
            }
        };
        Handler h = new Handler();
        h.postDelayed(r,500);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        progress.setVisibility(View.VISIBLE);
        sessionListView.setVisibility(View.GONE);
    }
}
