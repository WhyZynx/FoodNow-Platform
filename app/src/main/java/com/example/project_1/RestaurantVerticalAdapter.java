package com.example.project_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantVerticalAdapter extends RecyclerView.Adapter<RestaurantVerticalAdapter.ViewHolder> {

    public interface OnRestaurantItemClickListener {
        void onRestaurantItemClick(Restaurant restaurant);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvRating;
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }

    private List<Restaurant> mRestaurants;
    private OnRestaurantItemClickListener mListener;

    public RestaurantVerticalAdapter(List<Restaurant> restaurants, OnRestaurantItemClickListener listener) {
        mRestaurants = restaurants;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_restaurant_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Restaurant restaurant = mRestaurants.get(position);

        viewHolder.tvName.setText(restaurant.name);
        if (restaurant.logo != 0) {
            viewHolder.ivImage.setImageResource(restaurant.logo);
            viewHolder.ivImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else if (restaurant.cover != 0) {
            viewHolder.ivImage.setImageResource(restaurant.cover);
            viewHolder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        if (restaurant.address != null && !restaurant.address.isEmpty()) {
            viewHolder.tvAddress.setText(restaurant.address);
        } else {
            viewHolder.tvAddress.setText("20–30 min");
        }
        viewHolder.tvRating.setText(restaurant.rating != null ? restaurant.rating : "4.8 (320)");

        viewHolder.itemView.setOnClickListener(v -> mListener.onRestaurantItemClick(restaurant));
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }
}
