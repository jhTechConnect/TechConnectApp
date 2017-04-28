package org.techconnect;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.techconnect.activities.MainActivity;

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

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void downloadGuide() {
        onView(withId(R.id.download_guides_button)).perform(click());


        //Click an item in list, currently the first one
        onData(anything()).inAdapterView(withId(R.id.guides_listView)).atPosition(0).onChildView(withId(R.id.downloadImageView)).perform(click());

        //Return to previous, check that the listview now has a guide
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.guides_listView)).atPosition(0).perform(click());
        onView(withText(R.string.start_session)).check(matches(isDisplayed()));

    }
}
