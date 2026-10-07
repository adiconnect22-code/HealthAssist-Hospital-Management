package com.example.healthcare.admin.more_section;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthcare.R;
import com.example.healthcare.admin.admin_section.AdminDashboardActivity;
import com.example.healthcare.admin.appts_section.AppointmentsActivity;
import com.example.healthcare.admin.doctors_section.DoctorsListDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Activity inside 'more_section' folder for Settings & Profile.
 */
public class SettingsProfileActivity extends AppCompatActivity {

    private EditText etHospitalName;
    private EditText etHospitalEmail;
    private EditText etHospitalPhone;
    private EditText etWorkingDays;

    private TextView tvDarkModeStatus;
    private SwitchMaterial switchDarkMode;

    private MaterialButton btnViewAdminDetails;
    private MaterialButton btnLogout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);

        initViews();
        setupSeamlessBottomNavigation();
        setupDarkModeToggle();
        setupInputWatchers();
        setupClickListeners();
        setupBottomNavigation();

        fetchSettingsFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSeamlessBottomNavigation();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        etHospitalName = findViewById(R.id.etHospitalName);
        etHospitalEmail = findViewById(R.id.etHospitalEmail);
        etHospitalPhone = findViewById(R.id.etHospitalPhone);
        etWorkingDays = findViewById(R.id.etWorkingDays);

        tvDarkModeStatus = findViewById(R.id.tvDarkModeStatus);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        btnViewAdminDetails = findViewById(R.id.btnViewAdminDetails);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupDarkModeToggle() {
        if (switchDarkMode == null || tvDarkModeStatus == null) return;

        android.content.SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("is_dark_mode", false);

        switchDarkMode.setOnCheckedChangeListener(null);
        switchDarkMode.setChecked(isDark);
        tvDarkModeStatus.setText(isDark ? "🌙 Dark Mode On" : "🌙 Dark Mode Off");

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                prefs.edit().putBoolean("is_dark_mode", isChecked).apply();
                tvDarkModeStatus.setText(isChecked ? "🌙 Dark Mode On" : "🌙 Dark Mode Off");

                int targetMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
                    AppCompatDelegate.setDefaultNightMode(targetMode);
                    overridePendingTransition(0, 0);
                }
                saveSettingsToDatabase();
            }
        });
    }

    private void setupInputWatchers() {
        TextWatcher autoSaveWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveSettingsToDatabase();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        if (etHospitalName != null) etHospitalName.addTextChangedListener(autoSaveWatcher);
        if (etHospitalEmail != null) etHospitalEmail.addTextChangedListener(autoSaveWatcher);
        if (etHospitalPhone != null) etHospitalPhone.addTextChangedListener(autoSaveWatcher);
        if (etWorkingDays != null) etWorkingDays.addTextChangedListener(autoSaveWatcher);
    }

    private void setupClickListeners() {
        if (btnViewAdminDetails != null) {
            btnViewAdminDetails.setOnClickListener(v ->
                    AdminDetailsDialog.newInstance().show(getSupportFragmentManager(), "AdminDetailsDialog")
            );
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, com.example.healthcare.admin.AdminLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(R.id.nav_more);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_appts) {
                Intent intent = new Intent(this, AppointmentsActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_doctors) {
                Intent intent = new Intent(this, com.example.healthcare.admin.doctors_section.DoctorsActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_more) {
                MoreMenuDialog.newInstance().show(getSupportFragmentManager(), "MoreDialog");
                return true;
            }
            return false;
        });
    }

    private void fetchSettingsFromDatabase() {
        // TODO: Database connection logic
    }

    private void saveSettingsToDatabase() {
        // TODO: Database connection logic
    }

    private void setupSeamlessBottomNavigation() {
        if (getWindow() != null) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.bg_card));
        }

        if (bottomNavigationView != null) {
            bottomNavigationView.setPadding(0, 0, 0, 0);
            ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
                int systemBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.bottomMargin = Math.max(0, systemBottom - 12);
                v.setLayoutParams(params);
                v.setPadding(0, 0, 0, 0);
                return insets;
            });
        }
    }
}
