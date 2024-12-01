package com.example.plannet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.platform.app.InstrumentationRegistry;
// fragment classes used
//import com.example.plannet.ui.orghome.AnotherFragmentToTest;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import com.example.plannet.ui.orghome.OrganizerCreateEventFragment;

/**
 * Instrumented test, to test fragment operations which will execute on an Android device.
 *
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest

public class ProjectInstrumentedTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void handleNotificationPermissionPopup() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        if (device.hasObject(By.text("Allow"))) {
            device.findObject(By.text("Allow")).click();
        }
    }


    /**
     * Tests if create new event fragment UI components are intact
     */
    @Test
    public void testOrganizerCreateNewEventFragmentUIComponents() {
        onView(withId(R.id.button_switch2)).perform(click());
        onView(withId(R.id.buttonNewEvent)).check(matches(isDisplayed()));
        // Check OrganizerCreateEventFragment UI elements
        // Commented out the next 2 lines because we removed this button
//        onView(withId(R.id.buttonQrCodes)).check(matches(isDisplayed()));
//        Espresso.onIdle();
        onView(withId(R.id.buttonDraw)).check(matches(isDisplayed()));
    }


    /**
     * Tests if navigation between tabs work on click
     */
    @Test
    public void testNavigationBetweenNavTabs() {
        // Click on entrant profile tab from nav bar
        onView(withId(R.id.navigation_entrantprofile)).perform(click());
        Espresso.onIdle();

        // Verify that content specific to entrantpprofile is displayed
        onView(withId(R.id.first_name_edittext)).check(matches(isDisplayed()));
        Espresso.onIdle();
    }

    /**
     * Tests if creating a facility dialog shows and creates for null facilities
     */
    @Test
    public void testCreateFacility() {
        // navigate to org view
        onView(withId(R.id.button_switch2)).perform(click());
        Espresso.onIdle();

        // click create new event
        onView(withId(R.id.buttonNewEvent)).perform(click());
        Espresso.onIdle();
        // input facility name/location
        onView(withId(R.id.facility_name_edit)).perform(ViewActions.typeText("Rogers Place"));
        onView(withId(R.id.facility_location_edit)).perform(ViewActions.typeText("Downtown"));
        // save
        onView(withText("Save")).check(matches(isDisplayed())).perform(click());
        Espresso.onIdle();
    }
}