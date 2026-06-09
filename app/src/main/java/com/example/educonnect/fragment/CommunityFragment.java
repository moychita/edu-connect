package com.example.educonnect.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.educonnect.R;
import com.example.educonnect.adapter.UserAdapter;
import com.example.educonnect.api.RetrofitClient;
import com.example.educonnect.databinding.FragmentCommunityBinding;
import com.example.educonnect.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadUsers();

        binding.swipeRefresh.setOnRefreshListener(this::loadUsers);
        binding.swipeRefresh.setColorSchemeResources(
                R.color.green_primary, R.color.orange_secondary);
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(userList);
        binding.rvUsers.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(userAdapter);
    }

    private void loadUsers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);

        RetrofitClient.getInstance().getApiService().getUsers()
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<User>> call,
                                           @NonNull Response<List<User>> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            userList.clear();
                            userList.addAll(response.body());
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<User>> call,
                                          @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText(
                                "Gagal memuat anggota.\nPeriksa koneksi internet.");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}