package com.example.project_1;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Food> cartItems;
    private boolean hasActiveOrder = false;
    private int restaurantId = 1;
    private int lastOrderId = 0;
    private String lastRestaurantName = "Restaurant";
    private String lastOrderAddress = "123 Main Street";
    private String lastPaymentMethod = "Cash";
    private int lastOrderTotal = 0;
    private String lastOrderStatus = "pending";
    private String lastDriverName = "";
    private String lastDriverPhone = "";

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

    public int getLastOrderId() {
        return lastOrderId;
    }

    public void setLastOrderId(int lastOrderId) {
        this.lastOrderId = lastOrderId;
    }

    public String getLastRestaurantName() {
        return lastRestaurantName;
    }

    public void setLastRestaurantName(String lastRestaurantName) {
        this.lastRestaurantName = lastRestaurantName;
    }

    public String getLastOrderAddress() {
        return lastOrderAddress;
    }

    public void setLastOrderAddress(String lastOrderAddress) {
        this.lastOrderAddress = lastOrderAddress;
    }

    public String getLastPaymentMethod() {
        return lastPaymentMethod;
    }

    public void setLastPaymentMethod(String lastPaymentMethod) {
        this.lastPaymentMethod = lastPaymentMethod;
    }

    public int getLastOrderTotal() {
        return lastOrderTotal;
    }

    public void setLastOrderTotal(int lastOrderTotal) {
        this.lastOrderTotal = lastOrderTotal;
    }

    public String getLastOrderStatus() {
        return lastOrderStatus;
    }

    public void setLastOrderStatus(String lastOrderStatus) {
        this.lastOrderStatus = lastOrderStatus;
    }

    public String getLastDriverName() {
        return lastDriverName;
    }

    public void setLastDriverName(String lastDriverName) {
        this.lastDriverName = lastDriverName;
    }

    public String getLastDriverPhone() {
        return lastDriverPhone;
    }

    public void setLastDriverPhone(String lastDriverPhone) {
        this.lastDriverPhone = lastDriverPhone;
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
