package com.adrianczuczka.songle;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.extensions.TestSetup;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.PreferenceMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static junit.framework.Assert.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

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
    public void resumeButtonRedirects(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mWelcomeScreen.getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lyrics", "test");
        editor.putString("kml", "test");
        editor.putString("title", "test");
        onView(withId(R.id.welcome_screen_resume_button)).perform(click());
        onView(withId(R.id.game_ui_parent_relative_layout)).check(matches(isDisplayed()));
    }
}
