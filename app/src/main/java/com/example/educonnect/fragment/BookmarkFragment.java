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

import com.example.educonnect.activity.DetailPostActivity;
import com.example.educonnect.adapter.PostAdapter;
import com.example.educonnect.database.DatabaseHelper;
import com.example.educonnect.databinding.FragmentBookmarkBinding;
import com.example.educonnect.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarkFragment extends Fragment {

    private FragmentBookmarkBinding binding;
    private PostAdapter postAdapter;
    private DatabaseHelper dbHelper;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Post> bookmarkList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload setiap kali halaman ini dibuka
        loadBookmarks();
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(
                bookmarkList,
                post -> {
                    Intent intent = new Intent(requireContext(),
                            DetailPostActivity.class);
                    intent.putExtra(DetailPostActivity.EXTRA_POST_ID,    post.getId());
                    intent.putExtra(DetailPostActivity.EXTRA_POST_TITLE, post.getTitle());
                    intent.putExtra(DetailPostActivity.EXTRA_POST_BODY,  post.getBody());
                    intent.putExtra(DetailPostActivity.EXTRA_USER_ID,    post.getUserId());
                    startActivity(intent);
                },
                post -> {
                    // Di halaman bookmark, klik bookmark = hapus
                    executor.execute(() -> {
                        dbHelper.removeBookmark(post.getId());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                bookmarkList.remove(post);
                                postAdapter.notifyDataSetChanged();
                                updateEmptyState();
                                Toast.makeText(requireContext(),
                                        "Dihapus dari bookmark",
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
        );

        binding.rvBookmarks.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvBookmarks.setAdapter(postAdapter);
    }

    private void loadBookmarks() {
        executor.execute(() -> {
            List<Post> bookmarks = dbHelper.getAllBookmarks();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    bookmarkList.clear();
                    bookmarkList.addAll(bookmarks);
                    postAdapter.notifyDataSetChanged();
                    updateEmptyState();
                });
            }
        });
    }

    private void updateEmptyState() {
        if (bookmarkList.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvBookmarks.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvBookmarks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executor.shutdown();
    }
}