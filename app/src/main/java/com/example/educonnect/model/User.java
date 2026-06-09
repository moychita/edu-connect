package com.example.educonnect.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("city")
    private String city;

    public int getId() {
        try { return Integer.parseInt(id); }
        catch (Exception e) { return 0; }
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return "-"; }

    // Langsung ambil city, tidak perlu inner class lagi
    public String getCity() {
        return city != null ? city : "-";
    }
}