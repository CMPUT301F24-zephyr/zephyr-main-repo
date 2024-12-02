package com.example.plannet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * first time user fragment testing if userID is not in DB
 */
@RunWith(AndroidJUnit4.class)
public class FirstTimeUserFragmentTest {
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
     * Tests if the FirstTimeUser fragment shows up for new users
     */
    @Test
    public void checkFirstTimeUserFragment() {
        // since we're using a new android ID, it should display new user page
        onView(withId(R.id.button_welcome)).check(matches(isDisplayed()));
        // a wait command for the previous query to finish
        Espresso.onIdle();

    }
}
