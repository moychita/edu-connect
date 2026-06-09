package com.example.educonnect.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.databinding.ItemPostBinding;
import com.example.educonnect.model.Post;

import java.util.List;
import java.util.Random;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClick(Post post);
    }

    public interface OnReminderClickListener {
        void onReminderClick(Post post);
    }

    private final List<Post> postList;
    private final OnPostClickListener clickListener;
    private final OnBookmarkClickListener bookmarkListener;
    private final OnReminderClickListener reminderListener;

    // Warna avatar yang bervariasi
    private static final int[] AVATAR_COLORS = {
            0xFF2D6A4F, 0xFF4361EE, 0xFFF72585,
            0xFF7209B7, 0xFF4CC9F0, 0xFFF4845F,
            0xFF06D6A0, 0xFFFFB703
    };

    public PostAdapter(List<Post> postList,
                       OnPostClickListener clickListener,
                       OnBookmarkClickListener bookmarkListener,
                       OnReminderClickListener reminderListener) {
        this.postList = postList;
        this.clickListener = clickListener;
        this.bookmarkListener = bookmarkListener;
        this.reminderListener = reminderListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(postList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;

        PostViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Post post, int position) {
            // Judul
            String title = post.getTitle();
            if (title != null && !title.isEmpty()) {
                title = Character.toUpperCase(title.charAt(0))
                        + title.substring(1);
            }
            binding.tvTitle.setText(title);
            binding.tvBody.setText(post.getBody());

            // Avatar — inisial dari nama user
            String[] userNames = {"Ahmad F.", "Siti R.", "Budi P.",
                    "Nur A.", "Rizky F."};
            int userIndex = (post.getUserId() - 1) % userNames.length;
            binding.tvUserId.setText(userNames[userIndex]);

            // Inisial avatar
            String initial = userNames[userIndex].substring(0, 1);
            binding.tvAvatar.setText(initial);

            // Warna avatar bervariasi
            int avatarColor = AVATAR_COLORS[position % AVATAR_COLORS.length];
            binding.tvAvatar.getBackground().setTint(avatarColor);

            // Waktu — simulasi
            String[] times = {"2 jam lalu", "4 jam lalu", "1 hari lalu",
                    "3 hari lalu", "Baru saja", "5 jam lalu"};
            binding.tvTime.setText(times[position % times.length]);

            // Kategori
            String category = post.getCategory();
            binding.tvCategory.setText(category);
            int catColor = getCategoryColor(category);
            binding.tvCategory.getBackground().setTint(catColor);

            // Strip warna kiri
            binding.viewCategoryColor.setBackgroundColor(catColor);

            // Like & komentar counter — simulasi
            int[] likes = {42, 15, 87, 23, 56, 8, 134, 31, 67, 19};
            int[] comments = {5, 3, 12, 7, 2, 9, 18, 4, 11, 6};
            binding.tvLikeCount.setText(
                    String.valueOf(likes[position % likes.length]));
            binding.tvCommentCount.setText(
                    String.valueOf(comments[position % comments.length]));

            // Status bookmark
            updateBookmarkUI(post.isBookmarked());

            // Click listeners
            binding.getRoot().setOnClickListener(
                    v -> clickListener.onPostClick(post));
            binding.btnBookmarkLayout.setOnClickListener(
                    v -> bookmarkListener.onBookmarkClick(post));
            binding.btnReminderLayout.setOnClickListener(
                    v -> reminderListener.onReminderClick(post));
        }

        private void updateBookmarkUI(boolean isBookmarked) {
            if (isBookmarked) {
                binding.btnBookmark.setImageResource(
                        R.drawable.ic_bookmark_filled);
                binding.tvBookmarkLabel.setText("Tersimpan");
                binding.tvBookmarkLabel.setTextColor(
                        binding.getRoot().getContext()
                                .getColor(R.color.green_primary));
            } else {
                binding.btnBookmark.setImageResource(
                        R.drawable.ic_bookmark_outline);
                binding.tvBookmarkLabel.setText("Simpan");
                binding.tvBookmarkLabel.setTextColor(
                        binding.getRoot().getContext()
                                .getColor(R.color.green_primary));
            }
        }

        private int getCategoryColor(String category) {
            if (category == null) return 0xFF2D6A4F;
            switch (category) {
                case "Akademik":  return 0xFF4361EE;
                case "Magang":    return 0xFF7209B7;
                case "Seminar":   return 0xFF4CC9F0;
                case "Diskusi":   return 0xFFF4845F;
                case "Beasiswa":  return 0xFFFFB703;
                case "Lomba":     return 0xFFF72585;
                default:          return 0xFF2D6A4F;
            }
        }
    }
}