package com.example.project_1;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Serializable {

    String name;
    int logo;
    int cover;
    String address;
    String openHours;
    ArrayList<Food> menu;

    public Restaurant(
            String name,
            int logo,
            int cover,
            String address,
            String openHours) {

        this.name = name;
        this.logo = logo;
        this.cover = cover;
        this.address = address;
        this.openHours = openHours;
        this.menu = new ArrayList<>();
    }

    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    public String getOpenHours() {
        return "Open Hours: " + openHours;
    }
}