package org.techconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.centum.techconnect.R;
import org.techconnect.fragments.GuidesFragment;
import org.techconnect.fragments.ReportsFragment;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.services.TCService;
import org.techconnect.sql.TCDatabaseHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Entry activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private static final String SHOWN_TUTORIAL = "org.techconnect.prefs.shownturotial";

    private static final int FRAGMENT_GUIDES = 0;
    private static final int FRAGMENT_REPORTS = 1;
    private final Fragment[] FRAGMENTS = new Fragment[]{new GuidesFragment(), new ReportsFragment()};

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.loading_banner)
    RelativeLayout loadingLayout;
    @Bind(R.id.permission_layout)
    LinearLayout permissionLayout;
    @Bind(R.id.main_fragment_container)
    FrameLayout fragmentContainer;

    private String[] fragmentTitles;
    private int currentFragment = -1;
    private boolean showedLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ResourceHandler.get(this);
        TCDatabaseHelper.get(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadingLayout.setVisibility(View.GONE);
        // Lock until we have permission (in check permissions)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fragmentTitles = getResources().getStringArray(R.array.fragment_titles);
        navigationView.setNavigationItemSelectedListener(this);
        int fragToOpen = FRAGMENT_GUIDES;
        if (savedInstanceState != null) {
            fragToOpen = savedInstanceState.getInt("org.techconnect.mainactivity.frag", FRAGMENT_GUIDES);
        }
        setCurrentFragment(fragToOpen);
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean showedIntro = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE)
                .getBoolean(SHOWN_TUTORIAL, false);
        if (!showedIntro) {
            // Show tutorial
            getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE)
                    .edit()
                    .putBoolean(SHOWN_TUTORIAL, true)
                    .apply();
            //startActivity(new Intent(MainActivity.this, IntroTutorial.class));
        } else if (!showedLogin) {
            showedLogin = true;
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        checkPermissions();
    }

    @OnClick(R.id.grant_permission_btn)
    public void ensurePermissions() {
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
    }

    private boolean checkPermissions() {
        boolean havePermission = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (havePermission) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            permissionLayout.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
        }
        return havePermission;
    }

    private void updateResources() {
        loadingLayout.setVisibility(View.VISIBLE);
        String ids[] = TCDatabaseHelper.get().getAllChartIds();
        TCService.startLoadCharts(this, ids, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                loadingLayout.setVisibility(View.GONE);
                ((GuidesFragment) FRAGMENTS[FRAGMENT_GUIDES]).onRefresh();
            }
        });
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
        if (id == R.id.nav_guides) {
            newFrag = FRAGMENT_GUIDES;
        } else if (id == R.id.nav_reports) {
            newFrag = FRAGMENT_REPORTS;
        } else if (id == R.id.call_dir) {
            startActivity(new Intent(this, CallActivity.class));
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_refresh) {
            updateResources();
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


