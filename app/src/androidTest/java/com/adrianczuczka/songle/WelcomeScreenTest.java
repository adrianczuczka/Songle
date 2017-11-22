package com.adrianczuczka.songle;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.PreferenceMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WelcomeScreenTest {
    @Rule
    public ActivityTestRule<WelcomeScreen> mWelcomeScreen = new ActivityTestRule<>(WelcomeScreen.class);
    @Test
    public void chooseSongButtonRedirects() {
        onView(withId(R.id.welcome_screen_play_button))
                .perform(click());
        onView(withId(R.id.activity_choose_song_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void settingsButtonRedirects(){
        onView(withId(R.id.welcome_screen_settings_game))
                .perform(click());
        onData(withKey("preference_screen")).check(matches(isDisplayed()));
    }
}
