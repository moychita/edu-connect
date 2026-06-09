package com.example.educonnect.fragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.educonnect.R;
import com.example.educonnect.activity.DetailPostActivity;
import com.example.educonnect.adapter.PostAdapter;
import com.example.educonnect.api.RetrofitClient;
import com.example.educonnect.database.DatabaseHelper;
import com.example.educonnect.databinding.FragmentHomeBinding;
import com.example.educonnect.model.Post;
import com.example.educonnect.utils.ReminderReceiver;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
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

    private List<Post> allPosts = new ArrayList<>();    // semua data dari API
    private List<Post> filteredPosts = new ArrayList<>(); // data setelah filter

    private String selectedCategory = "Semua";
    private String searchQuery = "";

    private static final String[] CATEGORIES = {
            "Semua", "Akademik", "Magang", "Seminar",
            "Diskusi", "Info", "Beasiswa", "Lomba"
    };

    private static final int[] CATEGORY_COLORS = {
            R.color.green_primary,
            R.color.cat_akademik,
            R.color.cat_magang,
            R.color.cat_seminar,
            R.color.cat_diskusi,
            R.color.cat_info,
            R.color.yellow_accent,
            R.color.cat_lomba
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        setupRecyclerView();
        setupCategoryFilter();
        setupSearch();
        loadPosts();

        binding.swipeRefresh.setOnRefreshListener(this::loadPosts);
        binding.swipeRefresh.setColorSchemeResources(
                R.color.green_primary,
                R.color.orange_secondary,
                R.color.yellow_accent);
    }

    private void setupCategoryFilter() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            final String cat = CATEGORIES[i];
            final int colorRes = CATEGORY_COLORS[i];

            Chip chip = new Chip(requireContext());
            chip.setText(cat);
            chip.setCheckable(true);
            chip.setChecked(cat.equals("Semua"));
            chip.setChipBackgroundColorResource(
                    cat.equals("Semua") ? R.color.green_primary : R.color.grey_bg);
            chip.setTextColor(cat.equals("Semua")
                    ? Color.WHITE
                    : ContextCompat.getColor(requireContext(), R.color.grey_text));

            chip.setOnClickListener(v -> {
                selectedCategory = cat;
                // Update tampilan semua chip
                updateChipStyles(cat);
                applyFilter();
            });

            binding.layoutFilter.addView(chip);
        }
    }

    private void updateChipStyles(String selected) {
        for (int i = 0; i < binding.layoutFilter.getChildCount(); i++) {
            Chip chip = (Chip) binding.layoutFilter.getChildAt(i);
            boolean isSelected = chip.getText().toString().equals(selected);
            chip.setChipBackgroundColorResource(
                    isSelected ? CATEGORY_COLORS[i] : R.color.grey_bg);
            chip.setTextColor(isSelected
                    ? Color.WHITE
                    : ContextCompat.getColor(requireContext(), R.color.grey_text));
        }
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilter() {
        filteredPosts.clear();

        for (Post post : allPosts) {
            boolean matchCategory = selectedCategory.equals("Semua")
                    || post.getCategory().equals(selectedCategory);
            boolean matchSearch = searchQuery.isEmpty()
                    || post.getTitle().toLowerCase().contains(searchQuery)
                    || post.getBody().toLowerCase().contains(searchQuery);

            if (matchCategory && matchSearch) {
                filteredPosts.add(post);
            }
        }

        postAdapter.notifyDataSetChanged();

        // Tampilkan pesan jika tidak ada hasil
        if (filteredPosts.isEmpty()) {
            binding.tvError.setVisibility(View.VISIBLE);
            binding.tvError.setText("Tidak ada postingan ditemukan 🔍");
        } else {
            binding.tvError.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(
                filteredPosts,
                post -> {
                    Intent intent = new Intent(requireContext(),
                            DetailPostActivity.class);
                    intent.putExtra(DetailPostActivity.EXTRA_POST_ID,
                            post.getId());
                    intent.putExtra(DetailPostActivity.EXTRA_POST_TITLE,
                            post.getTitle());
                    intent.putExtra(DetailPostActivity.EXTRA_POST_BODY,
                            post.getBody());
                    intent.putExtra(DetailPostActivity.EXTRA_USER_ID,
                            post.getUserId());
                    startActivity(intent);
                },
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
                },
                // Klik tombol reminder
                post -> showReminderDialog(post)
        );

        binding.rvPosts.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvPosts.setAdapter(postAdapter);
    }

    private void showReminderDialog(Post post) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(
                            requireContext(),
                            (view2, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);

                                if (calendar.getTimeInMillis()
                                        <= System.currentTimeMillis()) {
                                    Toast.makeText(requireContext(),
                                            "Pilih waktu yang akan datang!",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                setReminder(post, calendar);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.setTitle("Pilih tanggal reminder");
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }

    private void setReminder(Post post, Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager)
                requireContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        intent.putExtra("title", "📚 Reminder EduConnect");
        intent.putExtra("message", post.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                post.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent);

            String waktu = String.format("%02d/%02d %02d:%02d",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));

            Toast.makeText(requireContext(),
                    "Reminder diset: " + waktu + " 🔔",
                    Toast.LENGTH_LONG).show();
        }
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
                            executor.execute(() -> {
                                dbHelper.cachePosts(posts);
                                for (Post post : posts) {
                                    post.setBookmarked(
                                            dbHelper.isBookmarked(post.getId()));
                                }
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        allPosts.clear();
                                        allPosts.addAll(posts);
                                        applyFilter();
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
                        allPosts.clear();
                        allPosts.addAll(cached);
                        applyFilter();
                        Toast.makeText(requireContext(),
                                "Mode offline: data tersimpan",
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