package org.techconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.dialogs.GuideFeedbackDialogFragment;
import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dwalsten on 11/23/16.
 */

public class SessionActivity extends AppCompatActivity {

    public static String EXTRA_SESSION = "org.techconnect.sessionactivity.session";
    //Bind all of the editable text views relevant to the session
    @Bind(R.id.manufacturer_textView)
    TextView manufacturerTextView;
    @Bind(R.id.model_textView)
    TextView modelTextView;
    @Bind(R.id.serial_textView)
    TextView serialTextView;
    @Bind(R.id.date_textView)
    TextView dateTextView;
    @Bind(R.id.device_textView)
    TextView deviceTextView;
    @Bind(R.id.step_textView)
    TextView stepTextView;
    @Bind(R.id.notes_textView)
    TextView notesTextView;
    private Session session;

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
            manufacturerTextView.setText(session.getManufacturer());
            modelTextView.setText(session.getModelNumber());
            serialTextView.setText(session.getSerialNumber());
            dateTextView.setText(new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss").format(new Date(session.getCreatedDate())));
            deviceTextView.setText(session.getFlowchart().getName());
            stepTextView.setText(session.getCurrentVertex().getName());
            notesTextView.setText(session.getNotes());

        } else {
            //Not very clean at the moment
            manufacturerTextView.setVisibility(View.GONE);
            modelTextView.setVisibility(View.GONE);
            serialTextView.setVisibility(View.GONE);
            dateTextView.setVisibility(View.GONE);
            deviceTextView.setVisibility(View.GONE);
            stepTextView.setVisibility(View.GONE);
            notesTextView.setVisibility(View.GONE);
        }
    }

    public void resumeSession(View view) {
        FirebaseEvents.logResumeSession(this, session);
        Intent intent = new Intent(this, PlayGuideActivity.class);
        intent.putExtra(PlayGuideActivity.EXTRA_CHART_ID, session.getFlowchart().getId());
        intent.putExtra(PlayGuideActivity.EXTRA_SESSION, session.getId());//Let the next activity load in the session
        startActivity(intent);
    }

    public void deleteSession(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_session)
                .setMessage(R.string.confirm_delete_session)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseEvents.logDeleteSession(SessionActivity.this, session);
                        TCDatabaseHelper.get(SessionActivity.this).deleteSession(session);
                        dialog.dismiss();
                        GuideFeedbackDialogFragment frag = GuideFeedbackDialogFragment.newInstance(session);
                        frag.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                        frag.show(getFragmentManager(), "guide_feedback");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back arrow click here
        if (item.getItemId() == android.R.id.home) {
            //Want to go back to the list of past session
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
