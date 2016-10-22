package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import org.centum.techconnect.R;
import org.techconnect.model.Session;
import org.techconnect.networkhelper.model.FlowChart;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 1/26/2016.
 * <p/>
 * The view to start a session, after getting input from the user.
 */
public class SelfHelpIntroView extends ScrollView implements View.OnClickListener {

    @Bind(R.id.department_editText)
    EditText departmentEditText;
    @Bind(R.id.device_spinner)
    Spinner deviceSpinner;
    @Bind(R.id.notes_editText)
    EditText notesEditText;
    @Bind(R.id.start_session_button)
    Button startButton;

    private OnClickListener clickListener;
    private FlowChart selectedFlowchart;
    private Session session;


    public SelfHelpIntroView(Context context) {
        super(context);
    }

    public SelfHelpIntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfHelpIntroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSessionCreatedListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    public Session getSession() {
        return session;
    }

    private void updateFlowchartSpinner() {
        final Map<String, String> names = TCDatabaseHelper.get().getChartNames();
        final String deviceNames[] = names.keySet().toArray(new String[names.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, deviceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(adapter);
        deviceSpinner.setSelection(0);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFlowchart = TCDatabaseHelper.get().getChart(names.get(deviceNames[i]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedFlowchart = null;
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        startButton.setOnClickListener(this);
        final String levels[] = new String[Session.Urgency.values().length];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = Session.Urgency.values()[i].name();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void onClick(View view) {
        //Attempt to start
        if (validate() && clickListener != null) {
            session = new Session();
            session.setCreatedDate(System.currentTimeMillis());
            session.setDepartment(departmentEditText.getText().toString());
            session.setFlowchart(selectedFlowchart);
            session.setNotes(notesEditText.getText().toString());
            clickListener.onClick(this);
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (deviceSpinner.getSelectedItem() == null) {
            valid = false;
        }
        if (departmentEditText.getText() == null
                || departmentEditText.getText().toString().trim().equals("")) {
            departmentEditText.setError("Department cannot be empty");
            valid = false;
        } else {
            departmentEditText.setError(null);
        }
        return valid;
    }

    public void update() {
        updateFlowchartSpinner();
    }
}
