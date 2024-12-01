package com.example.plannet.ArrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.plannet.Entrant.EntrantProfile;
import com.example.plannet.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


// Help creating this class was from labs and https://github.com/codepath/android_guides/wiki/using-an-arrayadapter-with-listview

/**
 * This is an ArrayAdapter class for the organizer entrant list for an event
 * Each item in the list is adapted to fit the listitem_organizer_entrant_list xml.
 * Help creating this class was from labs and https://github.com/codepath/android_guides/wiki/using-an-arrayadapter-with-listview
 */
public class OrganizerEntrantListArrayAdapter extends ArrayAdapter<EntrantProfile> {
    private ArrayList<EntrantProfile> unfiltered;
    private ArrayList<EntrantProfile> filtered;

    /**
     * Constructor.
     *
     * @param context
     *      The context of the fragment (The QR code list fragment)
     * @param entrants
     *      ArrayList of the EntrantProfile objects for each entrant.
     */
    public OrganizerEntrantListArrayAdapter(@NonNull Context context, ArrayList<EntrantProfile> entrants) {
        super(context, 0, entrants);
        this.unfiltered = new ArrayList<>(entrants);  // This is the original list
        this.filtered = new ArrayList<>(entrants);  // Filtered list. Initially the same as original list (since no filters are applied)
        Log.d("Entrant List Adapter", "Unfiltered/Filtered lists created. Unfiltered: " + unfiltered.toString());

    }

    /**
     * gets the count of the filtered list
     * @return
     */
    @Override
    public int getCount() {
        // We must override this function in case a filter is applied
        return filtered.size();
    }

    /**
     * gets item details from position
     * @param position
     * @return
     */
    @Nullable
    @Override
    public EntrantProfile getItem(int position) {
        // We must override this function in case a filter is applied
        return filtered.get(position);
    }

    /**
     * add all filters to a single filter
     * @param collection
     */
    @Override
    public void addAll(@NonNull Collection<? extends EntrantProfile> collection) {
        super.addAll(collection);
        // Ensure that when entrants are added, they show up in both lists (there should be no filters at this point)
        unfiltered.addAll(collection);
        filtered.addAll(collection);
        Log.d("Entrant List Adapter", "Entrant added. Unfiltered/filtered lists updated.");
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_organizer_entrant_list, parent, false);
        }

        // Get the current entrant
        EntrantProfile entrant = getItem(position);

        // Set the data to show in the TextViews
        TextView name = convertView.findViewById(R.id.name);
        TextView status = convertView.findViewById(R.id.status);
        if (entrant != null) {
            name.setText(entrant.getName());
            switch(entrant.getWaitlistStatus()){
                case "pending":
                    status.setText("Pending");
                    status.setTextColor(ContextCompat.getColor(getContext(), R.color.pending));
                    break;
                case "chosen":
                    status.setText("Chosen");
                    status.setTextColor(ContextCompat.getColor(getContext(), R.color.chosen));
                    break;
                case "enrolled":
                    status.setText("Enrolled");
                    status.setTextColor(ContextCompat.getColor(getContext(), R.color.enrolled));
                    break;
                case "declined":
                    status.setText("Declined");
                    status.setTextColor(ContextCompat.getColor(getContext(), R.color.cancelled));
                    break;
            }
        }
        else {
            name.setText("ERROR!");
            status.setText("ERROR!");
            status.setTextColor(ContextCompat.getColor(getContext(), R.color.cancelled));
        }

        return convertView;
    }

    /**
     * Allows the user to filter entrants in list by statuses based on which buttons are pressed
     *
     * @param statuses
     *      a List of all currently active buttons (filters applied)
     */
    public void filter(List<String> statuses) {
        if (statuses.isEmpty()){
            // No filters are applied. Reset filtered to be the same as unfiltered.
            filtered = new ArrayList<>(unfiltered);
        }
        else {
            filtered = new ArrayList<>();
            for (String status : statuses){
                // For each filter...
                Log.d("Entrant List Adapter", "Filtering by status: " + status);
                for (EntrantProfile entrant : unfiltered){
                    // We want to check each entrant in the unfiltered list and see if they are allowed
                    if (entrant.getWaitlistStatus().equals(status)){
                        filtered.add(entrant);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}