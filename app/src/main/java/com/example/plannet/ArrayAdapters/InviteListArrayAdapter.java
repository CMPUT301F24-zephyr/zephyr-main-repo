package com.example.plannet.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.plannet.Notification.Invite;
import com.example.plannet.R;

import java.util.ArrayList;

public class InviteListArrayAdapter extends ArrayAdapter<Invite> {

    /**
     * Constructor for InviteArrayAdapter.
     *
     * @param context The context of the fragment (e.g., EntrantNotificationsFragment).
     * @param invites List of invites to display.
     */
    public InviteListArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Invite> invites) {
        super(context, resource, invites);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_invite, parent, false);
        }

        // Get current Invite object
        Invite invite = getItem(position);

        // Bind to views
        TextView eventTitle = convertView.findViewById(R.id.invite_title);
        TextView eventStatus = convertView.findViewById(R.id.invite_details);

        if (invite != null) {
            eventTitle.setText(invite.getEventTitle());
            eventStatus.setText(invite.getStatus());
        }

        return convertView;
    }
}
