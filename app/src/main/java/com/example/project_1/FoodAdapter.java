package com.example.project_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    public interface OnFoodItemClickListener {
        void onFoodItemClick(Food food);
        void onPlusClick(Food food, int position);
        void onMinusClick(Food food, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFood;
        TextView tvFoodName, tvFoodPrice, tvPlus, tvMinus, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFood = itemView.findViewById(R.id.ivFood);
            tvPlus = itemView.findViewById(R.id.tvPlus);
            tvMinus = itemView.findViewById(R.id.tvMinus);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
        }
    }

    private List<Food> mFoods;
    private OnFoodItemClickListener mListener;

    public FoodAdapter(
            List<Food> foods,
            OnFoodItemClickListener listener) {

        mFoods = foods;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_food, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Food food = mFoods.get(position);

        holder.ivFood.setImageResource(food.image);
        holder.tvFoodName.setText(food.name);
        holder.tvFoodPrice.setText(String.format("%,d VND", food.price));

        if (food.quantity > 0) {
            holder.tvQuantity.setText(String.valueOf(food.quantity));
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvMinus.setVisibility(View.VISIBLE);
        } else {
            holder.tvQuantity.setVisibility(View.INVISIBLE);
            holder.tvMinus.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v ->
                mListener.onFoodItemClick(food)
        );

        holder.tvPlus.setOnClickListener(v ->
                mListener.onPlusClick(food, position)
        );

        holder.tvMinus.setOnClickListener(v ->
                mListener.onMinusClick(food, position)
        );
    }

    @Override
    public int getItemCount() {
        return mFoods.size();
    }
}
