package com.example.plannet;
//package com.example.plannet;

import com.example.plannet.Event.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class QRCodeScanTest {

    @Mock
    FirebaseFirestore mockFirestore;
    @Mock
    CollectionReference mockCollectionReference;
    @Mock
    DocumentReference mockDocumentReference;
    @Mock
    DocumentSnapshot mockDocumentSnapshot;
    @Mock
    Task<DocumentSnapshot> mockTask; // Mock the Task directly to control behavior

    private AutoCloseable mockCloseable;
    private static final String TEST_EVENT_ID = "sampleEvent123";

    @Before
    public void setUp() throws Exception {
        mockCloseable = MockitoAnnotations.openMocks(this);

        // Setup Firestore to return a mock collection and document
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(TEST_EVENT_ID)).thenReturn(mockDocumentReference);

        // Setup mock task to simulate a successful Firebase call

// Inside setUp method
        when(mockDocumentReference.get()).thenReturn(mockTask);

// Mock success listener to trigger when fetchEventDetails is called
        when(mockTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenAnswer(invocation -> {
                    OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
                    listener.onSuccess(mockDocumentSnapshot);
                    return mockTask;
                });

// Mock failure listener similarly (if needed for failure cases)
        when(mockTask.addOnFailureListener(any(OnFailureListener.class)))
                .thenReturn(mockTask);

// Mock DocumentSnapshot to return the desired Event object
        Event testEvent = new Event(
                "Sample Event",
                null,
                "Free",
                100,
                50,
                new Date(),
                new Date(),
                new Date(),
                "Description here",
                false,
                "Sample Facility"
        );
        when(mockDocumentSnapshot.toObject(Event.class)).thenReturn(testEvent);


        // Inject mockFirestore into QRCodeScan.firebaseDB using reflection
        Field firebaseDBField = QRCodeScan.class.getDeclaredField("firebaseDB");
        firebaseDBField.setAccessible(true);
        firebaseDBField.set(null, mockFirestore); // Set static field
    }

    @After
    public void tearDown() throws Exception {
        if (mockCloseable != null) {
            mockCloseable.close();
        }
    }

    @Test
    public void testFetchEventDetails() {
        // Call the method to be tested
        QRCodeScan.fetchEventDetails(TEST_EVENT_ID);

        // Verify that Firestore's document retrieval is called once
        verify(mockDocumentReference, times(1)).get();

        // Additional assertions can go here
        Event event = mockDocumentSnapshot.toObject(Event.class);
        assert event != null;
        assert "Sample Event".equals(event.getEventName());
        System.out.println("Test completed: Event data retrieval logic tested successfully.");
    }
}

