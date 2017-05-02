package org.techconnect;

import android.app.Activity;
import android.database.Cursor;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.techconnect.activities.MainActivity;
import org.techconnect.helpers.TestingUtilities;
import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseContract;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by dwalsten5 on 4/29/2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepairHistoryTest {

    private int NUMBER_STEPS = 10;
    private String department = "testString1";
    private String manufacturer = "testString2";
    private String modelNumber = "testString3";
    private String serialNumber = "testString4";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void onEndSessionEarlySave() {
        //Start the session from the guides fragment
        startSession();

        //For a certain number of screens, hit options, then terminate the session
        LinearLayout layout = (LinearLayout) getActivityInstance().findViewById(R.id.options_linearLayout);
        View session_info = getActivityInstance().findViewById(R.id.session_info_layout);
        for (int i = 0; i < NUMBER_STEPS; i++ ) {
            //First check to see if we somehow have completed a session (save is shown), need to back out first
            if (session_info.getVisibility() == View.VISIBLE) {
                onView(isRoot()).perform(ViewActions.pressBack());
                //Dialog should pop up
                onView(withId(android.R.id.button1)).perform(click());

                break;

            } else {
                //Attempt to open an image if it is there


                //Attempt to open a resource if it is there


                // View is not in hierarchy
                int index = nextInt(0, layout.getChildCount());
                //Pick random option to advance
                onView(withIndex(withParent(withId(R.id.options_linearLayout)), index)).perform(click());
            }
        }

        //If we made it here, we want to end
        //Now, need to hit the end session button
        onView(withId(R.id.end_session)).perform(click());
        onView(withId(android.R.id.button1)).perform(click()); //Want to quit
        onView(withId(android.R.id.button1)).perform(click()); //Want to save

        //Check that the session info is visible, save everything
        onView(withId(R.id.session_info_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.department_editText)).perform(scrollTo(),typeText(department));
        onView(withId(R.id.manufacturer_editText)).perform(scrollTo(),typeText(manufacturer));
        onView(withId(R.id.model_editText)).perform(scrollTo(), typeText(modelNumber));
        onView(withId(R.id.serial_editText)).perform(scrollTo(),typeText(serialNumber),closeSoftKeyboard());

        //Click save
        onView(withId(R.id.start_button)).perform(scrollTo(), click());

        //Check that we're back at the Guide page
        onView(withId(R.id.button)).check(matches(isDisplayed()));
    }

    @Test
    public void onResumeSession() {
        //Open the panel, click repair history
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_repair_history));

        //Now, just need to click the first item in each view to open session
        onData(anything()).inAdapterView(withId(R.id.categoryListView)).atPosition(0).perform(click());
        //onData(anything()).inAdapterView(withId(R.id.session_ListView)).atPosition(0).perform(click());
        onData(allOf(is(instanceOf(Cursor.class)),withRowString(TCDatabaseContract.SessionEntry.MANUFACTURER,manufacturer)))
                .inAdapterView(withId(R.id.session_ListView))
                .perform(click());

        //Confirm that all of the fields are correct based on session

        onView(withId(R.id.manufacturer_textView)).check(matches(withText(manufacturer)));
        onView(withId(R.id.model_textView)).check(matches(withText(modelNumber)));
        onView(withId(R.id.serial_textView)).check(matches(withText(serialNumber)));
        TextView textView = (TextView) getActivityInstance().findViewById(R.id.step_textView);
        String step = textView.getText().toString();

        //Resume session, ensure that the current step is still correct as compared to the
        onView(withId(R.id.resumeButton)).perform(click());
        onView(withId(R.id.question_textView)).check(matches(withText(step)));

        //Just quit for now
        onView(withId(R.id.end_session)).perform(click());
        onView(withId(android.R.id.button1)).perform(click()); //Want to quit
        onView(withId(android.R.id.button2)).perform(click()); //Don't want to save
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    public Activity getActivityInstance(){
        final Activity[] currentActivity = {null};
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity[0] = (Activity) resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity[0];
    }

    private void startSession() {
        onData(anything()).inAdapterView(withId(R.id.guides_listView)).atPosition(0).perform(click());
        onView(withId(R.id.button)).perform(scrollTo(), click());
        //onView(withId(R.id.button)).perform(click());

        //Since we have a dialog pop-up, we need to find the ok button
        try {
            onView(withId(android.R.id.button1)).perform(click());
        } catch (NoMatchingViewException e) {
            onView(withId(R.id.button)).perform(click());
            onView(withId(android.R.id.button1)).perform(click());
        }
    }
}
