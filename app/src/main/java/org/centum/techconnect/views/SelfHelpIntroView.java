package org.centum.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.java.model.FlowChart;

import org.centum.techconnect.R;
import org.centum.techconnect.model.Session;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 1/26/2016.
 * <p/>
 * The view to start a session, after getting input from the user.
 */
public class SelfHelpIntroView extends ScrollView implements View.OnClickListener {

    private static final int ROLE_TECH = 0;
    private static final int ROLE_END = 1;

    @Bind(R.id.department_editText)
    EditText departmentEditText;
    @Bind(R.id.device_spinner)
    Spinner deviceSpinner;
    @Bind(R.id.role_spinner)
    Spinner roleSpinner;
    @Bind(R.id.notes_editText)
    EditText notesEditText;
    @Bind(R.id.start_session_button)
    Button startButton;
    @Bind(R.id.devices_available_text_view)
    TextView devicesAvailableLabel;

    //private Device[] devices;
    private FlowChart[] devices;
    private OnClickListener clickListener;
    //private Device selectedDevice;
    private FlowChart selectedDevice;
    private int selectedRole;
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

    public void setDevices(FlowChart[] devices) {
        this.devices = devices;
        updateDeviceSpinner();
    }

    public Session getSession() {
        return session;
    }

    private void updateDeviceSpinner() {
        String deviceNames[] = new String[devices.length];
        for (int i = 0; i < devices.length; i++) {
            deviceNames[i] = devices[i].getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, deviceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(adapter);
        deviceSpinner.setSelection(0);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDevice = devices[i];
                updateProblemSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedDevice = null;
                selectedRole = -1;
                roleSpinner.setAdapter(null);
            }
        });
        devicesAvailableLabel.setText(devices.length + " " + getResources().getString(R.string.devices_available));
    }

    private void updateProblemSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Biomedical Technician", "Clinician/End User"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setSelection(0);
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRole = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedRole = -1;
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
            session.setDevice(selectedDevice);
            session.setNotes(notesEditText.getText().toString());
            //session.setRole(selectedRole == ROLE_TECH ? selectedDevice.getTechRole() : selectedDevice.getEndUserRole());
            clickListener.onClick(this);
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (departmentEditText.getText() == null
                || departmentEditText.getText().toString().trim().equals("")) {
            departmentEditText.setError("Department cannot be empty");
            valid = false;
        } else {
            departmentEditText.setError(null);
        }
        return valid;
    }
}
