package com.example.healthcare.admin.appts_section;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.healthcare.admin.admin_section.AdminDashboardActivity;
import com.example.healthcare.admin.more_section.MoreMenuDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity in 'appts_section' for managing Hospital Appointments.
 * Fully connected to Firebase Cloud Firestore real-time database with 12-hour automated deletion for cancelled appointments.
 */
public class AppointmentsActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentActionListener, AppointmentDetailsDialog.OnAppointmentStatusChangeListener {

    private static final String TAG = "AppointmentsActivity";
    private static final long TWELVE_HOURS_MS = 12 * 60 * 60 * 1000L; // 12 hours in milliseconds

    private TextView tabAll;
    private TextView tabUpcoming;
    private TextView tabPending;
    private TextView tabCompleted;

    private EditText etSearchAppts;
    private RecyclerView rvAllAppointments;
    private TextView tvNoApptsFound;

    private AppointmentAdapter appointmentAdapter;
    private final List<AppointmentModel> allAppointmentList = new ArrayList<>();

    private ImageView btnApptsBack;
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;
    private ListenerRegistration firestoreListener;
    private String currentSelectedTab = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupSeamlessBottomNavigation();
        setupAllAppointmentsRecyclerView();
        setupSearchFilter();
        setupTabListeners();
        setupActionListeners();
        setupBottomNavigation();

        selectTab(tabAll, "All");

        // Immediately load local default list so UI is never blank
        loadLocalDefaultAppointments();

        setupFirestoreRealtimeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSeamlessBottomNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }

    private void initViews() {
        btnApptsBack = findViewById(R.id.btnApptsBack);

        tabAll = findViewById(R.id.tabAll);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabPending = findViewById(R.id.tabPending);
        tabCompleted = findViewById(R.id.tabCompleted);

        etSearchAppts = findViewById(R.id.etSearchAppts);
        rvAllAppointments = findViewById(R.id.rvAllAppointments);
        tvNoApptsFound = findViewById(R.id.tvNoApptsFound);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupAllAppointmentsRecyclerView() {
        rvAllAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(this, allAppointmentList, this);
        rvAllAppointments.setAdapter(appointmentAdapter);
    }

    private void setupFirestoreRealtimeListener() {
        firestoreListener = db.collection("appointments")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Firestore listen failed: ", error);
                        runOnUiThread(this::loadLocalDefaultAppointments);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        allAppointmentList.clear();
                        long now = System.currentTimeMillis();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            try {
                                AppointmentModel model = doc.toObject(AppointmentModel.class);
                                if (model == null) {
                                    model = new AppointmentModel();
                                }

                                String id = doc.getId();
                                String patient = doc.getString("patientName");
                                if (patient == null || patient.trim().isEmpty()) {
                                    patient = doc.getString("patient");
                                }
                                if (patient == null || patient.trim().isEmpty()) {
                                    patient = "Patient " + id;
                                }

                                model.setId(id);
                                model.setPatientName(patient);

                                if (model.getDoctorName() == null || model.getDoctorName().isEmpty()) {
                                    model.setDoctorName(doc.getString("doctorName"));
                                }
                                if (model.getDepartment() == null || model.getDepartment().isEmpty()) {
                                    model.setDepartment(doc.getString("department"));
                                }
                                if (model.getDate() == null || model.getDate().isEmpty()) {
                                    model.setDate(doc.getString("date"));
                                }
                                if (model.getTimeSlot() == null || model.getTimeSlot().isEmpty()) {
                                    model.setTimeSlot(doc.getString("timeSlot"));
                                }
                                if (model.getStatus() == null || model.getStatus().isEmpty()) {
                                    model.setStatus(doc.getString("status"));
                                }
                                if (model.getReason() == null || model.getReason().isEmpty()) {
                                    model.setReason(doc.getString("reason"));
                                }

                                Long cancelledTime = doc.getLong("cancelledTimestamp");
                                if (cancelledTime != null && cancelledTime > 0) {
                                    model.setCancelledTimestamp(cancelledTime);

                                    // 12-Hour Auto-Deletion Check
                                    if ((now - cancelledTime) >= TWELVE_HOURS_MS) {
                                        deleteAppointmentFromDatabase(id);
                                        continue; // Skip expired cancelled appointment
                                    }
                                }

                                allAppointmentList.add(model);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing appointment snapshot", e);
                            }
                        }
                        runOnUiThread(this::updateAppointmentsUI);
                    } else {
                        runOnUiThread(this::seedRealAppointmentsToFirestore);
                    }
                });
    }

    private void loadLocalDefaultAppointments() {
        if (allAppointmentList.isEmpty()) {
            allAppointmentList.add(new AppointmentModel("APT-1001", "Amit Sharma", "+91 9876543210", "Dr. Mehta", "Cardiology", "28 Aug", "4:30 PM", "Confirmed", "Heart Assessment"));
            allAppointmentList.add(new AppointmentModel("APT-1002", "Sneha Patil", "+91 9876543211", "Dr. Iyer", "General", "28 Aug", "11:30 AM", "Completed", "Routine Checkup"));
            allAppointmentList.add(new AppointmentModel("APT-1003", "Priya Das", "+91 9876543212", "Dr. Arjun Patel", "Cardiology", "26 Aug", "11:00 AM", "Pending", "ECG Consultation"));
            allAppointmentList.add(new AppointmentModel("APT-1004", "Kunal Gupta", "+91 9876543213", "Dr. Arjun Patel", "Cardiology", "27 Aug", "3:15 PM", "Pending", "Heart Rate Followup"));
            allAppointmentList.add(new AppointmentModel("APT-1005", "Deepika P.", "+91 9876543214", "Dr. Priya Sharma", "Pediatrics", "25 Aug", "10:00 AM", "Cancelled", "Child Care Checkup"));
            allAppointmentList.add(new AppointmentModel("APT-1006", "Rahul", "+91 9876543215", "Dr. Sharma", "General", "25 Aug", "2:00 PM", "Pending", "Fever Followup"));
            allAppointmentList.add(new AppointmentModel("APT-1007", "Ravi Kumar", "+91 9876543216", "Dr. Rahul Sharma", "General Physician", "Today", "02:30 PM", "Confirmed", "Regular Medical Review"));
        }
        updateAppointmentsUI();
    }

    private void seedRealAppointmentsToFirestore() {
        loadLocalDefaultAppointments();

        for (AppointmentModel appointment : allAppointmentList) {
            saveAppointmentToDatabase(appointment);
        }
    }

    private void setupSearchFilter() {
        if (etSearchAppts == null) return;

        etSearchAppts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (appointmentAdapter != null) {
                    String query = (s != null) ? s.toString() : "";
                    boolean isEmpty = appointmentAdapter.filter(query, currentSelectedTab);
                    if (isEmpty && !query.trim().isEmpty()) {
                        tvNoApptsFound.setVisibility(View.VISIBLE);
                        rvAllAppointments.setVisibility(View.GONE);
                    } else {
                        tvNoApptsFound.setVisibility(View.GONE);
                        rvAllAppointments.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v -> selectTab(tabAll, "All"));
        tabUpcoming.setOnClickListener(v -> selectTab(tabUpcoming, "Upcoming"));
        tabPending.setOnClickListener(v -> selectTab(tabPending, "Pending"));
        tabCompleted.setOnClickListener(v -> selectTab(tabCompleted, "Completed"));
    }

    private void selectTab(TextView selectedTab, String tabName) {
        currentSelectedTab = tabName;

        resetTabStyle(tabAll);
        resetTabStyle(tabUpcoming);
        resetTabStyle(tabPending);
        resetTabStyle(tabCompleted);

        selectedTab.setBackgroundResource(R.drawable.bg_popup_rounded);
        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.teal_primary));

        if (appointmentAdapter != null) {
            String query = (etSearchAppts != null) ? etSearchAppts.getText().toString() : "";
            boolean isEmpty = appointmentAdapter.filter(query, currentSelectedTab);
            if (isEmpty && !query.trim().isEmpty()) {
                tvNoApptsFound.setVisibility(View.VISIBLE);
                rvAllAppointments.setVisibility(View.GONE);
            } else {
                tvNoApptsFound.setVisibility(View.GONE);
                rvAllAppointments.setVisibility(View.VISIBLE);
            }
        }
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackground(null);
        tab.setTextColor(ContextCompat.getColor(this, R.color.admin_text_secondary));
    }

    private void setupActionListeners() {
        if (btnApptsBack != null) {
            btnApptsBack.setOnClickListener(v -> finish());
        }
    }

    @Override
    public void onApproveAppointment(AppointmentModel appointment) {
        if (appointment == null) return;
        appointment.setStatus("Confirmed");
        saveAppointmentToDatabase(appointment);
        Toast.makeText(this, "✓ Appointment Approved & Confirmed for " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectAppointment(AppointmentModel appointment) {
        if (appointment == null) return;
        appointment.setStatus("Cancelled");
        appointment.setCancelledTimestamp(System.currentTimeMillis());
        saveAppointmentToDatabase(appointment);
        Toast.makeText(this, "✕ Appointment Cancelled (Will be auto-deleted in 12 hours)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleteAppointment(AppointmentModel appointment) {
        if (appointment == null) return;
        appointment.setStatus("Completed");
        saveAppointmentToDatabase(appointment);
        Toast.makeText(this, "✓ Appointment Marked as Completed for " + appointment.getPatientName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelAppointment(AppointmentModel appointment) {
        if (appointment == null) return;
        appointment.setStatus("Cancelled");
        appointment.setCancelledTimestamp(System.currentTimeMillis());
        saveAppointmentToDatabase(appointment);
        Toast.makeText(this, "✕ Appointment Cancelled (Visible for 12 hours before auto-delete)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppointmentClick(AppointmentModel appointment) {
        if (appointment == null) return;
        AppointmentDetailsDialog dialog = AppointmentDetailsDialog.newInstance(appointment);
        dialog.setOnAppointmentStatusChangeListener(this);
        dialog.show(getSupportFragmentManager(), "AppointmentDetailsDialog");
    }

    private void updateAppointmentsUI() {
        runOnUiThread(() -> {
            int pendingCount = 0;
            int completedCount = 0;
            for (AppointmentModel item : allAppointmentList) {
                if (item != null && item.getStatus() != null) {
                    if (item.getStatus().equalsIgnoreCase("Pending")) pendingCount++;
                    if (item.getStatus().equalsIgnoreCase("Completed")) completedCount++;
                }
            }

            if (tabAll != null) tabAll.setText("All (" + allAppointmentList.size() + ")");
            if (tabPending != null) tabPending.setText("Pending\n(" + pendingCount + ")");
            if (tabCompleted != null) tabCompleted.setText("Completed\n(" + completedCount + ")");

            if (appointmentAdapter != null) {
                appointmentAdapter.updateList(allAppointmentList);
                String query = (etSearchAppts != null) ? etSearchAppts.getText().toString() : "";
                boolean isEmpty = appointmentAdapter.filter(query, currentSelectedTab);

                if (isEmpty && !query.trim().isEmpty()) {
                    tvNoApptsFound.setVisibility(View.VISIBLE);
                    rvAllAppointments.setVisibility(View.GONE);
                } else {
                    tvNoApptsFound.setVisibility(View.GONE);
                    rvAllAppointments.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void saveAppointmentToDatabase(AppointmentModel appointment) {
        if (appointment == null) return;
        String id = (appointment.getId() != null && !appointment.getId().isEmpty())
                ? appointment.getId() : "APT-" + System.currentTimeMillis();
        appointment.setId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("id", appointment.getId());
        data.put("patientName", appointment.getPatientName());
        data.put("patientPhone", appointment.getPatientPhone());
        data.put("doctorName", appointment.getDoctorName());
        data.put("department", appointment.getDepartment());
        data.put("date", appointment.getDate());
        data.put("timeSlot", appointment.getTimeSlot());
        data.put("status", appointment.getStatus());
        data.put("reason", appointment.getReason());
        data.put("rejectionReason", appointment.getRejectionReason());
        data.put("cancelledTimestamp", appointment.getCancelledTimestamp());

        db.collection("appointments").document(id)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Appointment saved to Firestore: " + id))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving appointment to Firestore", e));
    }

    private void deleteAppointmentFromDatabase(String appointmentId) {
        if (appointmentId == null || appointmentId.isEmpty()) return;
        db.collection("appointments").document(appointmentId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Appointment auto-deleted after 12 hours: " + appointmentId))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting expired appointment from Firestore", e));
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
