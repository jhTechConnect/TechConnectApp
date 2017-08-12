package org.techconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.techconnect.R;
import org.techconnect.adapters.SessionInfoAdapter;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.asynctasks.ExportResponsesAsyncTask;
import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dwalsten on 11/23/16.
 */

public class SessionActivity extends AppCompatActivity {

    public static String EXTRA_SESSION = "org.techconnect.sessionactivity.session";

    //Intent Result Types
    public static final int SESSION_DELETED = 2;
    public static final int SESSION_STABLE = 3;
    public static final int SESSION_RESUME = 1;

    //Intent Request Types
    private static final int EDIT_SESSION_REQUEST = 0;

    //Bind all of the editable text views relevant to the session
    @Bind(R.id.sessionScrollView)
    ScrollView sessionScrollView;
    @Bind(R.id.deviceListView)
    ListView deviceListView;
    @Bind(R.id.repairListView)
    ListView repairListView;

    @Bind(R.id.notes_textView)
    TextView notesTextView;
    @Bind(R.id.problem_textView)
    TextView problemTextView;
    @Bind(R.id.solution_textView)
    TextView solutionTextView;
    @Bind(R.id.resumeButton)
    Button resumeButton;

    private Session session;
    private boolean isEdited = false;
    //Define the fields for the listview for device and repair info
    private String[] deviceFields = {"Device","Manufacturer","Model Number","Serial Number"};
    private String[] repairPausedFields = {"Start Date", "Current Step"};
    private String[] repairCompleteFields = {"Start Date", "Finished Date"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);

        //Get the Session from the Intent
        if (getIntent() != null && getIntent().hasExtra(EXTRA_SESSION)) {
            this.session = getIntent().getParcelableExtra(EXTRA_SESSION);
        }
        updateViews();

        //Show the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void updateViews() {
        if (this.session != null) {
            //Setup device info map
            HashMap<String, String> deviceFieldMap = new HashMap<>();
            deviceFieldMap.put("Device",session.getDeviceName());
            deviceFieldMap.put("Manufacturer", session.getManufacturer());
            deviceFieldMap.put("Model Number", session.getModelNumber());
            deviceFieldMap.put("Serial Number", session.getSerialNumber());

            //Setup repair info map
            HashMap<String, String> repairFieldMap = new HashMap<>();
            repairFieldMap.put("Start Date", new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss").format(new Date(session.getCreatedDate())));
            if (!session.isFinished()) {
                if (session.hasChart()) {
                    repairFieldMap.put("Current Step", session.getCurrentVertex().getName());
                } else {
                    repairFieldMap.put("Current Step",String.format("%s Guide unavailable. Download guide to view current step",session.getDeviceName()));
                }
            } else {
                resumeButton.setVisibility(View.GONE);
                repairFieldMap.put("Finish Date", new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss").format(new Date(session.getFinishedDate())));
                //Update the height of the scrollview to fill the screen
                //ViewGroup.LayoutParams params = sessionScrollView.getLayoutParams();
                //params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                //sessionScrollView.setLayoutParams(params);
            }

            problemTextView.setText(session.getProblem());
            solutionTextView.setText(session.getSolution());
            notesTextView.setText(session.getNotes());

            //If active, hide the "Resume Session" button as this is not possible
            if(session.isFinished()) {
                resumeButton.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = sessionScrollView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                sessionScrollView.setLayoutParams(params);
            }

            SessionInfoAdapter deviceFieldAdapter = new SessionInfoAdapter(this,deviceFieldMap,deviceFields);
            SessionInfoAdapter repairFieldAdapter;
            if (session.isFinished()) {
                repairFieldAdapter = new SessionInfoAdapter(this, repairFieldMap, repairCompleteFields);
            } else {
                repairFieldAdapter = new SessionInfoAdapter(this, repairFieldMap, repairPausedFields);
            }

            deviceListView.setAdapter(deviceFieldAdapter);
            repairListView.setAdapter(repairFieldAdapter);

            setListViewHeightBasedOnChildren(deviceListView);
            setListViewHeightBasedOnChildren(repairListView);


        } else {
            //Not very clean at the moment
            notesTextView.setVisibility(View.GONE);
        }
    }

    public void resumeSession(View view) {
        if (session.hasChart()) {
            FirebaseEvents.logResumeSession(this, session);
            Intent intent = new Intent(this, PlayGuideActivity.class);
            intent.putExtra(PlayGuideActivity.EXTRA_SESSION, session);//Let the next activity load in the session
            //Need another intent for result
            Intent resultIntent = new Intent();
            setResult(SESSION_RESUME, resultIntent);
            startActivity(intent);
        } else {
            Toast.makeText(this,String.format("%s guide has been deleted. Cannot resume session",session.getDeviceName()),Toast.LENGTH_LONG).show();
        }
    }

    public void deleteSession() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_session)
                .setMessage(R.string.confirm_delete_session)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseEvents.logDeleteSession(SessionActivity.this, session);
                        TCDatabaseHelper.get(SessionActivity.this).deleteSession(session);
                        dialog.dismiss();
                        /*
                        GuideFeedbackDialogFragment frag = GuideFeedbackDialogFragment.newInstance(session);
                        frag.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                        frag.show(getFragmentManager(), "guide_feedback");
                        */
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onResume() {
        //Reload the session from the Database, hopefull don't need a cursor loader
        super.onResume();
        session = TCDatabaseHelper.get(this).getSession(session.getId());
        updateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_session_toolbar, menu);
        MenuItem item = menu.findItem(R.id.actionSend);
        item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back arrow click here
        switch(item.getItemId()) {
            case android.R.id.home:
                //Want to go back to the list of past sessions
                Intent resultIntent = new Intent();
                if (!isEdited) {
                    setResult(SESSION_STABLE, resultIntent);
                } else {
                    setResult(SESSION_RESUME,resultIntent);//The session was changed
                }
                finish();
                break;
            case R.id.actionSend:
                //Start an intent to send a message with question/response data
                if (session.hasChart()) {
                    new ExportResponsesAsyncTask(this).execute(session.getId());
                    FirebaseEvents.logContactExpertFromHistory(this,session);
                } else {
                    Toast.makeText(this, String.format("%s guide has been deleted. Cannot send completed steps", session.getDeviceName()), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.deleteSession:
                deleteSession(); //Delete the stored session
                break;
            case R.id.editSession:
                //Open up the edit session activity
                Intent intent = new Intent(SessionActivity.this, EditSessionActivity.class);
                intent.putExtra(EditSessionActivity.EXTRA_SESSION,session);
                startActivityForResult(intent,EDIT_SESSION_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_SESSION_REQUEST) {
            if (resultCode == EditSessionActivity.SESSION_CHANGE) {
                //Need to refresh the textviews
                if (data.hasExtra(EXTRA_SESSION)) {
                    this.session = data.getParcelableExtra(EXTRA_SESSION);
                    updateViews();
                    isEdited = true; //Store that the session was changed
                } else {
                    //Catch error
                }
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
