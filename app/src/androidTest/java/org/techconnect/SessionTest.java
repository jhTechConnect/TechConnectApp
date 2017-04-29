package org.techconnect;

import android.app.Activity;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.view.View;
import android.widget.LinearLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.techconnect.activities.MainActivity;
import org.techconnect.helpers.TestingUtilities;
import org.techconnect.model.session.Session;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.anything;

/**
 * Created by doranwalsten on 4/27/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SessionTest {

    private int NUMBER_STEPS = 10;
    private Session session;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void startSession() {
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

    @Test
    public void onEndSessionEarlyDoNotSave() {
        //For a certain number of screens, hit options, then terminate the session
        LinearLayout layout = (LinearLayout) getActivityInstance().findViewById(R.id.options_linearLayout);
        View session_info = getActivityInstance().findViewById(R.id.session_info_layout);

        for (int i = 0; i < NUMBER_STEPS; i++ ) {
            //First check to see if we somehow have completed a session (save is shown)
            if (session_info.getVisibility() == View.VISIBLE) {

                onView(isRoot()).perform(ViewActions.pressBack());

                //Dialog should pop up
                onView(withId(android.R.id.button1)).perform(click());

                //Now, need to hit the end session button
                onView(withId(R.id.end_session)).perform(click());
                onView(withId(android.R.id.button1)).perform(click()); //Want to quit
                onView(withId(android.R.id.button2)).perform(click()); //Don't want to save

                onView(withId(R.id.button)).check(matches(isDisplayed()));
                return;

            } else {
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
        onView(withId(android.R.id.button2)).perform(click()); //Don't want to save

        onView(withId(R.id.button)).check(matches(isDisplayed()));
    }

    @Test
    public void OnEndSessionEarlySave() {
        //For a certain number of screens, hit options, then terminate the session
        LinearLayout layout = (LinearLayout) getActivityInstance().findViewById(R.id.options_linearLayout);
        View session_info = getActivityInstance().findViewById(R.id.session_info_layout);

        for (int i = 0; i < NUMBER_STEPS; i++ ) {
            //First check to see if we somehow have completed a session (save is shown)
            if (session_info.getVisibility() == View.VISIBLE) {

                onView(isRoot()).perform(ViewActions.pressBack());

                //Dialog should pop up
                onView(withId(android.R.id.button1)).perform(click());

                //Now, need to hit the end session button
                onView(withId(R.id.end_session)).perform(click());
                onView(withId(android.R.id.button1)).perform(click()); //Want to quit
                onView(withId(android.R.id.button1)).perform(click()); //Want to save

                //Check that the session info is visible, save everything
                onView(withId(R.id.session_info_layout)).check(matches(isDisplayed()));
                onView(withId(R.id.department_editText)).perform(typeText(TestingUtilities.getRandomString()));
                onView(withId(R.id.manufacturer_editText)).perform(typeText(TestingUtilities.getRandomString()));
                onView(withId(R.id.model_editText)).perform(typeText(TestingUtilities.getRandomString()));
                onView(withId(R.id.serial_editText)).perform(typeText(TestingUtilities.getRandomString()));

                //Click save
                onView(withId(R.id.start_button)).perform(click());

                //Technically, probably want to go and check the repair history to verify that the session is there.
                onView(withId(R.id.button)).check(matches(isDisplayed()));
                return;

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
        onView(withId(R.id.department_editText)).perform(scrollTo(),typeText(TestingUtilities.getRandomString()));
        onView(withId(R.id.manufacturer_editText)).perform(scrollTo(),typeText(TestingUtilities.getRandomString()));
        onView(withId(R.id.model_editText)).perform(scrollTo(), typeText(TestingUtilities.getRandomString()));
        onView(withId(R.id.serial_editText)).perform(scrollTo(),typeText(TestingUtilities.getRandomString()),closeSoftKeyboard());

        //Click save
        onView(withId(R.id.start_button)).perform(scrollTo(), click());

        //Technically, probably want to go and check the repair history to verify that the session is there.
        onView(withId(R.id.button)).check(matches(isDisplayed()));

    }

    public void onCompleteSession() {
        //For the department, manufacturer, model, serial number enter in a random string
        /*
        onView(withId(R.id.department_editText)).perform(typeText(TCDatabaseHelper.get(getContext()).getRandomId()));
        onView(withId(R.id.manufacturer_editText)).perform(typeText(TCDatabaseHelper.get(getContext()).getRandomId()));
        onView(withId(R.id.model_editText)).perform(typeText(TCDatabaseHelper.get(getContext()).getRandomId()));
        onView(withId(R.id.serial_editText)).perform(typeText(TCDatabaseHelper.get(getContext()).getRandomId()));

        //Click save
        onView(withId(R.id.start_button)).perform(click());
        */
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

}
