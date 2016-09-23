package org.centum.techconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.centum.techconnect.R;
import org.centum.techconnect.asynctasks.LoadResourcesAsyncTask;
import org.centum.techconnect.fragments.ReportsFragment;
import org.centum.techconnect.fragments.SelfHelpFragment;
import org.centum.techconnect.resources.ResourceHandler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        new LoadResourcesAsyncTask(this, new LoadResourcesAsyncTask.ExecutionCompleteListener() {
            @Override
            public void onFinished(boolean error) {
                loadingLayout.setVisibility(View.GONE);
                if (error) {
                    //TODO replace with snackbar
                    Toast.makeText(MainActivity.this, R.string.error_loading_resources, Toast.LENGTH_LONG).show();
                }
            }
        }).execute();
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
}
