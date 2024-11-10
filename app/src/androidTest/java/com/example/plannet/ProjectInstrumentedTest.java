package com.example.plannet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

// fragment classes used
//import com.example.plannet.ui.orghome.AnotherFragmentToTest;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

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

    /**
     * Tests if the FirstTimeUser fragment shows up for new users
     */
    @Test
    public void checkFirstTimeUserFragment() {
        // since we're using a new android ID, it should display new user page
        onView(withId(R.id.button_welcome)).check(matches(isDisplayed()));
        // a wait command for the previous query to finish
        Espresso.onIdle();

        onView(withId(R.id.button_welcome)).perform(click());
        Espresso.onIdle();
    }

    /**
     * Tests if create new event fragment UI components are intact
     */
    @Test
    public void testOrganizerCreateNewEventFragmentUIComponents() {
        checkFirstTimeUserFragment();
        Espresso.onIdle();

        onView(withId(R.id.button_switch2)).perform(click());
        Espresso.onIdle();

        onView(withId(R.id.buttonNewEvent)).check(matches(isDisplayed()));
        Espresso.onIdle();
        // Check OrganizerCreateEventFragment UI elements
        onView(withId(R.id.buttonQrCodes)).check(matches(isDisplayed()));
        Espresso.onIdle();
        onView(withId(R.id.buttonDraw)).check(matches(isDisplayed()));
        Espresso.onIdle();
    }

    /**
     * Tests if navigation between tabs work on click
     */
    @Test
    public void testNavigationBetweenNavTabs() {
        // Get past first time user
        checkFirstTimeUserFragment();
        Espresso.onIdle();

        // Click on organizer profile tab from nav bar
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
        // Get past first time user
        checkFirstTimeUserFragment();
        Espresso.onIdle();
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