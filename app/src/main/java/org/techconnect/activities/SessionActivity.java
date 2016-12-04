package org.techconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.centum.techconnect.R;
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

    //Bind all of the editable text views relevant to the session
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

    public static String EXTRA_SESSION = "org.techconnect.sessionactivity.session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);

        //Get the Session from the Intent
        if(getIntent() != null && getIntent().hasExtra(EXTRA_SESSION)) {
            this.session = getIntent().getParcelableExtra(EXTRA_SESSION);
        }
        updateViews();
    }


    private void updateViews() {
        if (this.session != null) {
            modelTextView.setText(session.getModelNumber());
            serialTextView.setText(session.getSerialNumber());
            dateTextView.setText(new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss").format(new Date(session.getCreatedDate())));
            deviceTextView.setText(session.getFlowchart().getName());
            stepTextView.setText(session.getCurrentVertex().getName());
            notesTextView.setText(session.getNotes());

        } else {
            //Not very clean at the moment
            modelTextView.setVisibility(View.GONE);
            serialTextView.setVisibility(View.GONE);
            dateTextView.setVisibility(View.GONE);
            deviceTextView.setVisibility(View.GONE);
            stepTextView.setVisibility(View.GONE);
            notesTextView.setVisibility(View.GONE);
        }
    }
    public void resumeSession(View view) {
        Intent intent = new Intent(this, PlayGuideActivity.class);
        intent.putExtra(PlayGuideActivity.EXTRA_CHART_ID, session.getFlowchart().getId());
        intent.putExtra(PlayGuideActivity.EXTRA_SESSION,session.getId());//Let the next activity load in the session
        startActivity(intent);
    }

    public void deleteSession(View view) {
        //Have a pop up to confirm deletion

        //Simply want to remove the session stored in this object from the SQL database
        TCDatabaseHelper.get(this).deleteSession(session);
        //End this activity and return to previous
        finish();
    }
}
