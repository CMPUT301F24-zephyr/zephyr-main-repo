package com.example.plannet;
import static org.mockito.Mockito.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.plannet.Event.Event;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class QRCodeScanTest {

    @Mock
    FirebaseFirestore mockFirestore;
    @Mock
    DocumentSnapshot mockDocumentSnapshot;

    private QRCodeScan qrCodeScan;
    private static final String TEST_EVENT_ID = "sampleEvent123";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        qrCodeScan = new QRCodeScan();
        qrCodeScan.firebaseDB = mockFirestore;  // Inject mocked Firestore into QRCodeScan
    }

    @Test
    public void testFetchEventDetails() {
        // Assume mockDocumentSnapshot is set up to return the correct event data
        Mockito.when(mockFirestore.collection("events").document(TEST_EVENT_ID).get())
                .thenReturn(Tasks.forResult(mockDocumentSnapshot));

        // Mock the event details in the DocumentSnapshot
        Mockito.when(mockDocumentSnapshot.toObject(Event.class)).thenReturn(new Event(
                "Sample Event",
                null, // Placeholder for the Image object, use a mock if necessary
                "Free",
                100,
                50,
                new Date(),
                new Date(),
                new Date(),
                "Description here",
                false,
                "Sample Facility"
        ));

        // Call the method to be tested
        qrCodeScan.fetchEventDetails(TEST_EVENT_ID);

        // Verify that the data was retrieved successfully (add assertions as needed)
        Mockito.verify(mockFirestore.collection("events").document(TEST_EVENT_ID).get(), Mockito.times(1));
        System.out.println("Test completed: Event data retrieved successfully.");
    }
}
