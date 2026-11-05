package com.example.project_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    private RestaurantFragment frgRestaurant;
    private OrderHistoryFragment frgOrderHistory;
    private OrderFragment frgCart;
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
            selectTab(targetTab);
        }
    }

    public void selectTab(int tabIndex) {
        if (bottomNavigation != null) {
            if (tabIndex == 0) bottomNavigation.setSelectedItemId(R.id.nav_home);
            else if (tabIndex == 1) bottomNavigation.setSelectedItemId(R.id.nav_orders);
            else if (tabIndex == 2) bottomNavigation.setSelectedItemId(R.id.nav_cart);
            else if (tabIndex == 3) bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }
    }

    public void updateCartBadge() {
        if (bottomNavigation == null) return;
        int totalItems = CartManager.getInstance().getTotalItems();
        if (totalItems > 0) {
            BadgeDrawable badge = bottomNavigation.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(true);
            badge.setNumber(totalItems);
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed));
        } else {
            bottomNavigation.removeBadge(R.id.nav_cart);
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

        bottomNavigation = findViewById(R.id.bottomNavigation);

        frgRestaurant = new RestaurantFragment();
        frgOrderHistory = new OrderHistoryFragment();
        frgCart = new OrderFragment();
        frgProfile = new ProfileFragment();

        displayFragment(frgRestaurant);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                displayFragment(frgRestaurant);
                return true;
            } else if (itemId == R.id.nav_orders) {
                displayFragment(frgOrderHistory);
                return true;
            } else if (itemId == R.id.nav_cart) {
                displayFragment(frgCart);
                return true;
            } else if (itemId == R.id.nav_profile) {
                displayFragment(frgProfile);
                return true;
            }
            return false;
        });

        handleIntent();
        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    protected void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutContainer, fragment)
                .commit();
    }
}
