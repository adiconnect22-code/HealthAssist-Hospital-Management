package com.example.healthcare.doctors_section;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.healthcare.appts_section.AppointmentsActivity;
import com.example.healthcare.more_section.MoreMenuDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity in 'doctors_section' folder for Managing Doctors matching design mockup.
 */
public class DoctorsActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorActionListener {

    private TextView tvDoctorsLimitCount;
    private EditText etSearchDoctor;
    private RecyclerView rvDoctors;
    private TextView tvNoDoctorsFound;

    private MaterialButton btnAddDoctor;
    private MaterialButton btnMarkUnavailable;

    private BottomNavigationView bottomNavigationView;

    private DoctorAdapter doctorAdapter;
    private final ArrayList<DoctorModel> doctorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        initViews();
        setupSeamlessBottomNavigation();
        setupRecyclerView();
        loadSampleDoctors();
        setupSearchFilter();
        setupClickListeners();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSeamlessBottomNavigation();
    }

    private void initViews() {
        tvDoctorsLimitCount = findViewById(R.id.tvDoctorsLimitCount);
        etSearchDoctor = findViewById(R.id.etSearchDoctor);
        rvDoctors = findViewById(R.id.rvDoctors);
        tvNoDoctorsFound = findViewById(R.id.tvNoDoctorsFound);

        btnAddDoctor = findViewById(R.id.btnAddDoctor);
        btnMarkUnavailable = findViewById(R.id.btnMarkUnavailable);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        doctorAdapter = new DoctorAdapter(this, doctorList, this);
        rvDoctors.setAdapter(doctorAdapter);
    }

    private void loadSampleDoctors() {
        doctorList.clear();

        doctorList.add(new DoctorModel("DOC-101", "Dr. Mehta", "Cardiology", "Interventional Cardiology", "M.D. Cardiology", "15 yrs · Male", "+91 9876500001", "10/day", "09:00 AM - 01:00 PM"));
        doctorList.add(new DoctorModel("DOC-102", "Dr. Iyer", "Orthopedics", "Joint Replacement", "MS Ortho", "12 yrs · Male", "+91 9876500002", "8/day", "10:00 AM - 02:00 PM"));
        doctorList.add(new DoctorModel("DOC-103", "Dr. Vikram Mehta", "Neurology", "Neuro Surgery", "M.Ch Neurology", "18 yrs · Male", "+91 9876500003", "12/day", "11:00 AM - 03:00 PM"));
        doctorList.add(new DoctorModel("DOC-104", "Dr. Sarah Smith", "Pediatrics", "Child Care", "MD Pediatrics", "8 yrs · Female", "+91 9876500004", "15/day", "08:00 AM - 12:00 PM"));
        doctorList.add(new DoctorModel("DOC-105", "Dr. Emily Davis", "Dermatology", "Skin & Laser", "MD Dermatology", "10 yrs · Female", "+91 9876500005", "10/day", "02:00 PM - 06:00 PM"));

        updateDoctorsUI();

        fetchDoctorsFromDatabase();
    }

    private void setupSearchFilter() {
        if (etSearchDoctor == null) return;

        etSearchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (doctorAdapter != null) {
                    boolean isEmpty = doctorAdapter.filter(s != null ? s.toString() : "");
                    if (isEmpty) {
                        tvNoDoctorsFound.setVisibility(View.VISIBLE);
                        rvDoctors.setVisibility(View.GONE);
                    } else {
                        tvNoDoctorsFound.setVisibility(View.GONE);
                        rvDoctors.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupClickListeners() {
        if (btnAddDoctor != null) {
            btnAddDoctor.setOnClickListener(v -> {
                AddEditDoctorDialog dialog = AddEditDoctorDialog.newInstance(null);
                dialog.setOnDoctorSavedListener(newDoc -> {
                    doctorList.add(0, newDoc);
                    updateDoctorsUI();
                    saveDoctorToDatabase(newDoc);
                });
                dialog.show(getSupportFragmentManager(), "AddDoctorDialog");
            });
        }

        if (btnMarkUnavailable != null) {
            btnMarkUnavailable.setOnClickListener(v -> {
                MarkDoctorUnavailableDialog dialog = MarkDoctorUnavailableDialog.newInstance(doctorList);
                dialog.setOnLeaveSavedListener((doctorName, startDate, endDate, reason) -> {
                    for (DoctorModel doc : doctorList) {
                        if (doc.getName() != null && doc.getName().equalsIgnoreCase(doctorName)) {
                            doc.setLeaveStartDate(startDate);
                            doc.setLeaveEndDate(endDate);
                            doc.setLeaveReason(reason);
                            doc.setUnavailable(true);
                            markDoctorLeaveInDatabase(doc);
                            break;
                        }
                    }
                    updateDoctorsUI();
                });
                dialog.show(getSupportFragmentManager(), "MarkUnavailableDialog");
            });
        }
    }

    @Override
    public void onEditDoctor(DoctorModel doctor) {
        AddEditDoctorDialog dialog = AddEditDoctorDialog.newInstance(doctor);
        dialog.setOnDoctorSavedListener(updatedDoc -> {
            for (int i = 0; i < doctorList.size(); i++) {
                if (doctorList.get(i).getId().equals(updatedDoc.getId())) {
                    doctorList.set(i, updatedDoc);
                    break;
                }
            }
            updateDoctorsUI();
            saveDoctorToDatabase(updatedDoc);
        });
        dialog.show(getSupportFragmentManager(), "EditDoctorDialog");
    }

    @Override
    public void onDeleteDoctor(DoctorModel doctor) {
        if (doctor == null) return;

        doctorList.remove(doctor);
        updateDoctorsUI();
        deleteDoctorFromDatabase(doctor);
        Toast.makeText(this, doctor.getName() + " deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateDoctorsUI() {
        if (tvDoctorsLimitCount != null) {
            tvDoctorsLimitCount.setText("(" + doctorList.size() + " / max 20)");
        }

        if (doctorAdapter != null) {
            doctorAdapter.updateList(doctorList);
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(R.id.nav_doctors);

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
                return true;
            } else if (itemId == R.id.nav_more) {
                MoreMenuDialog.newInstance().show(getSupportFragmentManager(), "MoreDialog");
                return true;
            }
            return false;
        });
    }

    private void fetchDoctorsFromDatabase() {
        // TODO: Database connection logic
    }

    private void saveDoctorToDatabase(DoctorModel doctor) {
        // TODO: Database connection logic
    }

    private void markDoctorLeaveInDatabase(DoctorModel doctor) {
        // TODO: Database connection logic
    }

    private void deleteDoctorFromDatabase(DoctorModel doctor) {
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
