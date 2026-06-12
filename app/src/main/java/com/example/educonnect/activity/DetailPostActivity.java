package com.example.educonnect.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.educonnect.R;
import com.example.educonnect.adapter.CommentAdapter;
import com.example.educonnect.api.RetrofitClient;
import com.example.educonnect.database.DatabaseHelper;
import com.example.educonnect.databinding.ActivityDetailPostBinding;
import com.example.educonnect.model.Comment;
import com.example.educonnect.model.Post;
import com.example.educonnect.model.User;

import java.util.ArrayList;
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
    public static final String EXTRA_CATEGORY   = "category";

    private ActivityDetailPostBinding binding;
    private DatabaseHelper dbHelper;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();

    private int postId;
    private int userId;
    private String postTitle, postBody, postCategory;
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
        postId       = getIntent().getIntExtra(EXTRA_POST_ID, 0);
        postTitle    = getIntent().getStringExtra(EXTRA_POST_TITLE);
        postBody     = getIntent().getStringExtra(EXTRA_POST_BODY);
        userId       = getIntent().getIntExtra(EXTRA_USER_ID, 0);
        postCategory = getIntent().getStringExtra(EXTRA_CATEGORY);
        if (postCategory == null) postCategory = "Info";

        // Tampilkan konten
        binding.tvPostTitle.setText(postTitle);
        binding.tvPostBody.setText(postBody);
        binding.tvAvatarAuthor.setText(String.valueOf(userId));

        // Set kategori chip
        binding.tvPostCategory.setText(postCategory);
        setCategoryColor(postCategory);

        // Setup RecyclerView komentar
        commentAdapter = new CommentAdapter(commentList);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));
        binding.rvComments.setAdapter(commentAdapter);

        // Load data dari API
        loadUserInfo();
        loadComments();

        // Cek status bookmark
        executor.execute(() -> {
            isBookmarked = dbHelper.isBookmarked(postId);
            runOnUiThread(this::updateBookmarkButton);
        });

        // Klik tombol bookmark
        binding.btnBookmark.setOnClickListener(v -> toggleBookmark());

        // Klik tombol reminder
        binding.btnReminderDetail.setOnClickListener(v ->
                Toast.makeText(this,
                        "Gunakan tombol 🔔 di halaman Beranda untuk set reminder",
                        Toast.LENGTH_SHORT).show());

        // ====================================================================
        // GABUNGAN: Logika Tombol Kirim Komentar
        // ====================================================================
        binding.btnSendComment.setOnClickListener(v -> {
            String name = binding.etCommentName.getText().toString().trim();
            String body = binding.etCommentBody.getText().toString().trim();

            if (name.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Nama dan komentar tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Jalankan fungsi pengiriman ke API
            sendCommentToApi(name, body);
        });
    }

    private void setCategoryColor(String category) {
        int color;
        switch (category) {
            case "Akademik":  color = getColor(R.color.cat_akademik); break;
            case "Magang":    color = getColor(R.color.cat_magang);   break;
            case "Seminar":   color = getColor(R.color.cat_seminar);  break;
            case "Diskusi":   color = getColor(R.color.cat_diskusi);  break;
            case "Beasiswa":  color = getColor(R.color.yellow_accent); break;
            case "Lomba":     color = getColor(R.color.cat_lomba);    break;
            default:          color = getColor(R.color.green_primary); break;
        }
        binding.tvPostCategory.getBackground().setTint(color);
        binding.viewCategoryStrip.setBackgroundColor(color);
    }

    private void loadUserInfo() {
        RetrofitClient.getInstance().getApiService()
                .getUserById(userId)
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
                            commentList.clear();
                            commentList.addAll(response.body());
                            commentAdapter.notifyDataSetChanged();
                            binding.tvCommentCount.setText(
                                    commentList.size() + " Komentar");
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

    // ====================================================================
    // GABUNGAN: Fungsi Request POST Retrofit untuk Kirim Komentar ke API
    // ====================================================================
    private void sendCommentToApi(String name, String body) {
        binding.progressComments.setVisibility(View.VISIBLE);

        // Sesuaikan parameter di bawah ini dengan constructor kelas Model Comment milikmu
        Comment newComment = new Comment(postId, name, body);

        RetrofitClient.getInstance().getCommentApiService()
                .postComment(newComment) // Pastikan method postComment() sudah dibuat di interface ApiService kamu
                .enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(@NonNull Call<Comment> call, @NonNull Response<Comment> response) {
                        binding.progressComments.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(DetailPostActivity.this, "Komentar berhasil dikirim! 💬", Toast.LENGTH_SHORT).show();

                            // Reset input field jika sukses
                            binding.etCommentName.setText("");
                            binding.etCommentBody.setText("");

                            // Refresh daftar komentar agar langsung terupdate di layar
                            loadComments();
                        } else {
                            Toast.makeText(DetailPostActivity.this, "Gagal mengirim komentar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Comment> call, @NonNull Throwable t) {
                        binding.progressComments.setVisibility(View.GONE);
                        Toast.makeText(DetailPostActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
            binding.btnBookmark.setText("✅ Tersimpan");
            binding.btnBookmark.setStrokeColorResource(R.color.green_primary);
        } else {
            binding.btnBookmark.setText("🔖 Simpan");
            binding.btnBookmark.setStrokeColorResource(R.color.grey_text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}