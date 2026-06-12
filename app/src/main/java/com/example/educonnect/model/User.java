//package com.example.educonnect.model;
//
//import com.google.gson.annotations.SerializedName;
//
//public class User {
//
//    @SerializedName("id")
//    private String id;
//
//    @SerializedName("name")
//    private String name;
//
//    @SerializedName("username")
//    private String username;
//
//    @SerializedName("email")
//    private String email;
//
//    @SerializedName("city")
//    private String city;
//
//    public int getId() {
//        try { return Integer.parseInt(id); }
//        catch (Exception e) { return 0; }
//    }
//
//    public String getName() { return name; }
//    public String getUsername() { return username; }
//    public String getEmail() { return email; }
//    public String getPhone() { return "-"; }
//
//    // Langsung ambil city, tidak perlu inner class lagi
//    public String getCity() {
//        return city != null ? city : "-";
//    }
//}

package com.example.educonnect.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private String id; // Tetap String untuk kompatibilitas dengan JSON

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("city")
    private String city;

    // Tambahan untuk database lokal (Register)
    private String password;

    // Constructor kosong (untuk Retrofit)
    public User() {}

    // Constructor untuk Registrasi
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public int getId() {
        try { return Integer.parseInt(id); }
        catch (Exception e) { return 0; }
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; } // Tambahan untuk login

    public String getCity() {
        return city != null ? city : "-";
    }

    // Setters (diperlukan untuk form input)
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}