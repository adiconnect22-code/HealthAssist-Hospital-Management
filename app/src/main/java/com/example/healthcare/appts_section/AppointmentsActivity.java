package com.example.healthcare.appts_section;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.admin_section.AdminDashboardActivity;
import com.example.healthcare.doctors_section.DoctorsListDialog;
import com.example.healthcare.more_section.MoreMenuDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity in 'appts_section' folder for managing Appointments.
 */
public class AppointmentsActivity extends AppCompatActivity {

    private TextView tabAll;
    private TextView tabUpcoming;
    private TextView tabPending;
    private TextView tabCompleted;

    private LinearLayout layoutContentAll;
    private LinearLayout layoutContentUpcoming;
    private LinearLayout layoutContentPending;
    private LinearLayout layoutContentCompleted;

    private EditText etSearchAppts;
    private RecyclerView rvAllAppointments;
    private TextView tvNoApptsFound;
    private AppointmentAdapter appointmentAdapter;
    private final List<AppointmentModel> allAppointmentList = new ArrayList<>();

    private ImageView btnApptsBack;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        initViews();
        setupSeamlessBottomNavigation();
        setupAllAppointmentsRecyclerView();
        setupSearchFilter();
        setupTabListeners();
        setupActionListeners();
        setupBottomNavigation();

        selectTab(tabAll, layoutContentAll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSeamlessBottomNavigation();
    }

    private void initViews() {
        btnApptsBack = findViewById(R.id.btnApptsBack);

        tabAll = findViewById(R.id.tabAll);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabPending = findViewById(R.id.tabPending);
        tabCompleted = findViewById(R.id.tabCompleted);

        layoutContentAll = findViewById(R.id.layoutContentAll);
        layoutContentUpcoming = findViewById(R.id.layoutContentUpcoming);
        layoutContentPending = findViewById(R.id.layoutContentPending);
        layoutContentCompleted = findViewById(R.id.layoutContentCompleted);

        etSearchAppts = findViewById(R.id.etSearchAppts);
        rvAllAppointments = findViewById(R.id.rvAllAppointments);
        tvNoApptsFound = findViewById(R.id.tvNoApptsFound);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupAllAppointmentsRecyclerView() {
        allAppointmentList.clear();
        allAppointmentList.add(new AppointmentModel("Amit Sharma", "Dr. Mehta", "Cardiology", "28 Aug, 4:30 PM", "Confirmed"));
        allAppointmentList.add(new AppointmentModel("Sneha Patil", "Dr. Iyer", "General", "28 Aug, 11:30 AM", "Completed"));
        allAppointmentList.add(new AppointmentModel("Priya Das", "Dr. Arjun Patel", "Cardiology", "26 Aug, 11:00 AM", "Pending"));
        allAppointmentList.add(new AppointmentModel("Kunal Gupta", "Dr. Arjun Patel", "Cardiology", "27 Aug, 3:15 PM", "Pending"));
        allAppointmentList.add(new AppointmentModel("Deepika P.", "Dr. Priya Sharma", "Pediatrics", "25 Aug, 10:00 AM", "Cancelled"));
        allAppointmentList.add(new AppointmentModel("Rahul", "Dr. Sharma", "General", "25 Aug, 2:00 PM", "Pending"));
        allAppointmentList.add(new AppointmentModel("Suresh Raina", "Dr. Arjun Patel", "Cardiology", "26 Aug, 11:00 AM", "Pending"));
        allAppointmentList.add(new AppointmentModel("Robert Brown", "Dr. Michael Clark", "Orthopedics", "29 Aug, 1:00 PM", "Confirmed"));

        rvAllAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(this, allAppointmentList);
        rvAllAppointments.setAdapter(appointmentAdapter);
    }

    private void setupSearchFilter() {
        if (etSearchAppts == null) return;

        etSearchAppts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (appointmentAdapter != null) {
                    boolean isEmpty = appointmentAdapter.filter(s != null ? s.toString() : "");
                    if (isEmpty) {
                        tvNoApptsFound.setVisibility(View.VISIBLE);
                        rvAllAppointments.setVisibility(View.GONE);
                    } else {
                        tvNoApptsFound.setVisibility(View.GONE);
                        rvAllAppointments.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(tabAll, layoutContentAll));
        tabUpcoming.setOnClickListener(v -> selectTab(tabUpcoming, layoutContentUpcoming));
        tabPending.setOnClickListener(v -> selectTab(tabPending, layoutContentPending));
        tabCompleted.setOnClickListener(v -> selectTab(tabCompleted, layoutContentCompleted));
    }

    private void selectTab(TextView selectedTab, LinearLayout selectedContent) {
        resetTabStyle(tabAll);
        resetTabStyle(tabUpcoming);
        resetTabStyle(tabPending);
        resetTabStyle(tabCompleted);

        layoutContentAll.setVisibility(View.GONE);
        layoutContentUpcoming.setVisibility(View.GONE);
        layoutContentPending.setVisibility(View.GONE);
        layoutContentCompleted.setVisibility(View.GONE);

        selectedTab.setBackgroundResource(R.drawable.bg_popup_rounded);
        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.teal_primary));

        selectedContent.setVisibility(View.VISIBLE);
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackground(null);
        tab.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
    }

    private void setupActionListeners() {
        if (btnApptsBack != null) {
            btnApptsBack.setOnClickListener(v -> finish());
        }

        MaterialButton btnApprove1 = findViewById(R.id.btnApprovePending1);
        MaterialButton btnReject1 = findViewById(R.id.btnRejectPending1);
        TextView btnPriority1 = findViewById(R.id.btnMarkPriority1);

        if (btnApprove1 != null) {
            btnApprove1.setOnClickListener(v ->
                    Toast.makeText(this, "Appointment Approved", Toast.LENGTH_SHORT).show()
            );
        }
        if (btnReject1 != null) {
            btnReject1.setOnClickListener(v ->
                    Toast.makeText(this, "Appointment Rejected", Toast.LENGTH_SHORT).show()
            );
        }
        if (btnPriority1 != null) {
            btnPriority1.setOnClickListener(v ->
                    Toast.makeText(this, "Marked as Priority", Toast.LENGTH_SHORT).show()
            );
        }

        MaterialButton btnComplete = findViewById(R.id.btnCompleteInProgress);
        if (btnComplete != null) {
            btnComplete.setOnClickListener(v ->
                    Toast.makeText(this, "Marked as Completed", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(R.id.nav_appts);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_appts) {
                return true;
            } else if (itemId == R.id.nav_doctors) {
                Intent intent = new Intent(this, com.example.healthcare.doctors_section.DoctorsActivity.class);
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
