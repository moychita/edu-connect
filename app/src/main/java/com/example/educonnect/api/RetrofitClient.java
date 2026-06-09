package com.example.educonnect.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // MockAPI untuk posts dan users
    private static final String MOCKAPI_BASE_URL =
            "https://6a278b41a84f9d39e908b1db.mockapi.io/educonnect/api/v1/";

    // JSONPlaceholder untuk komentar
    private static final String JSONPLACEHOLDER_URL =
            "https://jsonplaceholder.typicode.com/";

    private static RetrofitClient instance;
    private final ApiService mockApiService;
    private final ApiService commentApiService;

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // Retrofit untuk MockAPI (posts + users)
        mockApiService = new Retrofit.Builder()
                .baseUrl(MOCKAPI_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);

        // Retrofit untuk JSONPlaceholder (komentar)
        commentApiService = new Retrofit.Builder()
                .baseUrl(JSONPLACEHOLDER_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    // Untuk posts dan users
    public ApiService getApiService() {
        return mockApiService;
    }

    // Untuk komentar
    public ApiService getCommentApiService() {
        return commentApiService;
    }
}