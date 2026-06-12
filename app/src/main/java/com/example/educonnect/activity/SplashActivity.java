//package com.example.educonnect.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.educonnect.R;
//
//import com.example.educonnect.utils.ThemeUtils;
//
//public class SplashActivity extends AppCompatActivity {
//
//    private static final int SPLASH_DURATION = 2500;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // ini
//        ThemeUtils.setDarkMode(this, false);
//
//        setContentView(R.layout.activity_splash);
//
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }, SPLASH_DURATION);
//    }
//}

package com.example.educonnect.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.educonnect.R;
import com.example.educonnect.utils.ThemeUtils;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menentukan tema
        ThemeUtils.setDarkMode(this, ThemeUtils.isDarkMode(this));

        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Cek sesi login
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            Intent intent;
            if (isLoggedIn) {
                // Jika sudah login, langsung ke MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Jika belum, ke LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish(); // Menutup Splash agar tidak bisa ditekan tombol back
        }, SPLASH_DURATION);
    }
}
