package com.example.plannet.ArrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.plannet.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminUserListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> userIDs;


    public AdminUserListAdapter(Context context, ArrayList<String> userIDs) {
        super(context, R.layout.listitem_admin_users, userIDs);
        this.context = context;
        this.userIDs = userIDs;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_admin_users, parent, false);
        }
        // guaranteed it exists
        TextView userIdView = convertView.findViewById(R.id.user_id);
        // loop through DB and find out
        TextView userNameView = convertView.findViewById(R.id.user_name);

        String userID = userIDs.get(position);
        userIdView.setText(userID);

        // find this on DB if it exists
        findUserName(userID, userNameView);
        //userNameView.setText("Unknown User");

        return convertView;
    }

    private void findUserName(String userID, TextView userNameView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");
        DocumentReference userDoc = usersCollection.document(userID);
        CollectionReference userInfoCollection = userDoc.collection("userInfo");
        DocumentReference profileDoc = userInfoCollection.document("profile");
        profileDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");

                String fullName = "";
                if (firstName != null) {
                    fullName += firstName;
                }
                if (lastName != null) {
                    fullName += " " + lastName;
                }

                userNameView.setText(fullName.trim());
            } else {
                Log.d("AdminUserAdapter", "Profile document does not exist.");
                userNameView.setText("Unknown User");
            }
        }).addOnFailureListener(e -> {
            Log.e("AdminUserAdapter", "Error fetching profile document: " + e.getMessage());
            userNameView.setText("Error");
        });
    }
}