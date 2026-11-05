package com.example.project_1;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Serializable {

    int id;
    String name;
    int logo;
    int cover;
    String address;
    String openHours;
    String rating = "4.6 (200+)";
    String badge = "";
    ArrayList<Food> menu;

    public Restaurant(
            int id,
            String name,
            int logo,
            int cover,
            String address,
            String openHours) {

        this.id = id;
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
            String openHours) {

        this.id = 0;
        this.name = name;
        this.logo = logo;
        this.cover = cover;
        this.address = address;
        this.openHours = openHours;
        this.rating = "4.6 (200+)";
        this.badge = "";
        this.menu = new ArrayList<>();
    }

    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    public String getOpenHours() {
        return "Open Hours: " + openHours;
    }
}
