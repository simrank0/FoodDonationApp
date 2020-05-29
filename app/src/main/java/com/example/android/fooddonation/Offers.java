package com.example.android.fooddonation;

public class Offers {

    private String food;
    private String amount;
    private String expDate;
    private String pick;
    private String charges;
    private String userId;

    public Offers(){

    }

    public Offers(String food, String amount, String expDate, String pick, String charges, String userId){
        this.food = food;
        this.amount = amount;
        this.expDate = expDate;
        this.pick = pick;
        this.charges = charges;
        this.userId = userId;

    }

    public String getFood() {
        return food;
    }

    public String getAmount() {
        return amount;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getCharges() {
        return charges;
    }

    public String getUserId() {
        return userId;
    }

    public String getPick() {
        return pick;
    }
}
