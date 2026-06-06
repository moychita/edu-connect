package com.example.educonnect.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.educonnect.adapter.PostAdapter;
import com.example.educonnect.api.RetrofitClient;
import com.example.educonnect.databinding.FragmentHomeBinding;
import com.example.educonnect.model.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PostAdapter postAdapter;
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

        setupRecyclerView();
        loadPosts();

        // Tarik ke bawah untuk refresh
        binding.swipeRefresh.setOnRefreshListener(this::loadPosts);
        binding.swipeRefresh.setColorSchemeResources(
                com.example.educonnect.R.color.green_primary,
                com.example.educonnect.R.color.orange_secondary
        );
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(
                postList,
                // Klik post → untuk sekarang tampilkan Toast dulu
                // nanti diganti buka DetailActivity
                post -> Toast.makeText(requireContext(),
                        "Klik: " + post.getTitle(), Toast.LENGTH_SHORT).show(),
                // Klik bookmark → untuk sekarang Toast dulu
                // nanti dihubungkan ke SQLite
                post -> Toast.makeText(requireContext(),
                        "Bookmark: " + post.getTitle(), Toast.LENGTH_SHORT).show()
        );

        binding.rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPosts.setAdapter(postAdapter);
    }

    private void loadPosts() {
        // Tampilkan loading
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
                            postList.clear();
                            postList.addAll(response.body());
                            postAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Post>> call,
                                          @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText("Gagal memuat data.\nPeriksa koneksi internet.");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}