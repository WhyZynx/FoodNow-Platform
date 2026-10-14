package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantItemClickListener {

    private RecyclerView rvCategoryRestaurants;
    private RestaurantAdapter adapter;
    private ArrayList<Restaurant> categoryRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String categoryName = getIntent().getStringExtra("category_name");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh mục: " + (categoryName != null ? categoryName : "Món ăn"));
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvCategoryRestaurants = findViewById(R.id.rvCategoryRestaurants);
        categoryRestaurants = new ArrayList<>();

        ArrayList<Restaurant> allRestaurants = (ArrayList<Restaurant>) getIntent().getSerializableExtra("restaurants_list");
        if (allRestaurants != null && categoryName != null) {
            String keyword = categoryName.toLowerCase();
            for (Restaurant r : allRestaurants) {
                boolean match = r.name.toLowerCase().contains(keyword);
                if (!match && r.menu != null) {
                    for (Food f : r.menu) {
                        if (f.name.toLowerCase().contains(keyword) || (keyword.equals("burger") && f.name.toLowerCase().contains("burger"))) {
                            match = true;
                            break;
                        }
                    }
                }
                if (match) {
                    categoryRestaurants.add(r);
                }
            }
            if (categoryRestaurants.isEmpty()) {
                categoryRestaurants.addAll(allRestaurants);
            }
        }

        adapter = new RestaurantAdapter(categoryRestaurants, this);
        rvCategoryRestaurants.setLayoutManager(new LinearLayoutManager(this));
        rvCategoryRestaurants.setAdapter(adapter);
    }

    @Override
    public void onRestaurantItemClick(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }
}
