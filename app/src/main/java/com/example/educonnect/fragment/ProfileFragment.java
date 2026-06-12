package com.example.educonnect.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educonnect.databinding.FragmentProfileBinding;
import com.example.educonnect.database.DatabaseHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private DatabaseHelper dbHelper;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());
        loadProfileStats();

        // Toggle dark mode dari profil
        binding.switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            com.example.educonnect.utils.ThemeUtils.setDarkMode(
                    requireContext(), isChecked);
            requireActivity().recreate();
        });

        // Set status awal switch
        binding.switchDarkMode.setChecked(
                com.example.educonnect.utils.ThemeUtils.isDarkMode(
                        requireContext()));
    }

    private void loadProfileStats() {
        executor.execute(() -> {
            int bookmarkCount = dbHelper.getAllBookmarks().size();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // KUNCI PERBAIKAN: Berikan proteksi pengecekan null agar tidak crash
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
        // KUNCI PERBAIKAN: Jangan mematikan executor di fragment agar thread-nya tetap bisa dipakai ulang saat kembali ke halaman ini
    }
}