package com.example.plannet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.example.plannet.MainActivity;
import com.example.plannet.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerTest {
    /**
     * testing organizer functionality exclusively
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    // for bypassing notif popup and navigating to Org fragment
    @Before
    public void preSetup() {
        // Handle notification permission popup
        Espresso.onIdle();

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        if (device.hasObject(By.text("Allow"))) {
            Espresso.onIdle();

            device.findObject(By.text("Allow")).click();
            Espresso.onIdle();

        }

        // Navigate to OrganizerHomeFragment
        Espresso.onIdle();
        onView(withId(R.id.button_switch2)).perform(click());
        Espresso.onIdle();


//        onView(withId(R.id.buttonDraw)).check(matches(isDisplayed()));
    }

    /**
     * testing navigation to entrant home
     */
    @Test
    public void testNavigationToEntrantHomeFragment() {
        // back to entrant fragment
        onView(withId(R.id.button_switch)).perform(click());

        onView(withId(R.id.scan_qr_button))
                .check(matches(isDisplayed()));
    }
    /**
     * testing navigation to create event fragment
     */
    @Test
    public void testOrganizerCreateEventFragment() {
        onView(withId(R.id.buttonNewEvent)).perform(click());

        onView(withId(R.id.generate_qr_button)).check(matches(isDisplayed()));
    }

    /**
     * testing admin button is invisible
     */
    @Test
    public void testAdminButtonVisibility() {
        // should be hidden since emulator IP is not in firebase (hidden by default)
        onView(withId(R.id.button_admin)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    /**
     * testing facility name display at top
     */
    @Test
    public void testFacilityNameDisplay() {
        onView(withId(R.id.title)).check(matches(withText("Facility: Not Set"))); // Default text
    }

    /**
     * simulate creating an event
     */
    @Test
    public void testCreateEvent() {
        // goto to OrganizerCreateEventFragment
        onView(withId(R.id.buttonNewEvent)).perform(click());

        // event details
        onView(withId(R.id.name_edit)).perform(typeText("Test Event"));
        onView(withId(R.id.price_edit)).perform(typeText("50"));
        onView(withId(R.id.max_entrants_edit)).perform(typeText("100"));
        onView(withId(R.id.description)).perform(typeText("This is a test event."));
        onView(withId(R.id.runtime_start_edit)).perform(typeText("01/01/2024"));
        onView(withId(R.id.runtime_end_edit)).perform(typeText("01/02/2024"));

        // simulate click on generate QR button
        onView(withId(R.id.generate_qr_button)).perform(click());

        onView(withId(R.id.toast_qr_image)).check(matches(isDisplayed()));
    }

    /**
     * Tests if creating a facility dialog shows and creates for null facilities
     */
    @Test
    public void testCreateFacility() {
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