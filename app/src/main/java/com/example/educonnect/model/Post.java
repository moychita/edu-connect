package com.example.educonnect.model;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("userId")
    private String userId;

    @SerializedName("category")
    private String category;

    public String getCategory() {
        return category != null ? category : "Info";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private boolean isBookmarked = false;

    public Post() {}

    public Post(int id, String title, String body, int userId) {
        this.id = String.valueOf(id);
        this.title = title;
        this.body = body;
        this.userId = String.valueOf(userId);
    }

    public int getId() {
        try { return Integer.parseInt(id); }
        catch (Exception e) { return 0; }
    }

    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public int getUserId() {
        try { return Integer.parseInt(userId); }
        catch (Exception e) { return 0; }
    }

    public void setUserId(int userId) {
        this.userId = String.valueOf(userId);
    }

    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }
}