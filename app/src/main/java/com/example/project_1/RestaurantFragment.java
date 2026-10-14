package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RestaurantFragment extends Fragment implements RestaurantAdapter.OnRestaurantItemClickListener {

    RecyclerView rvRestaurant;
    RestaurantAdapter adapter;
    ArrayList<Restaurant> restaurantList;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_restaurant,
                container,
                false
        );

        rvRestaurant = view.findViewById(R.id.rvRestaurant);
        initData();
        adapter = new RestaurantAdapter(restaurantList, this);
        rvRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRestaurant.setAdapter(adapter);

        return view;
    }

    private void initData() {
        restaurantList = new ArrayList<>();

        Restaurant circleK = new Restaurant("Circle K", R.drawable.ic_circlek, R.drawable.cover_menu_1, "123 Nguyen Hue St, District 1", "00:00 - 23:59");
        ArrayList<Food> menuCircleK = new ArrayList<>();
        menuCircleK.add(new Food("Classic Hot Dog", R.drawable.hotdog, 25000));
        menuCircleK.add(new Food("Cheese Burger", R.drawable.cheese_burger, 35000));
        menuCircleK.add(new Food("Mixed Noodles", R.drawable.mixed_noodle, 25000));
        menuCircleK.add(new Food("Soft Drink", R.drawable.ic_milk_tea, 12000));
        circleK.setMenu(menuCircleK);
        restaurantList.add(circleK);

        Restaurant highland = new Restaurant("Highland Coffee", R.drawable.ic_highland, R.drawable.cover_menu_1, "456 Le Loi St, District 1", "07:00 - 22:00");
        ArrayList<Food> menuHighland = new ArrayList<>();
        menuHighland.add(new Food("Premium Milk Coffee", R.drawable.milk_coffee, 39000));
        menuHighland.add(new Food("Matcha Latte", R.drawable.matcha_latte, 49000));
        menuHighland.add(new Food("Strawberry Latte", R.drawable.strawberry_latte, 49000));
        menuHighland.add(new Food("Lemon Mousse Cake", R.drawable.lemon_mousse, 35000));
        highland.setMenu(menuHighland);
        restaurantList.add(highland);

        Restaurant miniStop = new Restaurant("MiniStop", R.drawable.ic_ministop, R.drawable.cover_menu_1, "789 CMT8 St, District 3", "00:00 - 23:59");
        ArrayList<Food> menuMiniStop = new ArrayList<>();
        menuMiniStop.add(new Food("Osaka Takoyaki", R.drawable.takoyaki, 30000));
        menuMiniStop.add(new Food("Spicy Tokbokki", R.drawable.tokbokki, 35000));
        menuMiniStop.add(new Food("Shrimp Burger", R.drawable.shrimp_burger, 32000));
        miniStop.setMenu(menuMiniStop);
        restaurantList.add(miniStop);

        Restaurant sevenEleven = new Restaurant("7 Eleven", R.drawable.ic_seveneleven, R.drawable.cover_menu_1, "101 Ton Duc Thang St, District 1", "00:00 - 23:59");
        ArrayList<Food> menuSeven = new ArrayList<>();
        menuSeven.add(new Food("Club Sandwich", R.drawable.sandwich, 28000));
        menuSeven.add(new Food("Spaghetti Meatball", R.drawable.spaghetti, 45000));
        menuSeven.add(new Food("Beef Tacos", R.drawable.tacos, 35000));
        sevenEleven.setMenu(menuSeven);
        restaurantList.add(sevenEleven);

        Restaurant vinMart = new Restaurant("VinMart", R.drawable.ic_vinmart, R.drawable.cover_menu_1, "202 Pham Ngu Lao St, District 1", "08:00 - 22:00");
        ArrayList<Food> menuVin = new ArrayList<>();
        menuVin.add(new Food("Grilled Sweet Potato", R.drawable.sweet_potato, 20000));
        menuVin.add(new Food("Fresh Egg Tart", R.drawable.tart, 15000));
        menuVin.add(new Food("Pre-cut Tropical Fruit", R.drawable.tropical_fruit, 25000));
        vinMart.setMenu(menuVin);
        restaurantList.add(vinMart);
    }

    @Override
    public void onRestaurantItemClick(Restaurant restaurant) {
        Intent intent = new Intent(getContext(), RestaurantActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }
}
