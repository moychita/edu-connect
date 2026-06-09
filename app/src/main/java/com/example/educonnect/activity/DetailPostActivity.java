package com.example.educonnect.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.educonnect.adapter.CommentAdapter;
import com.example.educonnect.api.RetrofitClient;
import com.example.educonnect.database.DatabaseHelper;
import com.example.educonnect.databinding.ActivityDetailPostBinding;
import com.example.educonnect.model.Comment;
import com.example.educonnect.model.Post;
import com.example.educonnect.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPostActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID    = "post_id";
    public static final String EXTRA_POST_TITLE = "post_title";
    public static final String EXTRA_POST_BODY  = "post_body";
    public static final String EXTRA_USER_ID    = "user_id";

    private ActivityDetailPostBinding binding;
    private DatabaseHelper dbHelper;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private int postId;
    private int userId;
    private String postTitle, postBody;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Post");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Init database
        dbHelper = DatabaseHelper.getInstance(this);

        // Ambil data dari Intent
        postId    = getIntent().getIntExtra(EXTRA_POST_ID, 0);
        postTitle = getIntent().getStringExtra(EXTRA_POST_TITLE);
        postBody  = getIntent().getStringExtra(EXTRA_POST_BODY);
        userId    = getIntent().getIntExtra(EXTRA_USER_ID, 0);

        // Tampilkan konten
        binding.tvPostTitle.setText(postTitle);
        binding.tvPostBody.setText(postBody);
        binding.tvAvatarAuthor.setText(String.valueOf(userId));

        // Load data dari API
        loadUserInfo();
        loadComments();

        // Cek status bookmark saat halaman dibuka
        executor.execute(() -> {
            isBookmarked = dbHelper.isBookmarked(postId);
            runOnUiThread(this::updateBookmarkButton);
        });

        // Klik tombol bookmark
        binding.btnBookmark.setOnClickListener(v -> toggleBookmark());
    }

    private void loadUserInfo() {
        RetrofitClient.getInstance().getApiService().getUserById(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call,
                                           @NonNull Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body();
                            binding.tvAuthorName.setText(user.getName());
                            binding.tvAuthorEmail.setText(user.getEmail());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call,
                                          @NonNull Throwable t) {
                        binding.tvAuthorName.setText("User #" + userId);
                        binding.tvAuthorEmail.setText("-");
                    }
                });
    }

    private void loadComments() {
        binding.progressComments.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getCommentApiService()
                .getCommentsByPostId(postId)
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Comment>> call,
                                           @NonNull Response<List<Comment>> response) {
                        binding.progressComments.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Comment> comments = response.body();
                            binding.tvCommentCount.setText(
                                    comments.size() + " Komentar");
                            CommentAdapter adapter = new CommentAdapter(comments);
                            binding.rvComments.setLayoutManager(
                                    new LinearLayoutManager(DetailPostActivity.this));
                            binding.rvComments.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Comment>> call,
                                          @NonNull Throwable t) {
                        binding.progressComments.setVisibility(View.GONE);
                        binding.tvCommentCount.setText("Gagal memuat komentar");
                    }
                });
    }

    private void toggleBookmark() {
        executor.execute(() -> {
            if (isBookmarked) {
                dbHelper.removeBookmark(postId);
                isBookmarked = false;
                runOnUiThread(() -> {
                    updateBookmarkButton();
                    Toast.makeText(this,
                            "Dihapus dari bookmark",
                            Toast.LENGTH_SHORT).show();
                });
            } else {
                Post post = new Post(postId, postTitle, postBody, userId);
                dbHelper.addBookmark(post);
                isBookmarked = true;
                runOnUiThread(() -> {
                    updateBookmarkButton();
                    Toast.makeText(this,
                            "Disimpan ke bookmark! 🔖",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateBookmarkButton() {
        if (isBookmarked) {
            binding.btnBookmark.setText("🔖  Tersimpan");
            binding.btnBookmark.setStrokeColorResource(
                    com.example.educonnect.R.color.green_primary);
        } else {
            binding.btnBookmark.setText("🔖  Simpan ke Bookmark");
            binding.btnBookmark.setStrokeColorResource(
                    com.example.educonnect.R.color.grey_text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}