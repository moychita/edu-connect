//package com.example.educonnect.fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.educonnect.activity.LoginActivity; // Pastikan ini mengarah ke Activity tujuanmu
//import com.example.educonnect.database.DatabaseHelper;
//import com.example.educonnect.databinding.FragmentProfileBinding;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class ProfileFragment extends Fragment {
//
//    private FragmentProfileBinding binding;
//    private DatabaseHelper dbHelper;
//    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view,
//                              @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        dbHelper = DatabaseHelper.getInstance(requireContext());
//        loadProfileStats();
//
//        // Toggle dark mode dari profil
//        binding.switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
//            com.example.educonnect.utils.ThemeUtils.setDarkMode(
//                    requireContext(), isChecked);
//            requireActivity().recreate();
//        });
//
//        // Set status awal switch
//        binding.switchDarkMode.setChecked(
//                com.example.educonnect.utils.ThemeUtils.isDarkMode(
//                        requireContext()));
//
//        // Klik tombol Logout
//        binding.textLogout.setOnClickListener(v -> {
//            // Beri pesan ke user
//            Toast.makeText(requireContext(), "Berhasil keluar sesi", Toast.LENGTH_SHORT).show();
//
//            // Arahkan user kembali ke halaman Login/Splash
//            Intent intent = new Intent(requireActivity(), LoginActivity.class);
//
//            // Bersihkan tumpukan aktivitas agar tidak bisa kembali dengan tombol Back
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            requireActivity().finish();
//        });
//    }
//
//    private void loadProfileStats() {
//        executor.execute(() -> {
//            int bookmarkCount = dbHelper.getAllBookmarks().size();
//            if (getActivity() != null) {
//                getActivity().runOnUiThread(() -> {
//                    // Proteksi pengecekan null agar tidak crash jika fragment sudah hancur
//                    if (binding != null) {
//                        binding.tvBookmarkCount.setText(String.valueOf(bookmarkCount));
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Penting: Hapus referensi binding agar tidak memory leak
//        binding = null;
//        // Kita tidak memanggil executor.shutdown() di sini agar executor
//        // tetap tersedia jika fragment dibuat ulang.
//    }
//}

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
        // Menginisialisasi binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());

        loadProfileStats();

        // Setup Dark Mode Switch
        binding.switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            ThemeUtils.setDarkMode(requireContext(), isChecked);
            requireActivity().recreate();
        });

        // Set status switch berdasarkan tema saat ini
        binding.switchDarkMode.setChecked(ThemeUtils.isDarkMode(requireContext()));

        // Setup Logout Button
        binding.layoutLogout.setOnClickListener(v -> {
            // 1. Hapus Session
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show();

            // 2. Arahkan ke LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            // Membersihkan tumpukan aktivitas
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadProfileStats() {
        executor.execute(() -> {
            // Mengambil data bookmark (asumsi ada method di dbHelper)
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
        // Mencegah memory leak
        binding = null;
    }
}