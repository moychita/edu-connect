package com.example.educonnect.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.databinding.ItemPostBinding;
import com.example.educonnect.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // Interface untuk handle klik item
    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    // Interface untuk handle klik bookmark
    public interface OnBookmarkClickListener {
        void onBookmarkClick(Post post);
    }

    private final List<Post> postList;
    private final OnPostClickListener clickListener;
    private final OnBookmarkClickListener bookmarkListener;

    public PostAdapter(List<Post> postList,
                       OnPostClickListener clickListener,
                       OnBookmarkClickListener bookmarkListener) {
        this.postList = postList;
        this.clickListener = clickListener;
        this.bookmarkListener = bookmarkListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(postList.get(position));
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

        void bind(Post post) {
            // Kapitalisasi huruf pertama judul
            String title = post.getTitle();
            if (title != null && !title.isEmpty()) {
                title = Character.toUpperCase(title.charAt(0)) + title.substring(1);
            }
            binding.tvTitle.setText(title);
            binding.tvBody.setText(post.getBody());

            // Tampilkan "User #1", "User #2", dst
            binding.tvUserId.setText("User #" + post.getUserId());

            // Avatar: huruf pertama dari userId
            binding.tvAvatar.setText(String.valueOf(post.getUserId()));

            // Icon bookmark berubah sesuai status
            binding.btnBookmark.setImageResource(
                    post.isBookmarked()
                            ? R.drawable.ic_bookmark_filled
                            : R.drawable.ic_bookmark_outline
            );

            // Klik seluruh card → buka detail
            binding.getRoot().setOnClickListener(v -> clickListener.onPostClick(post));

            // Klik tombol bookmark saja
            binding.btnBookmark.setOnClickListener(v -> bookmarkListener.onBookmarkClick(post));
        }
    }
}