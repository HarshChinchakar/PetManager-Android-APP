package com.pets.dog.cat.petmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView; // Changed from FrameLayout
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private Context profileAdapterContext;
    private List<Profile> profileList;
    private List<Integer> selectedPositions = new ArrayList<>();

    public ProfileAdapter(Context profileAdapterContext, List<Profile> profileList) {
        this.profileAdapterContext = profileAdapterContext;
        this.profileList = profileList;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // UPDATED: Now inflating the new "Flashy" Card Layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_profile_card, parent, false);

        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        // 1. Load Image
        try {
            Bitmap profilePBitmap = decodeUri(profileAdapterContext, Uri.parse(profileList.get(position).getProfilePictureUri()), 100); // Increased size slightly for quality
            holder.profilePictureImageIV.setImageBitmap(profilePBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. Set Name
        holder.nameTvw.setText(profileList.get(position).getName());

        // 3. Handle Selection (Highlighting)
        // Since CardView extends FrameLayout, setForeground works perfectly
        if (selectedPositions.contains(position)) {
            // Show a highlight overlay if selected
            holder.rootCardView.setForeground(new ColorDrawable(ContextCompat.getColor(profileAdapterContext, R.color.recyclerViewItemSelectionColor)));
        } else {
            // Remove overlay (Standard "Selectable Item" Ripple is handled in XML)
            holder.rootCardView.setForeground(ContextCompat.getDrawable(profileAdapterContext, android.R.drawable.list_selector_background));
        }
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    // --- UPDATED VIEW HOLDER ---
    protected class ProfileViewHolder extends RecyclerView.ViewHolder {
        protected ImageView profilePictureImageIV;
        protected TextView nameTvw;
        protected CardView rootCardView; // Changed from FrameLayout to CardView

        public ProfileViewHolder(View view) {
            super(view);
            // UPDATED: Finding the new IDs from row_profile_card.xml
            profilePictureImageIV = view.findViewById(R.id.profile_list_image_view);
            nameTvw = view.findViewById(R.id.profile_list_name_text_view);
            rootCardView = view.findViewById(R.id.profile_card_root);
        }
    }

    public void setSelectedPositions(int previousPosition, List<Integer> selectedPositions) {
        this.selectedPositions = selectedPositions;

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }

        if (this.selectedPositions.size() > 0) {
            notifyItemChanged(this.selectedPositions.get(0));
        }
    }

    public Profile getItem(int position) {
        return profileList.get(position);
    }

    public Bitmap decodeUri(Context context, Uri uri, final int requiredSize) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o2);
    }
}