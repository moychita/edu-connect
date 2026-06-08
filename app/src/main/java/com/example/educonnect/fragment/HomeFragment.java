package com.example.educonnect.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.educonnect.R;
import com.example.educonnect.activity.DetailPostActivity;
import com.example.educonnect.adapter.PostAdapter;
import com.example.educonnect.api.RetrofitClient;
import com.example.educonnect.database.DatabaseHelper;
import com.example.educonnect.databinding.FragmentHomeBinding;
import com.example.educonnect.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PostAdapter postAdapter;
    private DatabaseHelper dbHelper;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Post> postList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        setupRecyclerView();
        loadPosts();

        binding.swipeRefresh.setOnRefreshListener(this::loadPosts);
        binding.swipeRefresh.setColorSchemeResources(
                R.color.green_primary,
                R.color.orange_secondary
        );
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(
                postList,
                // Klik post → buka DetailPostActivity
                post -> {
                    Intent intent = new Intent(requireContext(),
                            DetailPostActivity.class);
                    intent.putExtra(DetailPostActivity.EXTRA_POST_ID,    post.getId());
                    intent.putExtra(DetailPostActivity.EXTRA_POST_TITLE, post.getTitle());
                    intent.putExtra(DetailPostActivity.EXTRA_POST_BODY,  post.getBody());
                    intent.putExtra(DetailPostActivity.EXTRA_USER_ID,    post.getUserId());
                    startActivity(intent);
                },
                // Klik bookmark → simpan/hapus
                post -> {
                    executor.execute(() -> {
                        if (post.isBookmarked()) {
                            dbHelper.removeBookmark(post.getId());
                            post.setBookmarked(false);
                        } else {
                            dbHelper.addBookmark(post);
                            post.setBookmarked(true);
                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                postAdapter.notifyDataSetChanged();
                                String msg = post.isBookmarked()
                                        ? "Disimpan! 🔖"
                                        : "Dihapus dari bookmark";
                                Toast.makeText(requireContext(),
                                        msg, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
        );

        binding.rvPosts.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvPosts.setAdapter(postAdapter);
    }

    private void loadPosts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);

        RetrofitClient.getInstance().getApiService().getPosts()
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Post>> call,
                                           @NonNull Response<List<Post>> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Post> posts = response.body();
                            // Cache + cek bookmark di background thread
                            executor.execute(() -> {
                                dbHelper.cachePosts(posts);
                                for (Post post : posts) {
                                    post.setBookmarked(
                                            dbHelper.isBookmarked(post.getId()));
                                }
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        postList.clear();
                                        postList.addAll(posts);
                                        postAdapter.notifyDataSetChanged();
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Post>> call,
                                          @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);
                        loadFromCache();
                    }
                });
    }

    private void loadFromCache() {
        executor.execute(() -> {
            List<Post> cached = dbHelper.getCachedPosts();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (!cached.isEmpty()) {
                        postList.clear();
                        postList.addAll(cached);
                        postAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(),
                                "Mode offline: menampilkan data tersimpan",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText(
                                "Tidak ada koneksi & tidak ada data tersimpan.");
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executor.shutdown();
    }
}