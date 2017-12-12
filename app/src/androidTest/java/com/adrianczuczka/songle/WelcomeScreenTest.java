package com.adrianczuczka.songle;

import android.Manifest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class WelcomeScreenTest {
    @Rule
    public final GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android
                    .Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE);

    @Rule
    public final ActivityTestRule<WelcomeScreen> mWelcomeScreen = new ActivityTestRule<>
            (WelcomeScreen.class);

    @Test
    public void chooseSongButtonRedirects() {
        onView(withId(R.id.welcome_screen_play_button))
                .perform(click());
        onView(withId(R.id.activity_choose_song_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void resumeButtonRedirects(){
        onView(withId(R.id.welcome_screen_play_button))
                .perform(click());
        onView(withId(R.id.content_choose_song_random_button)).perform(click());
        onView(withId(R.id.easy_button)).perform(click());
        pressBack();
        onView(withId(R.id.welcome_screen_resume_button)).perform(click());
        onView(withId(R.id.game_ui_parent_relative_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void settingsButtonRedirects() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.welcome_screen_settings_button), withText("SETTINGS"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction listView = onView(
                allOf(withId(android.R.id.list),
                        childAtPosition(
                                allOf(withId(android.R.id.list_container),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        listView.check(matches(isDisplayed()));

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
