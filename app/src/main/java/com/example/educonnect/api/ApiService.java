package com.example.educonnect.api;

import com.example.educonnect.model.Comment;
import com.example.educonnect.model.Post;
import com.example.educonnect.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // MockAPI — posts
    // Ganti "post" jika nama resource kamu berbeda
    @GET("posts")
    Call<List<Post>> getPosts();

    // MockAPI — users
    // Ganti "user" jika nama resource kamu berbeda
    @GET("users")
    Call<List<User>> getUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int id);

    // JSONPlaceholder — komentar
    @GET("posts/{id}/comments")
    Call<List<Comment>> getCommentsByPostId(@Path("id") int postId);
}