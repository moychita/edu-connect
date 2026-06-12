package com.example.educonnect.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.educonnect.R;
import com.example.educonnect.databinding.ActivityMainBinding;
import com.example.educonnect.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("EduConnect");
        }

        try {
            NavHostFragment navHostFragment = (NavHostFragment)
                    getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                NavigationUI.setupWithNavController(
                        binding.bottomNavigation, navController);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}