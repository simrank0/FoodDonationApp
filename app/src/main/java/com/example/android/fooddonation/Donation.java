package com.example.android.fooddonation;

public class Donation {
    private boolean donation;
    private String cooked;
    private String food;
    private String amount;
    private String userId;
    private boolean pick;
    private String measure;
    private String charges;


    public Donation() {

    }


    public Donation(boolean donation, String cooked, String food, String amount, String userId, boolean pick, String measure, String charges) {
        this.donation = donation;
        this.cooked = cooked;
        this.food = food;
        this.amount = amount;
        this.userId = userId;
        this.pick = pick;
        this.measure = measure;
        this.charges= charges;

    }


    public boolean isDonation() {
        return donation;
    }

    public String getCooked() {
        return cooked;
    }

    public String getFood() {
        return food;
    }

    public String getAmount() {
        return amount;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isPick() {
        return pick;
    }

    public String getMeasure() {
        return measure;
    }

    public String getCharges() {
        return charges;
    }
}