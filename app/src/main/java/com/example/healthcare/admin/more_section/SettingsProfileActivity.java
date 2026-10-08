package com.example.healthcare.admin.more_section;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity inside 'more_section' folder for Settings & Profile.
 * Connected directly to Firebase Cloud Firestore 'settings/hospital_info' collection.
 */
public class SettingsProfileActivity extends AppCompatActivity {

    private static final String TAG = "SettingsProfileActivity";

    private EditText etHospitalName;
    private EditText etHospitalEmail;
    private EditText etHospitalPhone;
    private EditText etWorkingDays;

    private TextView tvDarkModeStatus;
    private SwitchMaterial switchDarkMode;

    private MaterialButton btnViewAdminDetails;
    private MaterialButton btnLogout;
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);

        db = FirebaseFirestore.getInstance();

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
        db.collection("settings").document("hospital_info")
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        String name = doc.getString("hospitalName");
                        String email = doc.getString("hospitalEmail");
                        String phone = doc.getString("hospitalPhone");
                        String hours = doc.getString("workingDays");

                        if (name != null && etHospitalName != null) etHospitalName.setText(name);
                        if (email != null && etHospitalEmail != null) etHospitalEmail.setText(email);
                        if (phone != null && etHospitalPhone != null) etHospitalPhone.setText(phone);
                        if (hours != null && etWorkingDays != null) etWorkingDays.setText(hours);
                    } else {
                        saveSettingsToDatabase();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching hospital settings from Firestore", e));
    }

    private void saveSettingsToDatabase() {
        String name = (etHospitalName != null && etHospitalName.getText() != null)
                ? etHospitalName.getText().toString().trim() : "Health Assist Manipal";
        String email = (etHospitalEmail != null && etHospitalEmail.getText() != null)
                ? etHospitalEmail.getText().toString().trim() : "contact@manipal.healthassist.com";
        String phone = (etHospitalPhone != null && etHospitalPhone.getText() != null)
                ? etHospitalPhone.getText().toString().trim() : "+91 80 2528 8333";
        String hours = (etWorkingDays != null && etWorkingDays.getText() != null)
                ? etWorkingDays.getText().toString().trim() : "Mon - Sat: 08:00 AM - 08:00 PM (24/7 Emergency)";

        Map<String, Object> data = new HashMap<>();
        data.put("hospitalName", name);
        data.put("hospitalEmail", email);
        data.put("hospitalPhone", phone);
        data.put("workingDays", hours);
        data.put("updatedAt", System.currentTimeMillis());

        db.collection("settings").document("hospital_info")
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Hospital settings saved to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving hospital settings to Firestore", e));
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
