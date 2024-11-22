package com.example.plannet.ui.entrantprofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.plannet.Event.EventData;
import com.example.plannet.R;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends android.widget.BaseAdapter {

    private final Context context;
    private final List<EventData> eventList;
    private List<EventData> filteredEventList;

    public EventListAdapter(Context context, List<EventData> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.filteredEventList = new ArrayList<>(eventList);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the custom layout for each item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_entrant_event_list, parent, false);
        }

        // Get the event data
        EventData eventData = eventList.get(position);

        // Set up the views in your fragment_entrant_event_list.xml
        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventStatus = convertView.findViewById(R.id.event_status);
        TextView eventLocation = convertView.findViewById(R.id.event_location);

        // Set data to the views
        eventName.setText(eventData.getName());
        eventStatus.setText(eventData.getStatus());
        eventLocation.setText(eventData.getLocation());

        return convertView;
    }
}


