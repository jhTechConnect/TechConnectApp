package org.techconnect;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.techconnect.activities.MainActivity;
import org.techconnect.helpers.IntentServiceIdlingResource;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by doranwalsten on 4/26/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DownloadGuideTest {

    private IntentServiceIdlingResource idlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void registerIntentServiceIdlingResource() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        idlingResource = new IntentServiceIdlingResource(instrumentation.getTargetContext());
        Espresso.registerIdlingResources(idlingResource);

    }

    @After
    public void unregisterIntentServiceIdlingResource() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void onDownloadGuide() {
        onView(withId(R.id.download_guides_button)).perform(click());


        //Click an item in list, currently the first one
        onData(anything()).inAdapterView(withId(R.id.guides_listView)).atPosition(0).onChildView(withId(R.id.downloadImageView)).perform(click());

        //Return to previous, check that the listview now has a guide
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.guides_listView)).atPosition(0).perform(click());
        onView(withText(R.string.start_session)).check(matches(isDisplayed()));

    }
}
