package com.example.plannet.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.plannet.R;

import java.util.ArrayList;

public class AdminImageListAdapter extends ArrayAdapter<String> {
    /**
     * Adapter for images (posters/avatars) to display in list dialog
     */
    private Context context;
    private ArrayList<String> imageUrls;

    public AdminImageListAdapter(Context context, ArrayList<String> imageUrls) {
        super(context, R.layout.admin_image_list_item, imageUrls);
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.admin_image_list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image_thumbnail);

        String imageUrl = imageUrls.get(position);

        // connect the image into the imageview with Glide
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        return convertView;
    }
}
