package com.example.educonnect.api;

import com.example.educonnect.model.Comment;
import com.example.educonnect.model.Post;
import com.example.educonnect.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // MockAPI — posts
    @GET("posts")
    Call<List<Post>> getPosts();

    // MockAPI — users
    @GET("users")
    Call<List<User>> getUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int id);

    // JSONPlaceholder — ambil komentar berdasarkan ID Post
    @GET("posts/{id}/comments")
    Call<List<Comment>> getCommentsByPostId(@Path("id") int postId);

    // ====================================================================
    // TAMBAHAN BARU: Logika POST untuk Mengirim Komentar Baru ke Server
    // ====================================================================
    @POST("comments")
    Call<Comment> postComment(@Body Comment comment);
}