package com.example.android.fooddonation;

public class Restaurant {
    private String name;
    private String userId;

    public Restaurant(){

    }

    public Restaurant(String name, String userId){
        this.name = name;
        this.userId = userId;
    }

    public String getFood() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}

