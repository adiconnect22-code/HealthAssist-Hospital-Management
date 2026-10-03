package com.example.healthcare.admin_section;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.appts_section.AppointmentDetailsDialog;
import com.example.healthcare.appts_section.AppointmentModel;
import com.example.healthcare.appts_section.AppointmentsActivity;
import com.example.healthcare.appts_section.RecentAppointmentAdapter;
import com.example.healthcare.common.NotificationListDialog;
import com.example.healthcare.common.PatientsListDialog;
import com.example.healthcare.common.RevenueBreakdownDialog;
import com.example.healthcare.doctors_section.DoctorsListDialog;
import com.example.healthcare.more_section.MoreMenuDialog;
import com.example.healthcare.more_section.SettingsProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hospital Admin Dashboard Activity located in 'admin_section' package folder.
 */
public class AdminDashboardActivity extends AppCompatActivity implements RecentAppointmentAdapter.OnRecentAppointmentClickListener {

    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private ImageView imgProfile;
    private ImageView imgNotification;

    private View cardPatients;
    private View cardDoctors;
    private View cardOrders;
    private View cardRevenue;

    private AppointmentsDonutChartView donutChartView;
    private TextView tvOverviewConfirmed;
    private TextView tvOverviewCompleted;
    private TextView tvOverviewPending;
    private TextView tvOverviewCancelled;

    private RecyclerView rvAdminOrders;
    private TextView tvEmptyOrdersMessage;
    private TextView btnViewAllOrders;
    private RecentAppointmentAdapter appointmentAdapter;

    private MaterialButton btnManagePatients;
    private MaterialButton btnManageDoctors;

    private BottomNavigationView bottomNavigationView;

    private final List<AppointmentModel> recentAppointmentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.content.SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupSeamlessBottomNavigation();
        setupStatCardTitlesAndIcons();
        setupAppointmentsRecyclerView();
        setupClickListeners();
        setupBottomNavigation();

        loadUiPreviewData();

        fetchHospitalStatsFromDatabase();
        fetchAppointmentsOverviewFromDatabase();
        fetchRecentAppointmentsFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSeamlessBottomNavigation();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        imgProfile = findViewById(R.id.imgProfile);
        imgNotification = findViewById(R.id.imgNotification);

        cardPatients = findViewById(R.id.cardPatients);
        cardDoctors = findViewById(R.id.cardDoctors);
        cardOrders = findViewById(R.id.cardOrders);
        cardRevenue = findViewById(R.id.cardRevenue);

        donutChartView = findViewById(R.id.donutChartView);
        tvOverviewConfirmed = findViewById(R.id.tvOverviewConfirmed);
        tvOverviewCompleted = findViewById(R.id.tvOverviewCompleted);
        tvOverviewPending = findViewById(R.id.tvOverviewPending);
        tvOverviewCancelled = findViewById(R.id.tvOverviewCancelled);

        rvAdminOrders = findViewById(R.id.rvAdminOrders);
        tvEmptyOrdersMessage = findViewById(R.id.tvEmptyOrdersMessage);
        btnViewAllOrders = findViewById(R.id.btnViewAllOrders);

        btnManagePatients = findViewById(R.id.btnManagePatients);
        btnManageDoctors = findViewById(R.id.btnManageDoctors);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupStatCardTitlesAndIcons() {
        bindStatCardView(cardPatients, "Total Patients", "142", "Active Inpatients", R.drawable.ic_patients, R.color.stat_bg_blue);
        bindStatCardView(cardDoctors, "Available Doctors", "28", "On Duty Today", R.drawable.ic_doctors, R.color.stat_bg_green);
        bindStatCardView(cardOrders, "Appointments Today", "18", "Scheduled Visits", R.drawable.ic_orders, R.color.stat_bg_orange);
        bindStatCardView(cardRevenue, "Total Revenue", "$11.2k", "Monthly Earnings", R.drawable.ic_revenue, R.color.stat_bg_purple);
    }

    private void bindStatCardView(View cardView, String title, String value, String subtitle, int iconResId, int bgResId) {
        if (cardView == null) return;

        TextView tvTitle = cardView.findViewById(R.id.tvStatTitle);
        TextView tvValue = cardView.findViewById(R.id.tvStatValue);
        TextView tvSubtitle = cardView.findViewById(R.id.tvStatSubtitle);
        ImageView imgIcon = cardView.findViewById(R.id.imgStatIcon);

        if (tvTitle != null) tvTitle.setText(title);
        if (tvValue != null) tvValue.setText(value);
        if (tvSubtitle != null) tvSubtitle.setText(subtitle);
        if (imgIcon != null) {
            imgIcon.setImageResource(iconResId);
            if (imgIcon.getBackground() != null) {
                imgIcon.getBackground().setTint(ContextCompat.getColor(this, bgResId));
            } else {
                imgIcon.setBackgroundColor(ContextCompat.getColor(this, bgResId));
            }
        }
    }

    private void setupAppointmentsRecyclerView() {
        rvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new RecentAppointmentAdapter(this, recentAppointmentsList, this);
        rvAdminOrders.setAdapter(appointmentAdapter);
    }

    private void setupClickListeners() {
        if (imgProfile != null) {
            imgProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsProfileActivity.class);
                startActivity(intent);
            });
        }

        imgNotification.setOnClickListener(v ->
                NotificationListDialog.newInstance().show(getSupportFragmentManager(), "NotificationDialog")
        );

        cardPatients.setOnClickListener(v ->
                PatientsListDialog.newInstance().show(getSupportFragmentManager(), "PatientsDialog")
        );

        cardDoctors.setOnClickListener(v ->
                DoctorsListDialog.newInstance().show(getSupportFragmentManager(), "DoctorsDialog")
        );

        cardRevenue.setOnClickListener(v ->
                RevenueBreakdownDialog.newInstance().show(getSupportFragmentManager(), "RevenueDialog")
        );

        cardOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppointmentsActivity.class);
            startActivity(intent);
        });

        btnViewAllOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppointmentsActivity.class);
            startActivity(intent);
        });

        btnManagePatients.setOnClickListener(v ->
                PatientsListDialog.newInstance().show(getSupportFragmentManager(), "PatientsDialog")
        );

        btnManageDoctors.setOnClickListener(v ->
                DoctorsListDialog.newInstance().show(getSupportFragmentManager(), "DoctorsDialog")
        );
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) return;

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_appts) {
                Intent intent = new Intent(this, AppointmentsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_doctors) {
                Intent intent = new Intent(this, com.example.healthcare.doctors_section.DoctorsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_more) {
                MoreMenuDialog.newInstance().show(getSupportFragmentManager(), "MoreDialog");
                return true;
            }
            return false;
        });
    }

    private void loadUiPreviewData() {
        updateAppointmentsOverviewUI(18, 6, 3, 1);

        recentAppointmentsList.clear();
        recentAppointmentsList.add(new AppointmentModel("Ravi Kumar", "Dr. Mehta", "Cardiology", "Today, 02:30 PM", "Confirmed"));
        recentAppointmentsList.add(new AppointmentModel("Amit Sharma", "Dr. Iyer", "Orthopedics", "Today, 04:15 PM", "Completed"));
        recentAppointmentsList.add(new AppointmentModel("Sneha Patil", "Dr. Arjun Patel", "General", "Tomorrow, 10:00 AM", "Pending"));
        recentAppointmentsList.add(new AppointmentModel("Rahul Verma", "Dr. Sharma", "Cardiology", "25 Aug, 02:00 PM", "Confirmed"));

        if (appointmentAdapter != null) {
            appointmentAdapter.updateList(recentAppointmentsList);
        }
        checkEmptyState();
    }

    public void updateAppointmentsOverviewUI(int confirmed, int completed, int pending, int cancelled) {
        if (donutChartView != null) {
            donutChartView.setOverviewData(confirmed, completed, pending, cancelled);
        }

        int total = Math.max(1, confirmed + completed + pending + cancelled);

        int pctConfirmed = Math.round((confirmed * 100f) / total);
        int pctCompleted = Math.round((completed * 100f) / total);
        int pctPending = Math.round((pending * 100f) / total);
        int pctCancelled = Math.round((cancelled * 100f) / total);

        if (tvOverviewConfirmed != null) {
            tvOverviewConfirmed.setText("Confirmed · " + confirmed + " (" + pctConfirmed + "%)");
        }
        if (tvOverviewCompleted != null) {
            tvOverviewCompleted.setText("Completed · " + completed + " (" + pctCompleted + "%)");
        }
        if (tvOverviewPending != null) {
            tvOverviewPending.setText("Pending · " + pending + " (" + pctPending + "%)");
        }
        if (tvOverviewCancelled != null) {
            tvOverviewCancelled.setText("Cancelled · " + cancelled + " (" + pctCancelled + "%)");
        }
    }

    @Override
    public void onAppointmentClick(AppointmentModel appointment) {
        AppointmentDetailsDialog dialog = AppointmentDetailsDialog.newInstance(appointment);
        dialog.show(getSupportFragmentManager(), "AppointmentDetailsDialog");
    }

    private void fetchHospitalStatsFromDatabase() {
        // TODO: Database connection logic
    }

    private void fetchAppointmentsOverviewFromDatabase() {
        // TODO: Database connection logic
    }

    private void fetchRecentAppointmentsFromDatabase() {
        // TODO: Database connection logic
    }

    private void checkEmptyState() {
        if (recentAppointmentsList == null || recentAppointmentsList.isEmpty()) {
            tvEmptyOrdersMessage.setVisibility(View.VISIBLE);
            rvAdminOrders.setVisibility(View.GONE);
        } else {
            tvEmptyOrdersMessage.setVisibility(View.GONE);
            rvAdminOrders.setVisibility(View.VISIBLE);
        }
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
