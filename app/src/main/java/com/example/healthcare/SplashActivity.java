package com.example.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.admin.AdminLoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.btnPatient).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
            prefs.edit().putBoolean("is_user_role", true).apply();
            startActivity(new Intent(this, LoginActivity.class));
        });

        findViewById(R.id.btnAdmin).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
            prefs.edit().putBoolean("is_user_role", false).apply();
            startActivity(new Intent(this, AdminLoginActivity.class));
        });
    }
}
