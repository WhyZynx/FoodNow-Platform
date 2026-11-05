package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantFragment extends Fragment implements RestaurantVerticalAdapter.OnRestaurantItemClickListener {

    RecyclerView rvRestaurant;
    TextView tvEmptyRestaurants;
    RestaurantVerticalAdapter adapter;
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
        tvEmptyRestaurants = view.findViewById(R.id.tvEmptyRestaurants);
        restaurantList = new ArrayList<>();
        fullRestaurantList = new ArrayList<>();

        adapter = new RestaurantVerticalAdapter(restaurantList, this);
        rvRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRestaurant.setAdapter(adapter);

        loadRestaurantsFromApi();

        return view;
    }

    private void loadRestaurantsFromApi() {
        ApiConfig.getRestaurantsApi(new ApiConfig.ApiCallback<List<ApiConfig.RestaurantModel>>() {
            @Override
            public void onSuccess(List<ApiConfig.RestaurantModel> models) {
                if (!isAdded() || getView() == null) return;
                if (models != null) {
                    fullRestaurantList.clear();
                    for (ApiConfig.RestaurantModel model : models) {
                        int logoRes = R.drawable.ic_circlek;
                        int coverRes = R.drawable.cover_menu_1;
                        String rname = model.name != null ? model.name.toLowerCase() : "";
                        if (rname.contains("highland") || rname.contains("coffee")) {
                            logoRes = R.drawable.ic_highland;
                        } else if (rname.contains("ministop")) {
                            logoRes = R.drawable.ic_ministop;
                        } else if (rname.contains("7") || rname.contains("eleven") || rname.contains("seven")) {
                            logoRes = R.drawable.ic_seveneleven;
                        } else if (rname.contains("vin") || rname.contains("winmart") || rname.contains("vinmart")) {
                            logoRes = R.drawable.ic_vinmart;
                        } else if (rname.contains("circle") || rname.contains("circlek")) {
                            logoRes = R.drawable.ic_circlek;
                        }

                        Restaurant r = new Restaurant(model.id, model.name, logoRes, coverRes, model.address, model.open_hours);

                        ApiConfig.getFoodsApi(model.id, new ApiConfig.ApiCallback<List<ApiConfig.FoodModel>>() {
                            @Override
                            public void onSuccess(List<ApiConfig.FoodModel> foodModels) {
                                if (!isAdded() || getView() == null) return;
                                ArrayList<Food> menu = new ArrayList<>();
                                if (foodModels == null) return;
                                for (ApiConfig.FoodModel fm : foodModels) {
                                    int img = R.drawable.dish;
                                    String fname = fm.name != null ? fm.name.toLowerCase() : "";
                                    if (fname.contains("hot dog") || fname.contains("hotdog")) img = R.drawable.hotdog;
                                    else if (fname.contains("cheese burger") || fname.contains("special burger")) img = R.drawable.cheese_burger;
                                    else if (fname.contains("shrimp")) img = R.drawable.shrimp_burger;
                                    else if (fname.contains("burger")) img = R.drawable.burger;
                                    else if (fname.contains("noodles") || fname.contains("mì")) img = R.drawable.mixed_noodle;
                                    else if (fname.contains("boba") || fname.contains("trà sữa")) img = R.drawable.boba;
                                    else if (fname.contains("drink") || fname.contains("soft")) img = R.drawable.ic_milk_tea;
                                    else if (fname.contains("coffee") || fname.contains("cà phê")) img = R.drawable.milk_coffee;
                                    else if (fname.contains("black coffee")) img = R.drawable.ic_black_coffee;
                                    else if (fname.contains("matcha")) img = R.drawable.matcha_latte;
                                    else if (fname.contains("strawberry")) img = R.drawable.strawberry_latte;
                                    else if (fname.contains("cake") || fname.contains("mousse")) img = R.drawable.lemon_mousse;
                                    else if (fname.contains("takoyaki")) img = R.drawable.takoyaki;
                                    else if (fname.contains("tokbokki")) img = R.drawable.tokbokki;
                                    else if (fname.contains("sandwich")) img = R.drawable.sandwich;
                                    else if (fname.contains("spaghetti")) img = R.drawable.spaghetti;
                                    else if (fname.contains("tacos")) img = R.drawable.tacos;
                                    else if (fname.contains("banh mi") || fname.contains("bánh mì")) img = R.drawable.ic_banh_mi;
                                    else if (fname.contains("sweet potato")) img = R.drawable.sweet_potato;
                                    else if (fname.contains("tart")) img = R.drawable.tart;
                                    else if (fname.contains("fruit")) img = R.drawable.tropical_fruit;
                                    else if (fname.contains("phở")) img = R.drawable.bowl;

                                    menu.add(new Food(fm.id, model.id, fm.name, img, (int) fm.price));
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
                    if (restaurantList.isEmpty()) {
                        if (tvEmptyRestaurants != null) tvEmptyRestaurants.setVisibility(View.VISIBLE);
                        if (rvRestaurant != null) rvRestaurant.setVisibility(View.GONE);
                    } else {
                        if (tvEmptyRestaurants != null) tvEmptyRestaurants.setVisibility(View.GONE);
                        if (rvRestaurant != null) rvRestaurant.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded() || getView() == null) return;
                if (tvEmptyRestaurants != null) tvEmptyRestaurants.setVisibility(View.VISIBLE);
                if (rvRestaurant != null) rvRestaurant.setVisibility(View.GONE);
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
