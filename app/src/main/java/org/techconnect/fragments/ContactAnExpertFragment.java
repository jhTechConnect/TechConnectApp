package org.techconnect.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.techconnect.R;
import org.techconnect.adapters.ContactExpertListAdapter;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.asynctasks.ExportResponsesAsyncTask;
import org.techconnect.asynctasks.LoadActiveSessionsAsyncTask;
import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ContactAnExpertFragment extends Fragment implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    //Loader Types
    public static final int SESSION_ACTIVE_LOADER = 2;

    @Bind(R.id.sessionListView)
    ListView sessionListView;
    @Bind(R.id.progressBar)
    ProgressBar progress;
    @Bind(R.id.emptyTextView)
    TextView emptyTextView;
    @Bind(R.id.clearButton)
    Button clearButton;
    @Bind(R.id.sendButton)
    Button sendButton;

    private ContactExpertListAdapter mAdapter;
    private int mPosition = -1;

    public ContactAnExpertFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.contact_an_expert);
            FirebaseAnalytics.getInstance(getContext()).setCurrentScreen(getActivity(),null,"ContactFragment");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact_an_expert, container, false);
        ButterKnife.bind(this, view);

        sendButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        LoadActiveSessionsAsyncTask task = new LoadActiveSessionsAsyncTask(getContext()) {
            @Override
            protected void onPostExecute(List<Session> sessions) {
                progress.setVisibility(View.GONE);
                mAdapter = new ContactExpertListAdapter(getContext(),sessions);
                sessionListView.setAdapter(mAdapter);
                sessionListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                if (mAdapter.getCount() > 0) {
                    sessionListView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                } else {
                    sessionListView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        };
        task.execute();
        /*
        HashMap<String, String> sessionInfo = new HashMap<>();
        String[] header = new String[sessions.size()];
        for (int i = 0; i < sessions.size(); i++) {
            header[i] = String.format("%s,%s,%s",sessions.get(i).getModelNumber(),sessions.get(i).getSerialNumber(),
                    new SimpleDateFormat("MM/dd/yyyy").format(new Date(sessions.get(i).getCreatedDate())));
            sessionInfo.put(header[i],String.format("%s,%s", sessions.get(i).getDeviceName(), sessions.get(i).getManufacturer()));
        }
        */
        //mAdapter = new ContactExpertListAdapter(getContext(),sessions);
        //sessionListView.setAdapter(mAdapter);
        //sessionListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        //getActivity().getSupportLoaderManager().initLoader(SESSION_ACTIVE_LOADER, null, this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendButton) {
            //Determine which sessions have been clicked within the listview
            int index = -1;
            for (int i = 0; i < sessionListView.getChildCount(); i++) {
                if (((SessionListItemView) sessionListView.getChildAt(i)).isChecked()) {
                    index = i;
                }
            }
            Log.d(getClass().toString(),String.format("%d",index));
            if (index != -1) {
                String id = ((SessionListItemView) sessionListView.getChildAt(index)).getSession().getId();
                new ExportResponsesAsyncTask(getContext()).execute(id);
                FirebaseEvents.logContactExpertFromFragment(getContext());
            } else {
                new ExportResponsesAsyncTask(getContext()).execute();
                FirebaseEvents.logContactExpertFromFragment(getContext());
            }
        } else if (view.getId() == R.id.clearButton) {
            sessionListView.clearChoices();
            sessionListView.requestLayout();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SESSION_ACTIVE_LOADER) {
            Log.d("SessionList Activity", "Initiate Cursor Loader for DATE");
            return TCDatabaseHelper.get(getContext()).getActiveSessionsCursorLoader();
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //mAdapter.swapCursor(data);
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
