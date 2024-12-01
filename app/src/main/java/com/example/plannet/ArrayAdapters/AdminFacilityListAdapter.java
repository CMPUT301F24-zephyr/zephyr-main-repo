package com.example.plannet.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.plannet.Organizer.Facility;
import com.example.plannet.R;

import java.util.ArrayList;

public class AdminFacilityListAdapter extends ArrayAdapter<Facility> {
    /**
     * adapter class for displaying facilities in admin mode
     */
    private final Context context;
    private final ArrayList<Facility> facilities;


    public AdminFacilityListAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
        this.context = context;
        this.facilities = facilities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_admin_facility, parent, false);
        }

        Facility facility = facilities.get(position);

        TextView nameView = convertView.findViewById(R.id.facility_name);
        TextView locationView = convertView.findViewById(R.id.facility_location);

        nameView.setText(facility.getFacilityName());
        locationView.setText(facility.getFacilityLocation());

        return convertView;
    }
}