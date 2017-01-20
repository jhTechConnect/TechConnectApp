package org.techconnect.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.activities.SessionActivity;
import org.techconnect.adapters.CategoryListAdapter;
import org.techconnect.adapters.SessionCursorAdapter;
import org.techconnect.asynctasks.ExportHistoryAsyncTask;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

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
    private static final int VIEW_SESSION_REQUEST = 2;

    //All of the binds
    @Bind(R.id.categoryButton)
    ImageButton categoryButton;
    @Bind(R.id.categoryTextView)
    TextView categoryTextView;
    @Bind(R.id.categoryListView)
    ListView categoryListView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.categoryLayout)
    RelativeLayout categoryLayout;
    @Bind(R.id.exportButton)
    Button exportButton;


    //Adapters
    private SessionCursorAdapter sessionAdapter;
    private CategoryListAdapter dateAdapter = new CategoryListAdapter();
    private CategoryListAdapter deviceAdapter = new CategoryListAdapter();
    private boolean categoryState = true; //True == Date, False == Device
    private String[] categoryData;
    private Map<String,String> device_map;
    private boolean sorting = true; //Sorting between date and device

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
        device_map = TCDatabaseHelper.get(this.getContext()).getChartNamesAndIDs();
        //Determine the number of sessions associated with each device
        updateCountAdapters();

        //Set the click listener for the imagebutton
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sorting = true; //Bring back menu item
                categoryLayout.setVisibility(View.GONE);
                if (categoryState) {
                    //Bring back the date list
                    categoryListView.setAdapter(dateAdapter);
                } else {
                    //Bring back the device list
                    categoryListView.setAdapter(deviceAdapter);
                }
                getActivity().invalidateOptionsMenu();
            }
        });

        //Set the click listener for the export button
        exportButton.setOnClickListener(this);

        //Setup the ListView w/ adapter and itemClickListener
        categoryListView.setAdapter(dateAdapter);
        final LoaderManager.LoaderCallbacks<Cursor> temp = this; //Needed for listener

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (categoryListView.getAdapter().getClass().equals(CategoryListAdapter.class)) {
                    categoryData = (String[]) categoryListView.getItemAtPosition(i);
                    Log.d("Repair History", String.format("Testing Click: %s, %s",categoryData[0], categoryData[1]));
                    if (categoryState) { //Date
                        Log.d("Repair History", "Doing Date");
                        Bundle args = new Bundle();
                        args.putString("date",categoryData[0]);
                        categoryListView.setAdapter(sessionAdapter);
                        getLoaderManager().destroyLoader(SESSION_DEVICE_LOADER);
                        getLoaderManager().destroyLoader(SESSION_DATE_LOADER); //clear the loader so it's ready for new one
                        getLoaderManager().initLoader(SESSION_DATE_LOADER,args,temp);

                    } else { //Device
                        Log.d("Repair History", "Doing Device");
                        Bundle args = new Bundle();
                        args.putString("id",device_map.get(categoryData[0]));
                        Log.d("Repair History",device_map.get(categoryData[0]));
                        categoryListView.setAdapter(sessionAdapter);
                        getLoaderManager().destroyLoader(SESSION_DATE_LOADER);
                        getLoaderManager().destroyLoader(SESSION_DEVICE_LOADER); //clear the loader so it's ready for new one
                        getLoaderManager().initLoader(SESSION_DEVICE_LOADER,args,temp);

                    }

                    //Startup the ProgressBar
                    categoryListView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    //Make the categoryLayoutVisible
                    categoryLayout.setVisibility(View.VISIBLE);
                    categoryTextView.setText(categoryData[0]);

                    sorting = false;
                    getActivity().invalidateOptionsMenu();

                } else if (categoryListView.getAdapter().getClass().equals(SessionCursorAdapter.class)) {
                    //Want to initiate a Session Activity just like in ResumeSessionFragment
                    SessionListItemView sessionView = ((SessionListItemView) view);
                    Intent intent = new Intent(getContext(), SessionActivity.class);
                    // Get the non-stub chart and open
                    intent.putExtra(SessionActivity.EXTRA_SESSION,
                            sessionView.getSession()); //Maybe? Not sure if this is a good idea
                    startActivityForResult(intent,VIEW_SESSION_REQUEST);
                }
            }
        });

        setHasOptionsMenu(true);
        Log.d("Repair History Setup", "View Initialized");

        return view;
    }

    @Override
    public void onResume() {
        Log.d("Repair History", "Resume Fragment");
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.repair_history);
        }
    }


    private void refreshData() {
        Log.d("Repair History", "Refresh Session List");

        if (categoryState) { //Date
            Bundle args = new Bundle();
            args.putString("date",categoryData[0]);
            getLoaderManager().restartLoader(SESSION_DATE_LOADER,args,this);
        } else {
            Bundle args = new Bundle();
            args.putString("id",device_map.get(categoryData[0]));
            getLoaderManager().initLoader(SESSION_DEVICE_LOADER,args,this);
        }

        //Startup the ProgressBar
        categoryListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        //Update the other adapters
        updateCountAdapters();
    }

    private void updateCountAdapters() {
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
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
    }


    private void setAdapter(ListAdapter a) {
        categoryListView.setAdapter(a);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main_toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_sort);
        if (sorting) {
            item.setVisible(true);
            //Select correct button
            if (categoryState) { //Date
                //Initially, will have date be the initial way to sort the sessions
                item.getSubMenu().findItem(R.id.date_item).setChecked(true);
            } else {
                //Initially, will have date be the initial way to sort the sessions
                item.getSubMenu().findItem(R.id.device_item).setChecked(true);
            }
        } else {
            item.setVisible(false);
        }

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
            return TCDatabaseHelper.get(this.getContext()).getSessionsFromDateCursorLoader(args.getString("date"));
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
        //Have a bit of a delay to ensure the progressBar doesn't mess with UI
        Runnable r = new Runnable() {
            @Override
            public void run() {
                categoryListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 500);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.exportButton) {
            //Open Dialog Box to get email
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setTitle(R.string.export_repair_history);
            builder.setMessage("Send History to Email");
            View v = inflater.inflate(R.layout.dialog_fragment_export_history,null);
            builder.setView(v);

            final EditText email = (EditText) v.findViewById(R.id.comments_editText);

            builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //Do nothing here because we override this button later to change the close behaviour.
                    //However, we still need this because on older versions of Android unless we
                    //pass a handler the button doesn't get instantiated
                }
            });
            builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //Do nothing here because we override this button later to change the close behaviour.
                    //However, we still need this because on older versions of Android unless we
                    //pass a handler the button doesn't get instantiated
                }
            });

            //Need to overwrite with funky custom listener
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Create CSV in Documents directory based on date of export
                    ExportHistoryAsyncTask task = new ExportHistoryAsyncTask(getContext());
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        Log.d("Repair History", "Valid email");
                        task.execute(email.getText().toString());
                        dialog.dismiss();
                    } else {
                        //Show error in the dialog box
                        Log.d("Repair History", "Invalid email");
                        email.setError(getResources().getString(R.string.error_invalid_email));
                    }
                }
            });

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_SESSION_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Need to update listview
                refreshData();
            }
        }

    }
}

