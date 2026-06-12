package com.example.educonnect.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.databinding.ItemUserBinding;
import com.example.educonnect.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.tvName.setText(user.getName());
            binding.tvUsername.setText("@" + user.getUsername());
            binding.tvEmail.setText(user.getEmail());
            binding.tvCity.setText("📍 " + user.getCity());

            // Inisial nama
            String initial = (user.getName() != null && !user.getName().isEmpty())
                    ? String.valueOf(user.getName().charAt(0)).toUpperCase() : "?";
            binding.tvAvatar.setText(initial);

            // Warna avatar bervariasi berdasarkan posisi
            int[] colors = {
                    0xFF2D6A4F, 0xFF4361EE, 0xFFF72585,
                    0xFF7209B7, 0xFF4CC9F0, 0xFFF4845F
            };
            int colorIndex = Math.abs(user.getName().hashCode()) % colors.length;
            binding.tvAvatar.getBackground().setTint(colors[colorIndex]);
        }
    }
}