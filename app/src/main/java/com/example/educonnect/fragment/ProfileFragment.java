package com.example.educonnect.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educonnect.activity.LoginActivity;
import com.example.educonnect.database.DatabaseHelper;
import com.example.educonnect.databinding.FragmentProfileBinding;
import com.example.educonnect.utils.ThemeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private DatabaseHelper dbHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        loadProfileStats();

        // Set status awal switch tanpa memicu listener (hindari loop)
        binding.switchDarkMode.setChecked(ThemeUtils.isDarkMode(requireContext()));

        // Toggle dark mode dengan proteksi looping
        binding.switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            boolean currentTheme = ThemeUtils.isDarkMode(requireContext());
            // Hanya jalankan recreate jika status berubah
            if (isChecked != currentTheme) {
                ThemeUtils.setDarkMode(requireContext(), isChecked);
                requireActivity().recreate();
            }
        });

        // Setup Logout Button
        binding.layoutLogout.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadProfileStats() {
        executor.execute(() -> {
            int bookmarkCount = dbHelper.getAllBookmarks().size();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding != null) {
                        binding.tvBookmarkCount.setText(String.valueOf(bookmarkCount));
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}