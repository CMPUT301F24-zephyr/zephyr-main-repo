package com.example.plannet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.example.plannet.MainActivity;
import com.example.plannet.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EntrantTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void preSetup() {
        Espresso.onIdle();
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        if (device.hasObject(By.text("Allow"))) {
            device.findObject(By.text("Allow")).click();
        }
    }

    @Test
    public void testEntrantHomeUIElements() {
        onView(withId(R.id.scan_qr_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_picture)).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccessfulQRScanNavigatesToEventDetail() {
        onView(withId(R.id.scan_qr_button)).perform(click());
        onView(withId(R.id.barcode_scanner)).perform(typeText("SampleEventQRData"));
        onView(withId(R.id.entrants_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToProfile() {
        onView(withId(R.id.profile_picture)).perform(click());
        onView(withId(R.id.profile_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToEventList() {
        onView(withId(R.id.view_events_button)).perform(click());
        onView(withId(R.id.event_item_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackNavigationFromEventList() {
        onView(withId(R.id.view_events_button)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.scan_qr_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementsOnScanPage() {
        onView(withId(R.id.scan_qr_button)).perform(click());
        onView(withId(R.id.barcode_scanner)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewEventsListViewItemClick() {
        onView(withId(R.id.view_events_button)).perform(click());
        onView(withId(R.id.event_item_container)).perform(click());
        onView(withId(R.id.entrant_View_Event_Fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationBarNavigationEntrantProfile() {
        onView(withId(R.id.navigation_entranthome)).perform(click());
        onView(withId(R.id.container)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_entrant_profile_display)).perform(click());
        onView(withId(R.id.profile_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationBarNavigationEntrantNotif() {
        onView(withId(R.id.navigation_entranthome)).perform(click());
        onView(withId(R.id.container)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_entrantnotifications)).perform(click());
        onView(withId(R.id.invite_list_view)).check(matches(isDisplayed()));
    }
}
