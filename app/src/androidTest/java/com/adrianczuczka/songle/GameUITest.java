package com.adrianczuczka.songle;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;
import java.util.Iterator;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

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
        clearPrefs();
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
        clearPrefs();
        onView(withId(R.id.welcome_screen_play_button)).perform(click());
        onView(withId(R.id.content_choose_song_random_button)).perform(click());
        onView(withId(R.id.easy_button)).perform(click());
        getActivityInstance().getIntent().putExtra("title", "Not Song 2");
        onView(withId(R.id.game_ui_bottom_sheet_arrows)).perform(click());
        onView(withId(R.id.game_ui_guess_song_input)).perform(typeText("Song 2"));
        onView(withId(R.id.game_ui_guess_song_input)).perform(closeSoftKeyboard());
        onView(withId(R.id.game_ui_guess_song)).perform(click());
        onView(withId(R.id.game_ui_tries_amount)).check(matches(isDisplayed()));
    }

    @Test
    public void showSongList() {
        clearPrefs();
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.welcome_screen_play_button), withText("NEW GAME"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.content_choose_song_random_button), withText("Guess a random song"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v4.widget.NestedScrollView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.easy_button), withText("Easy"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.game_ui_bottom_sheet_arrows),
                        childAtPosition(
                                allOf(withId(R.id.game_ui_bottom_sheet),
                                        childAtPosition(
                                                withClassName(is("android.support.design.widget.CoordinatorLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.game_ui_show_list), withText("Show List of Words Found"),
                        childAtPosition(
                                allOf(withId(R.id.game_ui_bottom_sheet),
                                        childAtPosition(
                                                withClassName(is("android.support.design.widget.CoordinatorLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction gridView = onView(
                allOf(withId(R.id.grid),
                        childAtPosition(
                                allOf(withId(R.id.word_list_fragment_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        gridView.check(matches(isDisplayed()));
    }

    @Test
    public void checkGameOverWorks() {
        clearPrefs();
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.welcome_screen_settings_button), withText("SETTINGS"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.list_container),
                                0)))
                .atPosition(4);
        linearLayout.perform(click());

        pressBack();

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.welcome_screen_play_button), withText("NEW GAME"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.content_choose_song_random_button), withText("Guess a random song"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v4.widget.NestedScrollView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.game_ui_bottom_sheet_arrows),
                        childAtPosition(
                                allOf(withId(R.id.game_ui_bottom_sheet),
                                        childAtPosition(
                                                withClassName(is("android.support.design.widget.CoordinatorLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        relativeLayout.perform(click());
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.game_ui_guess_song_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.game_ui_bottom_sheet),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.game_ui_guess_song_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.game_ui_bottom_sheet),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("not song 2"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.game_ui_guess_song_input), withText("not song 2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.game_ui_bottom_sheet),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.game_ui_guess_song), withText("Guess the Song"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.game_ui_bottom_sheet),
                                        2),
                                2),
                        isDisplayed()));
        appCompatButton4.perform(click());

        onView(withId(R.id.game_over_return_button)).check(matches(isDisplayed()));
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

    private void clearPrefs(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mWelcomeScreen.getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
