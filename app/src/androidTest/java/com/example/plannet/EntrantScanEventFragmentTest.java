package com.example.plannet;
//package com.example.plannet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.plannet.Event.Event;
import com.example.plannet.ui.entranthome.EntrantScanEventFragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Date;

/**
 * tests related to entrants scanning an event + details
 */
@RunWith(AndroidJUnit4.class)
public class EntrantScanEventFragmentTest {

    private FirebaseFirestore mockFirestore;

    @Before
    public void setUp() {
        // Initialize Mock Firebase Firestore
        mockFirestore = mock(FirebaseFirestore.class);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        when(mockFirestore.getFirestoreSettings()).thenReturn(settings);
    }

    @Test
    public void testFetchEventDetailsWithoutScanningQRCode() {
        // Set up a mock DocumentSnapshot for the Firebase data
        DocumentSnapshot mockSnapshot = mock(DocumentSnapshot.class);
        Event mockEvent = new Event(
                "Sample Event",
                "$50",
                100,
                10,
                new Date(),
                new Date(),
                new Date(),
                "Sample Description",
                false,
                "Sample Facility"
        );
        when(mockSnapshot.toObject(Event.class)).thenReturn(mockEvent);

        // Mock Firestore response to return our mock event
        when(mockFirestore.collection("events")
                .document("sampleEventID")
                .get())
                .thenReturn(Mockito.mock(Task.class));

        // Commented this out for now - Moe
//        // Launch the Fragment with mocked Firestore
//        FragmentScenario<EntrantScanEventFragment> scenario =
//                FragmentScenario.launchInContainer(EntrantScanEventFragment.class, null, R.style.Theme_PlanNet);
//
//        // Simulate calling fetchEventDetails directly without scanning
//        scenario.onFragment(fragment -> {
//            fragment.fetchEventDetails("sampleEventID");
//        });

        // Verify that the EventDetailsFragment opens with the mock event data
        onView(withId(R.id.title))  // Assuming 'event_title' is in EventDetailsFragment
                .check(matches(withText("Sample Event")));
        onView(withId(R.id.facility_name))
                .check(matches(withText("Sample Facility")));
        onView(withId(R.id.cost))
                .check(matches(withText("$50")));
    }
}


