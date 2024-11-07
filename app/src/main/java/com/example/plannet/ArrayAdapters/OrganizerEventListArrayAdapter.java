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
public class OrganizerEventListArrayAdapter extends ArrayAdapter<Event> {

    public OrganizerEventListArrayAdapter(Context context, ArrayList<Event> events) { super(context, 0, events); }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_organizer_event_list, parent, false);
        }
        Event event = getItem(position);
        TextView eventID = convertView.findViewById(R.id.hashed_qr_data);
        TextView eventName = convertView.findViewById(R.id.event_details);
        eventID.setText(event.getEventID());
        eventName.setText(event.getEventName());
        return super.getView(position, convertView, parent);
    }
}