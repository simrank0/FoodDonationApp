package com.example.android.fooddonation;

public class User {
    private String firstName;
    private String lastName;
    private String location;
    private String contact;
    private String coordinates, address;
    private String userId;
    private String email;

    public User() {

    }

    public User(String firstName, String lastName, String location, String contact, String coordinates, String address, String userId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.contact = contact;
        this.coordinates = coordinates;
        this.address = address;
        this.userId= userId;
        this.email= email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getAddress() {
        return address;
    }

    public String getuserId() {
        return userId;
    }

    public String getmail(){
        return email;
    }
}