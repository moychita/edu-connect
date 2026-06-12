package com.example.educonnect.model;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("id")
    private int id;

    @SerializedName("postId")
    private int postId;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("body")
    private String body;

    // ====================================================================
    // TAMBAHAN BARU: Constructor untuk membuat objek Comment baru
    // ====================================================================
    public Comment(int postId, String name, String body) {
        this.postId = postId;
        this.name = name;
        this.body = body;
        this.email = ""; // Diisi string kosong dulu karena form kita tidak menginput email
    }

    // Getter bawaan kodemu sebelumnya
    public int getId() { return id; }
    public int getPostId() { return postId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBody() { return body; }
}