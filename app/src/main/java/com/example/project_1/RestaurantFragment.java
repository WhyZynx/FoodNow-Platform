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
        adapter = new RestaurantAdapter(restaurantList, this);
        rvRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRestaurant.setAdapter(adapter);

        loadRestaurantsFromApi();

        return view;
    }

    private void loadRestaurantsFromApi() {
        ApiConfig.getRestaurantsApi(new ApiConfig.ApiCallback<List<ApiConfig.RestaurantModel>>() {
            @Override
            public void onSuccess(List<ApiConfig.RestaurantModel> models) {
                restaurantList.clear();
                for (ApiConfig.RestaurantModel model : models) {
                    int logoRes = R.drawable.ic_circlek;
                    if (model.name.toLowerCase().contains("highland")) {
                        logoRes = R.drawable.ic_highland;
                    } else if (model.name.toLowerCase().contains("ministop")) {
                        logoRes = R.drawable.ic_ministop;
                    } else if (model.name.toLowerCase().contains("7") || model.name.toLowerCase().contains("eleven")) {
                        logoRes = R.drawable.ic_seveneleven;
                    } else if (model.name.toLowerCase().contains("vin")) {
                        logoRes = R.drawable.ic_vinmart;
                    }

                    Restaurant r = new Restaurant(model.name, logoRes, R.drawable.cover_menu_1, model.address, model.open_hours);
                    
                    ApiConfig.getFoodsApi(model.id, new ApiConfig.ApiCallback<List<ApiConfig.FoodModel>>() {
                        @Override
                        public void onSuccess(List<ApiConfig.FoodModel> foodModels) {
                            ArrayList<Food> menu = new ArrayList<>();
                            for (ApiConfig.FoodModel fm : foodModels) {
                                int img = R.drawable.dish;
                                if (fm.name.toLowerCase().contains("cơm")) img = R.drawable.bowl;
                                else if (fm.name.toLowerCase().contains("trà")) img = R.drawable.boba;
                                else if (fm.name.toLowerCase().contains("bánh")) img = R.drawable.sandwich;
                                else if (fm.name.toLowerCase().contains("coffee")) img = R.drawable.milk_coffee;
                                
                                menu.add(new Food(fm.name, img, (int) fm.price));
                            }
                            r.setMenu(menu);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String errorMessage) {
                        }
                    });

                    restaurantList.add(r);
                }
                adapter.notifyDataSetChanged();
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
