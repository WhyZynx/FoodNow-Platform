package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;

    private RestaurantFragment frgRestaurant;
    private OrderFragment frgOrder;
    private ProfileFragment frgProfile;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        int targetTab = getIntent().getIntExtra("target_tab", -1);
        if (targetTab != -1) {
            TabLayout.Tab tab = tabLayout.getTabAt(targetTab);
            if (tab != null) {
                tab.select();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserSession userSession = new UserSession(this);
        if (!userSession.isLoggedIn()) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);

        createTabs();

        frgRestaurant = new RestaurantFragment();
        frgOrder = new OrderFragment();
        frgProfile = new ProfileFragment();

        displayFragment(frgRestaurant);

        tabLayout.addOnTabSelectedListener(this);

        handleIntent();
    }

    private void createTabs() {
        tabLayout.addTab(
                tabLayout.newTab()
                        .setText("Trang chủ")
                        .setIcon(R.drawable.icon_home)
        );

        tabLayout.addTab(
                tabLayout.newTab()
                        .setText("Đơn hàng")
                        .setIcon(R.drawable.icon_order)
        );

        tabLayout.addTab(
                tabLayout.newTab()
                        .setText("Tài khoản")
                        .setIcon(R.drawable.icon_profile)
        );
    }

    protected void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutContainer, fragment)
                .commit();
    }

    @Override
    public void onTabSelected(@NonNull TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            displayFragment(frgRestaurant);
        } else if (tab.getPosition() == 1) {
            displayFragment(frgOrder);
        } else if (tab.getPosition() == 2) {
            displayFragment(frgProfile);
        }
    }

    @Override
    public void onTabUnselected(@NonNull TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(@NonNull TabLayout.Tab tab) {

    }
}
