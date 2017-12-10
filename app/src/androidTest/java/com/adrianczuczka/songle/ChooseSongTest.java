package com.adrianczuczka.songle;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChooseSongTest {
    @Rule
    public final ActivityTestRule<ChooseSong> mChooseSong = new ActivityTestRule<>
            (ChooseSong.class);

    @Test
    public void chooseRandomWorks() {
        onView(withId(R.id.content_choose_song_random_button)).perform(click());
        onView(withId(R.id.easy_button)).perform(click());
        onView(withId(R.id.game_ui_parent_relative_layout)).check(matches(isDisplayed()));
    }
}
