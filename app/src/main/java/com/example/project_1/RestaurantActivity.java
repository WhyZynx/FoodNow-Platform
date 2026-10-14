package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantActivity extends AppCompatActivity
        implements FoodAdapter.OnFoodItemClickListener {

    TextView tvName, tvAddress, tvOpenHours;
    ImageView ivCover;
    RecyclerView rvFoods;

    View layoutViewBasket;
    TextView tvTotalPrices, tvTotalItems;

    Restaurant restaurant;
    FoodAdapter foodAdapter;

    int totalItems = 0;
    int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvOpenHours = findViewById(R.id.tvOpenHours);
        ivCover = findViewById(R.id.ivCover);
        rvFoods = findViewById(R.id.rvFoods);

        layoutViewBasket = findViewById(R.id.layoutViewBasket);
        tvTotalPrices = findViewById(R.id.tvTotalPrices);
        tvTotalItems = findViewById(R.id.tvTotalItems);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        layoutViewBasket.setVisibility(View.GONE);

        layoutViewBasket.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("target_tab", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        if (restaurant != null) {
            CartManager.getInstance().setLastRestaurantName(restaurant.name);
            syncCart(restaurant.menu);

            tvName.setText(restaurant.name);
            tvAddress.setText(restaurant.address);
            tvOpenHours.setText(restaurant.getOpenHours());
            ivCover.setImageResource(restaurant.cover);

            foodAdapter = new FoodAdapter(restaurant.menu, this);

            rvFoods.setLayoutManager(new LinearLayoutManager(this));
            rvFoods.setAdapter(foodAdapter);

            updateBasketInitial();
        }
    }

    private void syncCart(ArrayList<Food> menu) {
        List<Food> cartItems = CartManager.getInstance().getCartItems();
        for (Food menuFood : menu) {
            for (Food cartFood : cartItems) {
                if (menuFood.name.equals(cartFood.name)) {
                    menuFood.quantity = cartFood.quantity;
                }
            }
        }
    }

    private void updateBasketInitial() {
        totalItems = 0;
        totalPrice = 0;
        for (Food f : restaurant.menu) {
            totalItems += f.quantity;
            totalPrice += f.price * f.quantity;
        }
        updateBasket();
    }

    @Override
    public void onFoodItemClick(Food food) {
    }

    @Override
    public void onPlusClick(Food food, int position) {
        food.quantity++;
        totalItems++;
        totalPrice += food.price;
        foodAdapter.notifyItemChanged(position);
        CartManager.getInstance().addFood(food);
        updateBasket();
    }

    @Override
    public void onMinusClick(Food food, int position) {
        if (food.quantity > 0) {
            food.quantity--;
            totalItems--;
            totalPrice -= food.price;
            foodAdapter.notifyItemChanged(position);
            CartManager.getInstance().addFood(food);
            updateBasket();
        }
    }

    private void updateBasket() {
        if (totalItems > 0) {
            layoutViewBasket.setVisibility(View.VISIBLE);
            tvTotalItems.setText(totalItems + (totalItems > 1 ? " items" : " item"));
            tvTotalPrices.setText(String.format(Locale.US, "%,d VND", totalPrice));
        } else {
            layoutViewBasket.setVisibility(View.GONE);
        }
    }
}
