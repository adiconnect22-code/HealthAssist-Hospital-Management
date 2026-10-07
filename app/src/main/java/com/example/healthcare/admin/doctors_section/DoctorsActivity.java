package com.example.healthcare.admin.doctors_section;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.healthcare.admin.admin_section.AdminDashboardActivity;
import com.example.healthcare.admin.appts_section.AppointmentsActivity;
import com.example.healthcare.admin.more_section.MoreMenuDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage Doctors Activity located in 'doctors_section' folder.
 * Safely fetches, displays, and updates live Doctor records from Firebase Cloud Firestore.
 */
public class DoctorsActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorActionListener {

    private static final String TAG = "DoctorsActivity";

    private TextView tvDoctorsLimitCount;
    private EditText etSearchDoctor;
    private RecyclerView rvDoctors;
    private TextView tvNoDoctorsFound;

    private MaterialButton btnAddDoctor;
    private MaterialButton btnMarkUnavailable;

    private BottomNavigationView bottomNavigationView;

    private DoctorAdapter doctorAdapter;
    private final ArrayList<DoctorModel> doctorList = new ArrayList<>();

    private FirebaseFirestore db;
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupSeamlessBottomNavigation();
        setupRecyclerView();
        setupSearchFilter();
        setupClickListeners();
        setupBottomNavigation();

        // Immediately load default local list so UI is never blank
        loadLocalDefaultDoctors();

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

    private void setupFirestoreRealtimeListener() {
        firestoreListener = db.collection("doctors")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Firestore listen failed: ", error);
                        runOnUiThread(this::loadLocalDefaultDoctors);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        doctorList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            try {
                                DoctorModel model = doc.toObject(DoctorModel.class);
                                if (model == null) {
                                    model = new DoctorModel();
                                }

                                String id = doc.getId();
                                String name = doc.getString("name");
                                if (name == null || name.trim().isEmpty()) {
                                    name = doc.getString("doctorName");
                                }
                                if (name == null || name.trim().isEmpty()) {
                                    name = "Dr. Specialist";
                                }

                                model.setId(id);
                                model.setName(name);

                                if (model.getDepartment() == null || model.getDepartment().isEmpty()) {
                                    model.setDepartment(doc.getString("department"));
                                }
                                if (model.getSpecialization() == null || model.getSpecialization().isEmpty()) {
                                    model.setSpecialization(doc.getString("specialization"));
                                }
                                if (model.getQualification() == null || model.getQualification().isEmpty()) {
                                    model.setQualification(doc.getString("qualification"));
                                }
                                if (model.getHospitalName() == null || model.getHospitalName().isEmpty()) {
                                    model.setHospitalName(doc.getString("hospitalName"));
                                }
                                if (model.getLocation() == null || model.getLocation().isEmpty()) {
                                    model.setLocation(doc.getString("location"));
                                }
                                if (model.getContactNumber() == null || model.getContactNumber().isEmpty()) {
                                    model.setContactNumber(doc.getString("contactNumber"));
                                }
                                if (model.getEmail() == null || model.getEmail().isEmpty()) {
                                    model.setEmail(doc.getString("email"));
                                }
                                if (model.getFeeCapacity() == null || model.getFeeCapacity().isEmpty()) {
                                    model.setFeeCapacity(doc.getString("feeCapacity"));
                                }
                                if (model.getTimeSlots() == null || model.getTimeSlots().isEmpty()) {
                                    model.setTimeSlots(doc.getString("timeSlots"));
                                }
                                if (model.getAboutSummary() == null || model.getAboutSummary().isEmpty()) {
                                    model.setAboutSummary(doc.getString("aboutSummary"));
                                }
                                if (model.getImageResName() == null || model.getImageResName().isEmpty()) {
                                    model.setImageResName(doc.getString("imageResName"));
                                }

                                Boolean unavail = doc.getBoolean("unavailable");
                                if (unavail != null) {
                                    model.setUnavailable(unavail);
                                }

                                doctorList.add(model);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing doctor document: " + doc.getId(), e);
                            }
                        }
                        runOnUiThread(this::updateDoctorsUI);
                    } else {
                        runOnUiThread(this::seedRealDoctorsToFirestore);
                    }
                });
    }

    private void loadLocalDefaultDoctors() {
        if (doctorList.isEmpty()) {
            doctorList.add(new DoctorModel("DOC-101", "Dr. Rahul Sharma", "General Physician", "MBBS, MD", "City Care Hospital", "Bengaluru", "+91 9876500001", "rahul.sharma@citycare.com", "doc_male_1", false));
            doctorList.add(new DoctorModel("DOC-102", "Dr. Priya Nair", "Dermatologist", "MBBS, MD (Dermatology)", "Manipal Hospital", "Manipal", "+91 9876500002", "priya.nair@manipal.edu", "doc_female_1", false));
            doctorList.add(new DoctorModel("DOC-103", "Dr. Arjun Mehta", "Cardiologist", "MBBS, MD (Cardiology)", "Apollo Hospital", "Mysuru", "+91 9876500003", "arjun.mehta@apollo.com", "doc_male_2", true));
            doctorList.add(new DoctorModel("DOC-104", "Dr. Sneha Iyer", "Pediatrician", "MBBS, MD (Pediatrics)", "Narayana Hospital", "Bengaluru", "+91 9876500004", "sneha.iyer@narayana.com", "doc_female_2", false));
        }
        updateDoctorsUI();
    }

    private void seedRealDoctorsToFirestore() {
        loadLocalDefaultDoctors();

        for (DoctorModel doctor : doctorList) {
            saveDoctorToDatabase(doctor);
        }
    }

    private void setupSearchFilter() {
        if (etSearchDoctor == null) return;

        etSearchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (doctorAdapter != null) {
                    boolean isEmpty = doctorAdapter.filter(s != null ? s.toString() : "");
                    if (isEmpty && s != null && !s.toString().trim().isEmpty()) {
                        tvNoDoctorsFound.setVisibility(View.VISIBLE);
                        rvDoctors.setVisibility(View.GONE);
                    } else {
                        tvNoDoctorsFound.setVisibility(View.GONE);
                        rvDoctors.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        if (btnAddDoctor != null) {
            btnAddDoctor.setOnClickListener(v -> {
                AddEditDoctorDialog dialog = AddEditDoctorDialog.newInstance(null);
                dialog.setOnDoctorSavedListener(this::saveDoctorToDatabase);
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
                            saveDoctorToDatabase(doc);
                            break;
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "MarkUnavailableDialog");
            });
        }
    }

    @Override
    public void onDoctorItemClick(DoctorModel doctor) {
        if (doctor == null) return;
        DoctorDetailsPopupDialog dialog = DoctorDetailsPopupDialog.newInstance(doctor);
        dialog.show(getSupportFragmentManager(), "DoctorDetailsPopupDialog");
    }

    @Override
    public void onEditDoctor(DoctorModel doctor) {
        AddEditDoctorDialog dialog = AddEditDoctorDialog.newInstance(doctor);
        dialog.setOnDoctorSavedListener(this::saveDoctorToDatabase);
        dialog.show(getSupportFragmentManager(), "EditDoctorDialog");
    }

    @Override
    public void onDeleteDoctor(DoctorModel doctor) {
        if (doctor == null) return;
        ConfirmDeleteDoctorDialog dialog = ConfirmDeleteDoctorDialog.newInstance(doctor);
        dialog.setOnDeleteConfirmedListener(docToDelete -> {
            deleteDoctorFromDatabase(docToDelete);
            Toast.makeText(this, "✓ " + docToDelete.getName() + " deleted successfully", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "ConfirmDeleteDoctorDialog");
    }

    private void updateDoctorsUI() {
        if (tvDoctorsLimitCount != null) {
            tvDoctorsLimitCount.setText("(" + doctorList.size() + " / max 20)");
        }

        if (doctorAdapter != null) {
            doctorAdapter.updateList(doctorList);

            String query = (etSearchDoctor != null) ? etSearchDoctor.getText().toString() : "";
            boolean isEmpty = doctorAdapter.filter(query);

            if (isEmpty && !doctorList.isEmpty() && !query.trim().isEmpty()) {
                tvNoDoctorsFound.setVisibility(View.VISIBLE);
                rvDoctors.setVisibility(View.GONE);
            } else {
                tvNoDoctorsFound.setVisibility(View.GONE);
                rvDoctors.setVisibility(View.VISIBLE);
            }
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

    private void saveDoctorToDatabase(DoctorModel doctor) {
        if (doctor == null) return;
        String id = (doctor.getId() != null && !doctor.getId().isEmpty())
                ? doctor.getId() : "DOC-" + System.currentTimeMillis();
        doctor.setId(id);

        String name = doctor.getName();
        if (name == null || name.trim().isEmpty()) {
            name = "Dr. Medical Specialist";
            doctor.setName(name);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", doctor.getId());
        data.put("name", name);
        data.put("doctorName", name);
        data.put("department", doctor.getDepartment() != null ? doctor.getDepartment() : "General");
        data.put("specialization", doctor.getSpecialization() != null ? doctor.getSpecialization() : "General Physician");
        data.put("qualification", doctor.getQualification() != null ? doctor.getQualification() : "MBBS, MD");
        data.put("hospitalName", doctor.getHospitalName() != null ? doctor.getHospitalName() : "City Care Hospital");
        data.put("location", doctor.getLocation() != null ? doctor.getLocation() : "Bengaluru");
        data.put("experienceGender", doctor.getExperienceGender() != null ? doctor.getExperienceGender() : "10 yrs");
        data.put("contactNumber", doctor.getContactNumber() != null ? doctor.getContactNumber() : "+91 9876500001");
        data.put("email", doctor.getEmail() != null ? doctor.getEmail() : "doctor@hospital.com");
        data.put("feeCapacity", doctor.getFeeCapacity() != null ? doctor.getFeeCapacity() : "10/day");
        data.put("timeSlots", doctor.getTimeSlots() != null ? doctor.getTimeSlots() : "10:00 AM - 02:00 PM");
        data.put("aboutSummary", doctor.getAboutSummary() != null ? doctor.getAboutSummary() : "Experienced medical specialist.");
        data.put("imageResName", doctor.getImageResName() != null ? doctor.getImageResName() : "doc_male_1");
        data.put("leaveStartDate", doctor.getLeaveStartDate() != null ? doctor.getLeaveStartDate() : "");
        data.put("leaveEndDate", doctor.getLeaveEndDate() != null ? doctor.getLeaveEndDate() : "");
        data.put("leaveReason", doctor.getLeaveReason() != null ? doctor.getLeaveReason() : "");
        data.put("unavailable", doctor.isUnavailable());

        db.collection("doctors").document(id)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Doctor saved to Firestore: " + id))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving doctor to Firestore", e));
    }

    private void deleteDoctorFromDatabase(DoctorModel doctor) {
        if (doctor == null || doctor.getId() == null) return;
        db.collection("doctors").document(doctor.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Doctor deleted from Firestore: " + doctor.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting doctor from Firestore", e));
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
