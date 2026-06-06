package com.example.educonnect.api;

import com.example.educonnect.model.Comment;
import com.example.educonnect.model.Post;
import com.example.educonnect.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("posts/{id}/comments")
    Call<List<Comment>> getCommentsByPostId(@Path("id") int postId);

    @GET("users")
    Call<List<User>> getUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int id);
}