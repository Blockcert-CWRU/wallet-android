package com.learningmachine.android.app.ui.splash;


import android.app.ActivityManager;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.learningmachine.android.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Text;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GeneratePassphraseTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

//    @Before
//    public void beforeTest() {
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        ((ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
//    }

    @Test
    public void generatePassphraseTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.new_account_button), withText("New Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction onboardingViewPager = onView(
                allOf(withId(R.id.onboarding_view_pager),
                        childAtPosition(
                                allOf(withId(android.R.id.content),
                                        childAtPosition(
                                                withId(R.id.action_bar_root),
                                                1)),
                                0),
                        isDisplayed()));
        onboardingViewPager.perform(swipeLeft());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.generate_passphrase), withText("Generate Passphrase"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        1),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction onboardingViewPager2 = onView(
                allOf(withId(R.id.onboarding_view_pager),
                        childAtPosition(
                                allOf(withId(android.R.id.content),
                                        childAtPosition(
                                                withId(R.id.action_bar_root),
                                                1)),
                                0),
                        isDisplayed()));
        onboardingViewPager2.perform(swipeLeft());

        ViewInteraction textView = onView(
                allOf(withId(R.id.onboarding_passphrase_text_view),
                        childAtPosition(
                                withParent(withId(R.id.onboarding_view_pager)),
                                3),
                        isDisplayed()));
        final String[] generatedPassphraseArray = { null };
        textView.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting the text from a text view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view;
                generatedPassphraseArray[0] = tv.getText().toString();
            }
        });
        final String generatedPassphrase = generatedPassphraseArray[0];
        Timber.i("Generated passphrase: " + generatedPassphrase);
//        textView.check(matches(withText("crater pioneer also space seven unhappy rare tail glove zero great noodle armed modify woman point guilt dry yellow inherit note reduce ticket brick")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.onboarding_done_button), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        1),
                                4),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.fragment_home_settings_menu_item), withContentDescription("Settings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.settings_reveal_passphrase_text_view), withText("Reveal Passphrase"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                1)));
        appCompatTextView.perform(scrollTo(), click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.current_passphrase_text_view),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                1),
                        isDisplayed()));

        final String[] displayedPassphraseArray = { null };
        textView2.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "Getting text from a text view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view;
                displayedPassphraseArray[0] = tv.getText().toString();
            }
        });
        final String displayedPassphrase = displayedPassphraseArray[0];
        textView2.check(matches(withText(displayedPassphrase)));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
