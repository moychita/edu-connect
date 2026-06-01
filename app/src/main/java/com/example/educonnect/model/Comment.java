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

    public int getId() { return id; }
    public int getPostId() { return postId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBody() { return body; }
}