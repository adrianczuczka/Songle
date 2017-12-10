package com.adrianczuczka.songle;

import android.Manifest;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


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
}
