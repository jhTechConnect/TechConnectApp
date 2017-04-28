package org.techconnect;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.techconnect.activities.MainActivity;
import org.techconnect.misc.auth.AuthManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by doranwalsten on 4/26/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest {

    //Save my login credentials so it's easy to attempt login
    String user = "dwalste1@jhu.edu";
    String password = "dwalsten";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setupLoginScreen() {
        //If the login screen is not visible, logout the current user
        if (AuthManager.get(mActivityRule.getActivity()).hasAuth()) {
            // Open Drawer to click on navigation.
            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                    .perform(open()); // Open Drawer

            onView(withId(R.id.nav_view))
                    .perform(navigateTo(R.id.logout));
        }
    }

    @Test
    public void onASkipMainActivityShown() {
        //Find the skip sign-in button, test if MainActivity screen is shown
        onView(withId(R.id.skip_signin_button))
                .perform(click());

        //Since we have a dialog pop-up, we need to find the ok button
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.download_guides_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void onLogin() {

        //Enter in information, then press button
        onView(withId(R.id.email)).perform(typeText(user));
        onView(withId(R.id.password)).perform(typeText(password),closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());

        //Check to see that the mainactivity is shown
        onView(withId(R.id.download_guides_button))
                .check(matches(isDisplayed()));

    }




}
