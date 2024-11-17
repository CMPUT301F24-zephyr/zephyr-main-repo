package com.example.plannet.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.plannet.Event.Event;
import com.example.plannet.R;

import java.util.ArrayList;


// Help creating this class was from labs and https://github.com/codepath/android_guides/wiki/using-an-arrayadapter-with-listview

/**
 * This is an ArrayAdapter class for the organizer event/QR code list.
 * Each item in the list is adapted to fit the listitem_organizer_event_list xml, with the hashed qr code (Event ID)
 * at the top and the event name at the bottom.
 * Help creating this class was from labs and https://github.com/codepath/android_guides/wiki/using-an-arrayadapter-with-listview
 */
public class OrganizerEventListArrayAdapter extends ArrayAdapter<Event> {

    /**
     * Constructor.
     *
     * @param context
     *      The context of the fragment (The QR code list fragment)
     * @param events
     *      ArrayList of the Event objects for each event.
     */
    public OrganizerEventListArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    /**
     * getView method that appears in every ArrayAdapter.
     *
     * @param position
     *      The position of an item within the adapter's array.
     * @param convertView
     *      The view that we are adapting the info to.
     * @param parent
     *      The parent of this view (the list that the elements are in).
     * @return
     *      The view with adapted information that the ArrayAdapter is editing.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the custom layout if convertView is null
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_organizer_event_list, parent, false);
        }

        // Get the current QR code hash (The event ID)
        Event event = getItem(position);

        // Set the data to show in the TextView
        TextView qrData = convertView.findViewById(R.id.hashed_qr_data);
        TextView eventDetailsTextView = convertView.findViewById(R.id.event_details);
        if (event != null) {
            qrData.setText(event.getEventID());
            eventDetailsTextView.setText(event.getEventName());
        }

        return convertView;
    }
}