package com.example.project_1;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Serializable {

    String name;
    int logo;
    int cover;
    String address;
    String openHours;
    String rating = "4.6 (200+)";
    String badge = "";
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
        this.rating = "4.6 (200+)";
        this.badge = "";
        this.menu = new ArrayList<>();
    }

    public Restaurant(
            String name,
            int logo,
            int cover,
            String address,
            String openHours,
            String rating,
            String badge) {

        this.name = name;
        this.logo = logo;
        this.cover = cover;
        this.address = address;
        this.openHours = openHours;
        this.rating = rating;
        this.badge = badge;
        this.menu = new ArrayList<>();
    }

    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    public String getOpenHours() {
        return "Open Hours: " + openHours;
    }
}