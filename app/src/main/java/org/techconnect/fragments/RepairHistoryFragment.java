package org.techconnect.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.adapters.CategoryListAdapter;
import org.techconnect.adapters.SessionCursorAdapter;
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
        TextWatcher,
        LoaderManager.LoaderCallbacks<Cursor> {

    //Loader Types
    private static final int SESSION_DATE_LOADER = 0;
    private static final int SESSION_DEVICE_LOADER = 1;

    //All of the binds
    @Bind(R.id.categoryButton)
    ImageButton categoryButton;
    @Bind(R.id.categoryTextView)
    TextView categoryTextView;
    @Bind(R.id.categoryListView)
    ListView categoryListView;

    //Adapters
    private SessionCursorAdapter sessionAdapter;
    private CategoryListAdapter dateAdapter = new CategoryListAdapter();
    private CategoryListAdapter deviceAdapter = new CategoryListAdapter();
    private boolean categoryState = true; //True == Date, False == Device

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

        //Startup the SessionCursor
        sessionAdapter = new SessionCursorAdapter(this.getContext());
        //Load the map of Name -> Id
        final Map<String,String> device_map = TCDatabaseHelper.get(this.getContext()).getChartNamesAndIDs();
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
        dateAdapter.setBaseMap(dateCounts);
        deviceAdapter.setBaseMap(deviceCounts);

        //Setup the ListView w/ adapter and itemClickListener
        categoryListView.setAdapter(dateAdapter);
        final LoaderManager.LoaderCallbacks<Cursor> temp = this; //Needed for listener

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (categoryListView.getAdapter().getClass().equals(CategoryListAdapter.class)) {
                    String[] items = (String[]) categoryListView.getItemAtPosition(i);
                    Log.d("Repair History", String.format("Testing Click: %s, %s", items[0], items[1]));
                    if (categoryState) { //Date

                    } else { //Device
                        Log.d("Repair History", "Doing Device");
                        Bundle args = new Bundle();
                        args.putString("id",device_map.get(items[0]));
                        Log.d("Repair History",device_map.get(items[0]));
                        categoryListView.setAdapter(sessionAdapter);
                        getLoaderManager().destroyLoader(SESSION_DEVICE_LOADER); //clear the loader so it's ready for new one
                        getLoaderManager().initLoader(SESSION_DEVICE_LOADER,args,temp);
                    }
                } else if (categoryListView.getAdapter().getClass().equals(SessionCursorAdapter.class)) {
                    //Stuff for Sessions
                }
            }
        });

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
        inflater.inflate(R.menu.activity_main_toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_sort);
        item.setVisible(true);
        //Initially, will have date be the initial way to sort the sessions
        item.getSubMenu().findItem(R.id.date_item).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);

        switch(item.getItemId()) {
            case R.id.date_item:
                Log.d("Repair History","DATE");
                setAdapter(dateAdapter);
                categoryState = true;
                //categoryAdapter.setBaseMap(date_counts);
                break;
            case R.id.device_item:
                Log.d("Repair History","DEVICE");
                setAdapter(deviceAdapter);
                categoryState = false;
                //categoryAdapter.setBaseMap(device_counts);
                break;
            case R.id.action_sort:
                Log.d("Repair History","SORT");
                break;
            default:
                Log.d("Repair History","DEVICE");
                //categoryAdapter.setBaseMap(null);
        }


        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SESSION_DATE_LOADER) {
            Log.d("Repair Session", "Initiate Cursor Loader for DATE");
            return null;
        } else if (id == SESSION_DEVICE_LOADER) {
            Log.d("Repair Session", "Initiate Cursor Loader for DEVICE");
            return TCDatabaseHelper.get(this.getContext()).getSessionsFromChartCursorLoader(args.getString("id"));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        sessionAdapter.swapCursor(data);
        sessionAdapter.notifyDataSetChanged();

        Log.d("Repair Session", "Made it through loader");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

