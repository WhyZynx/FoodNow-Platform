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
import java.util.List;

public class RestaurantFragment extends Fragment implements RestaurantAdapter.OnRestaurantItemClickListener {

    RecyclerView rvRestaurant;
    RestaurantAdapter adapter;
    ArrayList<Restaurant> restaurantList;
    ArrayList<Restaurant> fullRestaurantList;

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
        restaurantList = new ArrayList<>();
        fullRestaurantList = new ArrayList<>();

        initLocalData();
        restaurantList.addAll(fullRestaurantList);

        adapter = new RestaurantAdapter(restaurantList, this);
        rvRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRestaurant.setAdapter(adapter);

        view.findViewById(R.id.layoutCategoryBurger).setOnClickListener(v -> openCategory("Burger"));
        view.findViewById(R.id.layoutCategoryMilkTea).setOnClickListener(v -> openCategory("Trà sữa"));
        view.findViewById(R.id.layoutCategoryDessert).setOnClickListener(v -> openCategory("Tráng miệng"));
        view.findViewById(R.id.layoutCategorySpicy).setOnClickListener(v -> openCategory("Cay"));

        loadRestaurantsFromApi();

        return view;
    }

    private void openCategory(String categoryName) {
        Intent intent = new Intent(getContext(), CategoryActivity.class);
        intent.putExtra("category_name", categoryName);
        intent.putExtra("restaurants_list", fullRestaurantList);
        startActivity(intent);
    }

    private void initLocalData() {
        fullRestaurantList.clear();

        Restaurant circleK = new Restaurant("Circle K", R.drawable.ic_circlek, R.drawable.cover_menu_1, "123 Nguyen Hue St, District 1", "00:00 - 23:59");
        ArrayList<Food> menuCircleK = new ArrayList<>();
        menuCircleK.add(new Food("Classic Hot Dog", R.drawable.hotdog, 25000));
        menuCircleK.add(new Food("Cheese Burger", R.drawable.cheese_burger, 35000));
        menuCircleK.add(new Food("Mixed Noodles", R.drawable.mixed_noodle, 25000));
        menuCircleK.add(new Food("Soft Drink", R.drawable.boba, 12000));
        circleK.setMenu(menuCircleK);
        fullRestaurantList.add(circleK);

        Restaurant highland = new Restaurant("Highland Coffee", R.drawable.ic_highland, R.drawable.cover_menu_1, "456 Le Loi St, District 1", "07:00 - 22:00");
        ArrayList<Food> menuHighland = new ArrayList<>();
        menuHighland.add(new Food("Premium Milk Coffee", R.drawable.milk_coffee, 39000));
        menuHighland.add(new Food("Matcha Latte", R.drawable.matcha_latte, 49000));
        menuHighland.add(new Food("Strawberry Latte", R.drawable.strawberry_latte, 49000));
        menuHighland.add(new Food("Lemon Mousse Cake", R.drawable.lemon_mousse, 35000));
        menuHighland.add(new Food("Black Coffee", R.drawable.ic_black_coffee, 29000));
        highland.setMenu(menuHighland);
        fullRestaurantList.add(highland);

        Restaurant miniStop = new Restaurant("MiniStop", R.drawable.ic_ministop, R.drawable.cover_menu_1, "789 CMT8 St, District 3", "00:00 - 23:59");
        ArrayList<Food> menuMiniStop = new ArrayList<>();
        menuMiniStop.add(new Food("Osaka Takoyaki", R.drawable.takoyaki, 30000));
        menuMiniStop.add(new Food("Spicy Tokbokki", R.drawable.tokbokki, 35000));
        menuMiniStop.add(new Food("Shrimp Burger", R.drawable.shrimp_burger, 32000));
        menuMiniStop.add(new Food("Special Burger", R.drawable.burger, 40000));
        miniStop.setMenu(menuMiniStop);
        fullRestaurantList.add(miniStop);

        Restaurant sevenEleven = new Restaurant("7 Eleven", R.drawable.ic_seveneleven, R.drawable.cover_menu_1, "101 Ton Duc Thang St, District 1", "00:00 - 23:59");
        ArrayList<Food> menuSeven = new ArrayList<>();
        menuSeven.add(new Food("Club Sandwich", R.drawable.sandwich, 28000));
        menuSeven.add(new Food("Spaghetti Meatball", R.drawable.spaghetti, 45000));
        menuSeven.add(new Food("Beef Tacos", R.drawable.tacos, 35000));
        menuSeven.add(new Food("Saigon Banh Mi", R.drawable.ic_banh_mi, 30000));
        sevenEleven.setMenu(menuSeven);
        fullRestaurantList.add(sevenEleven);

        Restaurant vinMart = new Restaurant("VinMart", R.drawable.ic_vinmart, R.drawable.cover_menu_1, "202 Pham Ngu Lao St, District 1", "08:00 - 22:00");
        ArrayList<Food> menuVin = new ArrayList<>();
        menuVin.add(new Food("Grilled Sweet Potato", R.drawable.sweet_potato, 20000));
        menuVin.add(new Food("Fresh Egg Tart", R.drawable.tart, 15000));
        menuVin.add(new Food("Pre-cut Tropical Fruit", R.drawable.tropical_fruit, 25000));
        menuVin.add(new Food("Healthy Bowl", R.drawable.bowl, 45000));
        vinMart.setMenu(menuVin);
        fullRestaurantList.add(vinMart);
    }

    private void loadRestaurantsFromApi() {
        ApiConfig.getRestaurantsApi(new ApiConfig.ApiCallback<List<ApiConfig.RestaurantModel>>() {
            @Override
            public void onSuccess(List<ApiConfig.RestaurantModel> models) {
                if (models != null && !models.isEmpty()) {
                    fullRestaurantList.clear();
                    for (ApiConfig.RestaurantModel model : models) {
                        int logoRes = R.drawable.ic_circlek;
                        String rname = model.name.toLowerCase();
                        if (rname.contains("highland")) {
                            logoRes = R.drawable.ic_highland;
                        } else if (rname.contains("ministop")) {
                            logoRes = R.drawable.ic_ministop;
                        } else if (rname.contains("7") || rname.contains("eleven")) {
                            logoRes = R.drawable.ic_seveneleven;
                        } else if (rname.contains("vin")) {
                            logoRes = R.drawable.ic_vinmart;
                        } else if (rname.contains("phở") || rname.contains("bánh mì")) {
                            logoRes = R.drawable.ic_food;
                        }

                        Restaurant r = new Restaurant(model.name, logoRes, R.drawable.cover_menu_1, model.address, model.open_hours);

                        ApiConfig.getFoodsApi(model.id, new ApiConfig.ApiCallback<List<ApiConfig.FoodModel>>() {
                            @Override
                            public void onSuccess(List<ApiConfig.FoodModel> foodModels) {
                                ArrayList<Food> menu = new ArrayList<>();
                                for (ApiConfig.FoodModel fm : foodModels) {
                                    int img = R.drawable.dish;
                                    String fname = fm.name.toLowerCase();
                                    if (fname.contains("hot dog")) img = R.drawable.hotdog;
                                    else if (fname.contains("cheese burger") || fname.contains("special burger")) img = R.drawable.cheese_burger;
                                    else if (fname.contains("burger")) img = R.drawable.burger;
                                    else if (fname.contains("noodles") || fname.contains("mì")) img = R.drawable.mixed_noodle;
                                    else if (fname.contains("drink") || fname.contains("soft")) img = R.drawable.boba;
                                    else if (fname.contains("coffee") || fname.contains("cà phê")) img = R.drawable.milk_coffee;
                                    else if (fname.contains("black coffee")) img = R.drawable.ic_black_coffee;
                                    else if (fname.contains("matcha")) img = R.drawable.matcha_latte;
                                    else if (fname.contains("strawberry")) img = R.drawable.strawberry_latte;
                                    else if (fname.contains("cake") || fname.contains("mousse")) img = R.drawable.lemon_mousse;
                                    else if (fname.contains("takoyaki")) img = R.drawable.takoyaki;
                                    else if (fname.contains("tokbokki")) img = R.drawable.tokbokki;
                                    else if (fname.contains("shrimp")) img = R.drawable.shrimp_burger;
                                    else if (fname.contains("sandwich")) img = R.drawable.sandwich;
                                    else if (fname.contains("spaghetti")) img = R.drawable.spaghetti;
                                    else if (fname.contains("tacos")) img = R.drawable.tacos;
                                    else if (fname.contains("banh mi") || fname.contains("bánh mì")) img = R.drawable.ic_banh_mi;
                                    else if (fname.contains("sweet potato")) img = R.drawable.sweet_potato;
                                    else if (fname.contains("tart")) img = R.drawable.tart;
                                    else if (fname.contains("fruit")) img = R.drawable.tropical_fruit;
                                    else if (fname.contains("phở")) img = R.drawable.bowl;

                                    menu.add(new Food(fm.name, img, (int) fm.price));
                                }
                                r.setMenu(menu);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(String errorMessage) {
                            }
                        });

                        fullRestaurantList.add(r);
                    }
                    restaurantList.clear();
                    restaurantList.addAll(fullRestaurantList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    @Override
    public void onRestaurantItemClick(Restaurant restaurant) {
        Intent intent = new Intent(getContext(), RestaurantActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }
}
