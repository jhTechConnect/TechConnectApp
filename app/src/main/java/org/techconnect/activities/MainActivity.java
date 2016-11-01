package org.techconnect.activities;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.asynctasks.LogoutAsyncTask;
import org.techconnect.fragments.GuidesFragment;
import org.techconnect.fragments.ReportsFragment;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.misc.auth.AuthListener;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.model.UserAuth;
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
    private static final String USER_LEARNED_DRAWER = "org.techconnect.prefs.shownturotial.learneddrawer";
    private static final String ASKED_PERMISSION = "org.techconnect.prefs.shownturotial.askedpermission";

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

    TextView headerTextView;
    ImageButton dropDownButton;
    MenuItem logoutMenuItem;
    MenuItem loginMenuItem;
    MenuItem viewProfileMenuItem;

    private String[] fragmentTitles;
    private int currentFragment = -1;
    private boolean showedLogin = false;
    private boolean userLearnedDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ResourceHandler.get(this);
        TCDatabaseHelper.get(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userLearnedDrawer = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE).getBoolean(USER_LEARNED_DRAWER, false);
        loadingLayout.setVisibility(View.GONE);
        // Lock until we have permission (in check permissions)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!userLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    userLearnedDrawer = true;
                    getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE).edit()
                            .putBoolean(USER_LEARNED_DRAWER, true).apply();
                }
            }
        };
        drawerLayout.addDrawerListener(toggle);
        //Set MenuItem properties for profile-related options
        logoutMenuItem = navigationView.getMenu().findItem(R.id.logout);
        loginMenuItem = navigationView.getMenu().findItem(R.id.login);
        viewProfileMenuItem = navigationView.getMenu().findItem(R.id.profile);

        headerTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.headerTextView);
        dropDownButton = (ImageButton) navigationView.getHeaderView(0).findViewById(R.id.dropDownButton);
        dropDownButton.setOnClickListener(new View.OnClickListener() {
            boolean isClicked = false;

            @Override
            public void onClick(View view) {
                if (isClicked) {
                    //Close the profile options
                    dropDownButton.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                    logoutMenuItem.setVisible(false);
                    viewProfileMenuItem.setVisible(false);
                } else {
                    //Open the profile options
                    dropDownButton.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                    logoutMenuItem.setVisible(true);
                    viewProfileMenuItem.setVisible(true);
                }
                isClicked = !isClicked;
            }
        });
        dropDownButton.setVisibility(View.INVISIBLE);//Not initially there

        toggle.syncState();

        AuthManager.get(this).addAuthListener(new AuthListener() {

            @Override
            public void onLoginSucces(UserAuth auth) {
                updateNavHeader();
            }

            @Override
            public void onLogout() {
                updateNavHeader();
            }
        });

        fragmentTitles = getResources().getStringArray(R.array.fragment_titles);
        navigationView.setNavigationItemSelectedListener(this);
        int fragToOpen = FRAGMENT_GUIDES;
        if (savedInstanceState != null) {
            fragToOpen = savedInstanceState.getInt("org.techconnect.mainactivity.frag", FRAGMENT_GUIDES);
        }
        setCurrentFragment(fragToOpen);
    }

    private void updateNavHeader() {
        boolean loggedIn = AuthManager.get(this).hasAuth();
        User user;
        if (loggedIn && (user = TCDatabaseHelper.get(this).getUser(AuthManager.get(this).getAuth().getUserId())) != null) {
            headerTextView.setText(user.getName());
            headerTextView.setOnClickListener(new View.OnClickListener() {
                boolean isClicked = false;
                @Override
                public void onClick(View view) {
                    if (isClicked) {
                        //Close the profile options
                        dropDownButton.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                        logoutMenuItem.setVisible(false);
                        viewProfileMenuItem.setVisible(false);
                    } else {
                        //Open the profile options
                        dropDownButton.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                        logoutMenuItem.setVisible(true);
                        viewProfileMenuItem.setVisible(true);
                    }
                    isClicked = !isClicked;
                }
            });
            dropDownButton.setVisibility(View.VISIBLE);
            dropDownButton.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
        } else {
            headerTextView.setText(R.string.app_name);
            dropDownButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean hasPermissions = checkPermissions();
        boolean showedIntro = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE)
                .getBoolean(SHOWN_TUTORIAL, false);
        if (!showedIntro) {
            // Show tutorial
            getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE)
                    .edit()
                    .putBoolean(SHOWN_TUTORIAL, true)
                    .apply();
            //startActivity(new Intent(MainActivity.this, IntroTutorial.class));
        } else if (!showedLogin && !AuthManager.get(this).hasAuth() && hasPermissions) {
            onShowLogin();
        } else if (AuthManager.get(this).hasAuth()) {
            loginMenuItem.setVisible(false);
            logoutMenuItem.setVisible(false);
            viewProfileMenuItem.setVisible(false);

        } else {
            loginMenuItem.setVisible(true);
            logoutMenuItem.setVisible(false);
            viewProfileMenuItem.setVisible(false);
        }
        updateNavHeader();
        if (hasPermissions && !userLearnedDrawer) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @OnClick(R.id.grant_permission_btn)
    public void askForPermission() {
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
        } else {
            if (!getSharedPreferences(getClass().getName(), MODE_PRIVATE).getBoolean(ASKED_PERMISSION, false)) {
                getSharedPreferences(getClass().getName(), MODE_PRIVATE)
                        .edit()
                        .putBoolean(ASKED_PERMISSION, true)
                        .apply();
                askForPermission();
            }
        }
        return havePermission;
    }

    private void updateResources() {
        loadingLayout.setVisibility(View.VISIBLE);
        String ids[] = TCDatabaseHelper.get(this).getAllChartIds();
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
        } else if (id == R.id.login) {
            onShowLogin();
            return true;
        } else if (id == R.id.logout) {
            onLogout();
            return true;
        } else if (id == R.id.profile) {
            //Open up Account info
            onViewProfile();
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        setCurrentFragment(newFrag);
        return true;
    }

    private void onShowLogin() {
        showedLogin = true;
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void onLogout() {
        if (AuthManager.get(this).hasAuth()) {
            new LogoutAsyncTask() {
                ProgressDialog pd;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pd = ProgressDialog.show(MainActivity.this, getString(R.string.logging_out), null, true, false);
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    AuthManager.get(null).setAuth(null);
                    pd.dismiss();
                    pd = null;
                    onShowLogin();
                }
            }.execute(AuthManager.get(this).getAuth());
        }
    }

    private void onViewProfile() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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


