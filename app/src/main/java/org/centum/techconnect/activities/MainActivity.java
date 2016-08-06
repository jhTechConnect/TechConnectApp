package org.centum.techconnect.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.centum.techconnect.R;
import org.centum.techconnect.fragments.ReportsFragment;
import org.centum.techconnect.fragments.SelfHelpFragment;
import org.centum.techconnect.model.Contact;
import org.centum.techconnect.model.Device;
import org.centum.techconnect.resources.NetworkHelper;
import org.centum.techconnect.resources.ResourceHandler;
import org.json.JSONException;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Entry activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_SELF_HELP = 0;
    private static final int FRAGMENT_LOGS = 1;
    private final Fragment[] FRAGMENTS = new Fragment[]{new SelfHelpFragment(), new ReportsFragment()};
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    private String[] fragmentTitles;
    private int currentFragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ResourceHandler.get(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        loadResources();

        // Show tutorial
        startActivity(new Intent(MainActivity.this, IntroTutorial.class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment > -1) {
            outState.putInt("frag", currentFragment);
        }
    }

    /**
     * Loads the resources in the background
     */
    private void loadResources() {
        new AsyncTask<Void, Void, Object[]>() {

            @Override
            protected Object[] doInBackground(Void... voids) {
                Log.d(MainActivity.class.getName(), "Loading resources...");
                try {
                    NetworkHelper helper = new NetworkHelper(MainActivity.this);
                    Device[] devices = helper.loadDevices(true);
                    Contact[] contacts = helper.loadCallDirectoryContacts(true);
                    return new Object[]{devices, contacts};
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object[] objects) {
                if (objects != null) {
                    ResourceHandler.get().setDevices((Device[]) objects[0]);
                    ResourceHandler.get().setContacts((Contact[]) objects[1]);
                }
            }
        }.execute();
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
            new AlertDialog.Builder(this).setTitle("Sync")
                    .setMessage("Resources will be synced on next app start")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
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
}
