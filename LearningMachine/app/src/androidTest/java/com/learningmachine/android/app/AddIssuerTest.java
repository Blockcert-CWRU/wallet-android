package com.learningmachine.android.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.learningmachine.android.app.ui.home.HomeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

/**
 * Created by chris on 12/20/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddIssuerTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    @Before
    public void beforeEachTest() {
        Timber.i("Initializing valid state");
    }

    @After
    public void afterEachTest() {
        Timber.i("Deinitializing valid state");
    }

    @Test
    public void addIssuer_fromEmptyState() {
        onView(withId(R.id.settings_add_issuer_text_view)).perform(click());

        onView(withId(R.id.add_issuer_url_edit_text)).check(matches(isDisplayed()));

        Timber.i("And we're running this test!");
    }
}
