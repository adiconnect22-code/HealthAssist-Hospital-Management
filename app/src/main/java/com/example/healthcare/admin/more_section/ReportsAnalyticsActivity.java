package com.example.healthcare.admin.more_section;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
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

/**
 * Activity in 'more_section' folder.
 */
public class ReportsAnalyticsActivity extends AppCompatActivity {

    private MaterialButton btnExportCsv;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_analytics);

        initViews();
        setupSeamlessBottomNavigation();
        setupClickListeners();
        setupBottomNavigation();

        fetchAnalyticsFromDatabase();
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

        btnExportCsv = findViewById(R.id.btnExportCsv);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupClickListeners() {
        if (btnExportCsv != null) {
            btnExportCsv.setOnClickListener(v ->
                    ExportCsvDialog.newInstance().show(getSupportFragmentManager(), "ExportCsvDialog")
            );
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

    private void fetchAnalyticsFromDatabase() {
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
