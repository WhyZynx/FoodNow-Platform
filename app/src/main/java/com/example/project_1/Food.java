package com.example.project_1;

import java.io.Serializable;

public class Food implements Serializable {
    int id;
    int restaurantId;
    String name;
    int image;
    int price;
    int quantity;

    public Food(int id, int restaurantId, String name, int image, int price) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = 0;
    }

    public Food(String name, int image, int price) {
        this.id = 0;
        this.restaurantId = 1;
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = 0;
    }
}
