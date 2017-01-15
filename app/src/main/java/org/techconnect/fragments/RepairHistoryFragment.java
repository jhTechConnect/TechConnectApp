package org.techconnect.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.activities.MainActivity;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Used to facilitate accessing the repair history stored in the phone
 */
public class RepairHistoryFragment extends Fragment implements
        View.OnClickListener,
        TextWatcher {

    //Used to select the base for the adapter of the ListView
    private static int SELECTION_DATE = 0;
    private static int SELECTION_DEVICE = 1;
    //All of the binds
    @Bind(R.id.categoryButton)
    ImageButton categoryButton;
    @Bind(R.id.categoryTextView)
    TextView categoryTextView;
    @Bind(R.id.categoryListView)
    ListView categoryListView;

    //Storage for list data
    private Map<String, Integer> deviceCounts = new HashMap<String,Integer>();
    private Map<String, Integer> dateCounts = new HashMap<String,Integer>();

    public RepairHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_repair_history, container, false);
        ButterKnife.bind(this, view);

        //Load the map of Name -> Id
        Map<String,String> device_map = TCDatabaseHelper.get(this.getContext()).getChartNamesAndIDs();
        //Determine the number of sessions associated with each device
        for (String dev : device_map.keySet()) {
            int count = TCDatabaseHelper.get(this.getContext()).getSessionsChartCount(device_map.get(dev));
            deviceCounts.put(dev,count);
            Log.d("Repair History", String.format("Device: %s, Count: %d", dev, count ));
        }
        //Determine months/years available in the session database
        dateCounts = TCDatabaseHelper.get(this.getContext()).getSessionDatesCounts();
        for (String comb : dateCounts.keySet()) {
            Log.d("Repair History", String.format("Date: %s, Count: %d",comb,dateCounts.get(comb)));
        }

        //Design an adpater to use a map<String, Integer> to make a ListView of the format desired
        ((MainActivity) getActivity()).getDateAdapter().setBaseMap(dateCounts);
        ((MainActivity) getActivity()).getDeviceAdapter().setBaseMap((deviceCounts));


        //Setup the ListView w/ adapter
        categoryListView.setAdapter(((MainActivity) getActivity()).getDateAdapter());

        setHasOptionsMenu(true);
        Log.d("Repair History Setup", "View Initialized");

        return view;
    }

    @Override
    public void onResume() {
        Log.d("Resume Session", "Resume Fragment");
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.repair_history);
        }
        onRefresh();
    }


    public void onRefresh() {
        Log.d("Resume Session", "Refresh Session List");
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        onRefresh();
    }

    public void setAdapter(ListAdapter a) {
        categoryListView.setAdapter(a);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_sort);
        item.setVisible(true);
        //Initially, will have date be the initial way to sort the sessions
        item.getSubMenu().findItem(R.id.date_item).setChecked(true);
    }
}

