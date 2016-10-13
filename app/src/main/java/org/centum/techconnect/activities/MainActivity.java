package org.centum.techconnect.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.centum.techconnect.R;
import org.centum.techconnect.fragments.ReportsFragment;
import org.centum.techconnect.fragments.SelfHelpFragment;
import org.centum.techconnect.resources.ResourceHandler;
import org.centum.techconnect.services.LoadResourcesService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Entry activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_SELF_HELP = 0;
    private static final int FRAGMENT_LOGS = 1;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private final Fragment[] FRAGMENTS = new Fragment[]{new SelfHelpFragment(), new ReportsFragment()};
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.loading_banner)
    RelativeLayout loadingLayout;

    private String[] fragmentTitles;
    private int currentFragment = -1;
    private ResponseReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the Broadcast Manager to receive messages from the IntentService Instance
        myReceiver = new ResponseReceiver();
        //Define the Intent filter for the
        IntentFilter mStatusIntentFilter = new IntentFilter(
               ResponseReceiver.PROCESS_RESPONSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,mStatusIntentFilter);

        setContentView(R.layout.activity_main);

        ResourceHandler.get(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadingLayout.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentTitles = getResources().getStringArray(R.array.fragment_titles);
        navigationView.setNavigationItemSelectedListener(this);
        int fragToOpen = FRAGMENT_SELF_HELP;
        if (savedInstanceState != null) {
            fragToOpen = savedInstanceState.getInt("frag", FRAGMENT_SELF_HELP);
        }
        setCurrentFragment(fragToOpen);
        if (ensurePermissions()) {
            //Here is the initial load of data
            loadResources();
        }

        // Show tutorial
        startActivity(new Intent(MainActivity.this, IntroTutorial.class));
    }

    private boolean ensurePermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_STORAGE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        loadResources();
    }

    private void loadResources() {
        loadingLayout.setVisibility(View.VISIBLE);
        //Here, want to replace AsyncTask with the IntentService. Currently, the only place necessary
        Intent loadResIntent = new Intent(this, LoadResourcesService.class);
        this.startService(loadResIntent);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment > -1) {
            outState.putInt("frag", currentFragment);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment == FRAGMENT_SELF_HELP) {
            if (!((SelfHelpFragment) FRAGMENTS[FRAGMENT_SELF_HELP]).onBack()) {
                // Fragment didn't consume back event
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int newFrag = -1;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_self_help) {
            newFrag = FRAGMENT_SELF_HELP;
        } else if (id == R.id.nav_reports) {
            newFrag = FRAGMENT_LOGS;
        } else if (id == R.id.call_dir) {
            startActivity(new Intent(this, CallActivity.class));
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_refresh) {
            ResourceHandler.get().clear();
            loadResources();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_view_tut) {
            startActivity(new Intent(this, IntroTutorial.class));
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        setCurrentFragment(newFrag);
        return true;
    }

    private void setCurrentFragment(int frag) {
        if (this.currentFragment != frag || this.currentFragment == -1) {
            this.currentFragment = frag;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, FRAGMENTS[frag])
                    .commit();
            setTitle(fragmentTitles[frag]);
            navigationView.getMenu().getItem(frag).setChecked(true);
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {

        //This corresponds to the Action that we want the receiver to do
        public static final String PROCESS_RESPONSE = "com.org.centum.techconnect.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            //If for some reason we are going to add a couple of filters instead of just one
            if(intent.getAction().equalsIgnoreCase(PROCESS_RESPONSE)) {
                //Check if the Download was successful
                LoadResourcesService.ResultType res = (LoadResourcesService.ResultType)
                        intent.getSerializableExtra(LoadResourcesService.RESULT_STATUS);
                ResourceHandler.get().deviceChanged();
                //Turh off the LoadingLayout
                loadingLayout.setVisibility(View.GONE);
                //Might not be necessary. In case there are different types of errors that we want to consider,
                //can set up this switch to do so
                switch(res) {
                    case SUCCESS:
                       break;
                    case RES_ERROR:
                        //Want to make a SnackBar which alerts the user that some resources are missing
                        Log.i("MainActivity", intent.getStringExtra(LoadResourcesService.RESULT_MESSAGE));
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_fragment_container),"Error in Loading Resources", Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                        break;
                }

            }



        }
    }
}


