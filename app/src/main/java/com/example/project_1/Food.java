package com.example.project_1;

import java.io.Serializable;

public class Food implements Serializable {
    String name;
    int image;
    int price;
    int quantity;

    public Food(String name, int image, int price) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = 0;
    }
}
