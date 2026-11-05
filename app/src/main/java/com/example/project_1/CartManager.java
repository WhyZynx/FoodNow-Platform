package com.example.project_1;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Food> cartItems;
    private boolean hasActiveOrder = false;
    private int restaurantId = 1;
    private String lastRestaurantName = "Restaurant";

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public boolean hasActiveOrder() {
        return hasActiveOrder;
    }

    public void setHasActiveOrder(boolean hasActiveOrder) {
        this.hasActiveOrder = hasActiveOrder;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getLastRestaurantName() {
        return lastRestaurantName;
    }

    public void setLastRestaurantName(String lastRestaurantName) {
        this.lastRestaurantName = lastRestaurantName;
    }

    public List<Food> getCartItems() {
        return cartItems;
    }

    public void addFood(Food food) {
        if (food == null) return;
        if (food.restaurantId > 0) {
            this.restaurantId = food.restaurantId;
        }
        String foodName = food.name != null ? food.name : "";
        for (int i = 0; i < cartItems.size(); i++) {
            Food item = cartItems.get(i);
            if (item != null && foodName.equals(item.name)) {
                item.quantity = food.quantity;
                if (item.quantity <= 0) {
                    cartItems.remove(i);
                }
                return;
            }
        }
        if (food.quantity > 0) {
            cartItems.add(food);
        }
    }

    public int getTotalPrice() {
        int total = 0;
        for (Food item : cartItems) {
            total += item.price * item.quantity;
        }
        return total;
    }

    public int getTotalItems() {
        int total = 0;
        for (Food item : cartItems) {
            total += item.quantity;
        }
        return total;
    }

    public void clear() {
        cartItems.clear();
    }
}
