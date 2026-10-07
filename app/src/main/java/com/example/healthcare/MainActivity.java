package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.healthcare.admin.admin_section.AdminDashboardActivity;
import com.example.healthcare.appointment.AppointmentsFragment;
import com.example.healthcare.home.HomeFragment;
import com.example.healthcare.notification.NotificationsFragment;
import com.example.healthcare.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    public static final int TAB_NONE = -1;
    public static final int TAB_HOME = 0;
    public static final int TAB_APPTS = 1;
    public static final int TAB_NOTIFY = 2;
    public static final int TAB_PROFILE = 3;

    private LinearLayout navHome, navAppts, navNotify, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("healthcare_settings", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);
        boolean isUserRole = prefs.getBoolean("is_user_role", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        if (!isUserRole) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(android.R.id.content);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                int systemBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                v.setPadding(0, 0, 0, systemBottom);
                return insets;
            });
        }

        navHome = findViewById(R.id.navHome);
        navAppts = findViewById(R.id.navAppts);
        navNotify = findViewById(R.id.navNotify);
        navProfile = findViewById(R.id.navProfile);

        if (navHome != null) navHome.setOnClickListener(v -> navigateTo(new HomeFragment()));
        if (navAppts != null) navAppts.setOnClickListener(v -> navigateTo(AppointmentsFragment.newInstance(0)));
        if (navNotify != null) navNotify.setOnClickListener(v -> navigateTo(new NotificationsFragment()));
        if (navProfile != null) navProfile.setOnClickListener(v -> navigateTo(new ProfileFragment()));

        if (savedInstanceState == null) {
            navigateTo(new HomeFragment());
        }
    }

    public void navigateTo(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void openAppointments(int tabIndex) {
        navigateTo(AppointmentsFragment.newInstance(tabIndex));
    }

    public void setSelectedTab(int tab) {
        // Active tab styling updates
    }

    public void showMoreMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.popup_more_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_switch_admin) {
                SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
                prefs.edit().putBoolean("is_user_role", false).apply();
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
        popup.show();
    }
}
