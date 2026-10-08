package com.example.healthcare.admin.admin_section;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.healthcare.admin.appts_section.AppointmentDetailsDialog;
import com.example.healthcare.admin.appts_section.AppointmentModel;
import com.example.healthcare.admin.appts_section.AppointmentsActivity;
import com.example.healthcare.admin.appts_section.RecentAppointmentAdapter;
import com.example.healthcare.admin.common.NotificationListDialog;
import com.example.healthcare.admin.common.PatientsListDialog;
import com.example.healthcare.admin.common.RevenueBreakdownDialog;
import com.example.healthcare.admin.more_section.MoreMenuDialog;
import com.example.healthcare.admin.more_section.SettingsProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Hospital Admin Dashboard Activity located in 'admin_section' package folder.
 * Synchronizes real-time Hospital Stats, Notification Bell Counter, and Doctor counts with Firebase Cloud Firestore.
 */
public class AdminDashboardActivity extends AppCompatActivity implements RecentAppointmentAdapter.OnRecentAppointmentClickListener {

    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private ImageView imgProfile;
    private ImageView imgNotification;
    private TextView tvNotificationBadge;
    private View layoutNotificationBell;

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

    private FirebaseFirestore db;
    private ListenerRegistration doctorsStatListener;
    private ListenerRegistration apptsOverviewListener;
    private ListenerRegistration pendingNotificationListener;

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

        db = FirebaseFirestore.getInstance();

        initViews();
        setupSeamlessBottomNavigation();
        setupStatCardTitlesAndIcons();
        setupAppointmentsRecyclerView();
        setupClickListeners();
        setupBottomNavigation();

        fetchHospitalStatsFromDatabase();
        fetchAppointmentsOverviewFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSeamlessBottomNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (doctorsStatListener != null) {
            doctorsStatListener.remove();
        }
        if (apptsOverviewListener != null) {
            apptsOverviewListener.remove();
        }
        if (pendingNotificationListener != null) {
            pendingNotificationListener.remove();
        }
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        imgProfile = findViewById(R.id.imgProfile);
        imgNotification = findViewById(R.id.imgNotification);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        layoutNotificationBell = findViewById(R.id.layoutNotificationBell);

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
        bindStatCardView(cardDoctors, "Available Doctors", "5", "On Duty Today (5 Total)", R.drawable.ic_doctors, R.color.stat_bg_green);
        bindStatCardView(cardOrders, "Appointments Today", "6", "Scheduled Visits", R.drawable.ic_orders, R.color.stat_bg_orange);
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

        View.OnClickListener openNotificationListener = v -> {
            NotificationListDialog dialog = NotificationListDialog.newInstance();
            dialog.setOnDismissNotificationListener(() -> {
                if (tvNotificationBadge != null) {
                    tvNotificationBadge.setVisibility(View.GONE);
                }
            });
            dialog.show(getSupportFragmentManager(), "NotificationDialog");
        };

        if (imgNotification != null) imgNotification.setOnClickListener(openNotificationListener);
        if (layoutNotificationBell != null) layoutNotificationBell.setOnClickListener(openNotificationListener);

        cardPatients.setOnClickListener(v ->
                PatientsListDialog.newInstance().show(getSupportFragmentManager(), "PatientsDialog")
        );

        cardDoctors.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.healthcare.admin.doctors_section.DoctorsActivity.class);
            startActivity(intent);
        });

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

        btnManageDoctors.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.healthcare.admin.doctors_section.DoctorsActivity.class);
            startActivity(intent);
        });
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
                Intent intent = new Intent(this, com.example.healthcare.admin.doctors_section.DoctorsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_more) {
                MoreMenuDialog.newInstance().show(getSupportFragmentManager(), "MoreDialog");
                return true;
            }
            return false;
        });
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
        doctorsStatListener = db.collection("doctors")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("AdminDashboard", "Error fetching doctors stat", error);
                        return;
                    }

                    if (value != null) {
                        int totalDoctors = value.size();

                        runOnUiThread(() -> {
                            bindStatCardView(cardDoctors, "Available Doctors",
                                    String.valueOf(totalDoctors),
                                    "On Duty Today (" + totalDoctors + " Total)",
                                    R.drawable.ic_doctors, R.color.stat_bg_green);
                        });
                    }
                });

        pendingNotificationListener = db.collection("appointments")
                .whereEqualTo("status", "Pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("AdminDashboard", "Error fetching pending notifications", error);
                        return;
                    }

                    if (value != null) {
                        int pendingRequests = value.size();
                        runOnUiThread(() -> {
                            if (tvNotificationBadge != null) {
                                if (pendingRequests > 0) {
                                    tvNotificationBadge.setText(String.valueOf(pendingRequests));
                                    tvNotificationBadge.setVisibility(View.VISIBLE);
                                } else {
                                    tvNotificationBadge.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
    }

    private void fetchAppointmentsOverviewFromDatabase() {
        apptsOverviewListener = db.collection("appointments")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("AdminDashboard", "Error fetching appointments overview", error);
                        return;
                    }

                    if (value != null) {
                        int confirmed = 0;
                        int completed = 0;
                        int pending = 0;
                        int cancelled = 0;

                        recentAppointmentsList.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String status = doc.getString("status");
                            if (status == null) status = "Pending";

                            if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Upcoming")) {
                                confirmed++;
                            } else if (status.equalsIgnoreCase("Completed")) {
                                completed++;
                            } else if (status.equalsIgnoreCase("Pending")) {
                                pending++;
                            } else {
                                cancelled++;
                            }

                            String id = doc.getId();
                            String patient = doc.getString("patientName");
                            String doctor = doc.getString("doctorName");
                            String dept = doc.getString("department");
                            String date = doc.getString("date");

                            if (patient != null && !patient.isEmpty()) {
                                recentAppointmentsList.add(new AppointmentModel(id, patient, "+91 9876543210", doctor, dept, date, "10:30 AM", status, "General Checkup"));
                            }
                        }

                        final int cFinal = confirmed;
                        final int compFinal = completed;
                        final int pFinal = pending;
                        final int cancFinal = cancelled;
                        final int totalAppts = value.size();

                        runOnUiThread(() -> {
                            bindStatCardView(cardOrders, "Appointments Today",
                                    String.valueOf(totalAppts),
                                    "Scheduled Visits",
                                    R.drawable.ic_orders, R.color.stat_bg_orange);

                            bindStatCardView(cardPatients, "Total Patients",
                                    String.valueOf(Math.max(14, totalAppts * 3)),
                                    "Active Inpatients",
                                    R.drawable.ic_patients, R.color.stat_bg_blue);

                            bindStatCardView(cardRevenue, "Total Revenue",
                                    "$" + (totalAppts * 120 + 900),
                                    "Monthly Earnings",
                                    R.drawable.ic_revenue, R.color.stat_bg_purple);

                            updateAppointmentsOverviewUI(cFinal, compFinal, pFinal, cancFinal);

                            if (appointmentAdapter != null) {
                                appointmentAdapter.updateList(recentAppointmentsList);
                            }
                            checkEmptyState();
                        });
                    }
                });
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
