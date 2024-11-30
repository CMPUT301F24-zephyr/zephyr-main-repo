package com.example.plannet.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.plannet.R;

import java.util.ArrayList;
import java.util.Map;

public class AdminEventListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> eventNames;
    private final Map<String, String> nameToIdMap;

    public AdminEventListAdapter(Context context, ArrayList<String> eventNames, Map<String, String> nameToIdMap) {
        super(context, 0, eventNames);
        this.context = context;
        this.eventNames = eventNames;
        this.nameToIdMap = nameToIdMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_admin_events, parent, false);
        }

        TextView eventNameView = convertView.findViewById(R.id.event_name);
        TextView eventIdView = convertView.findViewById(R.id.event_id);

        String eventName = eventNames.get(position);
        String eventId = nameToIdMap.get(eventName);

        eventNameView.setText(eventName);
        eventIdView.setText(eventId);

        return convertView;
    }
}