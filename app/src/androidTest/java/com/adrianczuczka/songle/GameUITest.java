package com.adrianczuczka.songle;

import android.Manifest;
import android.app.Activity;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;
import java.util.Iterator;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(JUnit4.class)
@LargeTest
public class GameUITest {
    @Rule
    public final GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android
                    .Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE);

    @Rule
    public final ActivityTestRule<WelcomeScreen> mWelcomeScreen = new ActivityTestRule<>
            (WelcomeScreen.class);

    private Activity getActivityInstance() {
        final Activity[] currentActivity = {null};

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry
                        .getInstance().getActivitiesInStage(Stage.RESUMED);
                Iterator<Activity> it = resumedActivity.iterator();
                currentActivity[0] = it.next();
            }
        });

        return currentActivity[0];
    }

    @Test
    public void guessSongWorksIfCorrectSong() {
        onView(withId(R.id.welcome_screen_play_button))
                .perform(click());
        onView(withId(R.id.content_choose_song_random_button)).perform(click());
        onView(withId(R.id.easy_button)).perform(click());
        getActivityInstance().getIntent().putExtra("title", "Song 2");
        onView(withId(R.id.game_ui_bottom_sheet_arrows)).perform(click());
        onView(withId(R.id.game_ui_guess_song_input)).perform(typeText("Song 2"));
        onView(withId(R.id.game_ui_guess_song_input)).perform(closeSoftKeyboard());
        onView(withId(R.id.game_ui_guess_song)).perform(click());
        onView(withId(R.id.congrats_text)).check(matches(isDisplayed()));
    }

    @Test
    public void guessSongWorksIfWrongSong() {
        onView(withId(R.id.welcome_screen_play_button)).perform(click());
        onView(withId(R.id.are_you_sure_yes_button)).perform(click());
        onView(withId(R.id.content_choose_song_random_button)).perform(click());
        onView(withId(R.id.easy_button)).perform(click());
        getActivityInstance().getIntent().putExtra("title", "Not Song 2");
        onView(withId(R.id.game_ui_bottom_sheet_arrows)).perform(click());
        onView(withId(R.id.game_ui_guess_song_input)).perform(typeText("Song 2"));
        onView(withId(R.id.game_ui_guess_song_input)).perform(closeSoftKeyboard());
        onView(withId(R.id.game_ui_guess_song)).perform(click());
        onView(withId(R.id.game_ui_tries_amount)).check(matches(isDisplayed()));
    }

    /*@Test
    public void showSongList(){
        onView(withId(R.id.welcome_screen_play_button)).perform(click());
        onView(withId(R.id.are_you_sure_yes_button)).perform(click());
        onView(withId(R.id.content_choose_song_random_button)).perform(click());
        onView(withId(R.id.easy_button)).perform(click());
        onView(withId(R.id.game_ui_bottom_sheet_arrows)).perform(click());
        onView(withId(R.id.game_ui_show_list)).perform(click());
        onData(allOf(withParent(withId(R.id.grid)))).check(matches(isDisplayed()));
    }*/
}
