package com.example.healthcare.admin.more_section;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
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
import com.example.healthcare.admin.appts_section.AppointmentsActivity;
import com.example.healthcare.admin.more_section.MoreMenuDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity in 'more_section' folder for managing Hospital Departments.
 * Connected directly to Firebase Cloud Firestore 'departments' collection for department validation.
 */
public class DepartmentsActivity extends AppCompatActivity implements DepartmentAdapter.OnDepartmentActionListener {

    private static final String TAG = "DepartmentsActivity";

    private TextView tvTotalDepartmentsCount;
    private TextView tvTotalDoctorsCount;
    private RecyclerView rvDepartments;
    private MaterialButton btnAddDepartment;
    private BottomNavigationView bottomNavigationView;

    private DepartmentAdapter departmentAdapter;
    private final List<DepartmentModel> departmentList = new ArrayList<>();

    private FirebaseFirestore db;
    private ListenerRegistration deptListener;
    private ListenerRegistration doctorCountListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departments);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupSeamlessBottomNavigation();
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation();

        loadLocalDefaultDepartments();
        setupFirestoreRealtimeListener();
        setupDoctorCountListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSeamlessBottomNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deptListener != null) {
            deptListener.remove();
        }
        if (doctorCountListener != null) {
            doctorCountListener.remove();
        }
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvTotalDepartmentsCount = findViewById(R.id.tvTotalDepartmentsCount);
        tvTotalDoctorsCount = findViewById(R.id.tvTotalDoctorsCount);
        rvDepartments = findViewById(R.id.rvDepartments);
        btnAddDepartment = findViewById(R.id.btnAddDepartment);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        rvDepartments.setLayoutManager(new LinearLayoutManager(this));
        departmentAdapter = new DepartmentAdapter(this, departmentList, this);
        rvDepartments.setAdapter(departmentAdapter);
    }

    private void setupFirestoreRealtimeListener() {
        deptListener = db.collection("departments")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Firestore departments listen failed: ", error);
                        runOnUiThread(this::loadLocalDefaultDepartments);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        departmentList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            try {
                                String id = doc.getId();
                                String name = doc.getString("name");
                                String floor = doc.getString("floor");
                                String head = doc.getString("headDoctor");
                                String status = doc.getString("status");

                                if (name == null || name.trim().isEmpty()) continue;

                                DepartmentModel model = new DepartmentModel(
                                        id,
                                        name,
                                        floor != null ? floor : "Floor 1",
                                        head != null ? head : "Chief Medical Officer",
                                        status != null ? status : "Active"
                                );
                                departmentList.add(model);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing department snapshot", e);
                            }
                        }
                        updateDepartmentCountUI();
                    } else {
                        seedRealDepartmentsToFirestore();
                    }
                });
    }

    private void setupDoctorCountListener() {
        doctorCountListener = db.collection("doctors")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    int docCount = value.size();
                    runOnUiThread(() -> {
                        if (tvTotalDoctorsCount != null) {
                            tvTotalDoctorsCount.setText(String.valueOf(docCount));
                        }
                    });
                });
    }

    private void loadLocalDefaultDepartments() {
        if (departmentList.isEmpty()) {
            departmentList.add(new DepartmentModel("DEPT-1", "Cardiology", "Floor 1", "Dr. Sarojini Rao", "Active"));
            departmentList.add(new DepartmentModel("DEPT-2", "Orthopedics", "Floor 2", "Dr. K. V. Mehta", "Active"));
            departmentList.add(new DepartmentModel("DEPT-3", "Dermatology", "Floor 3", "Dr. Sunita Iyer", "Active"));
            departmentList.add(new DepartmentModel("DEPT-4", "Neurology", "Floor 4", "Dr. Rajesh Sharma", "Active"));
            departmentList.add(new DepartmentModel("DEPT-5", "Pediatrics", "Floor 5", "Dr. Sneha Iyer", "Active"));
            departmentList.add(new DepartmentModel("DEPT-6", "General Medicine", "Ground Floor", "Dr. Rahul Sharma", "Active"));
        }
        updateDepartmentCountUI();
    }

    private void seedRealDepartmentsToFirestore() {
        loadLocalDefaultDepartments();

        for (DepartmentModel department : departmentList) {
            saveDepartmentToDatabase(department);
        }
    }

    private void setupClickListeners() {
        if (btnAddDepartment != null) {
            btnAddDepartment.setOnClickListener(v -> {
                AddEditDepartmentDialog dialog = AddEditDepartmentDialog.newInstance(null);
                dialog.setOnDepartmentSavedListener(newDept -> {
                    saveDepartmentToDatabase(newDept);
                    Toast.makeText(this, "✓ " + newDept.getName() + " department created", Toast.LENGTH_SHORT).show();
                });
                dialog.show(getSupportFragmentManager(), "AddDepartmentDialog");
            });
        }
    }

    @Override
    public void onEdit(DepartmentModel department) {
        AddEditDepartmentDialog dialog = AddEditDepartmentDialog.newInstance(department);
        dialog.setOnDepartmentSavedListener(updatedDept -> {
            saveDepartmentToDatabase(updatedDept);
            Toast.makeText(this, "✓ " + updatedDept.getName() + " updated", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "EditDepartmentDialog");
    }

    @Override
    public void onDelete(DepartmentModel department) {
        if (department == null) return;

        deleteDepartmentFromDatabase(department);
        Toast.makeText(this, department.getName() + " deleted from database", Toast.LENGTH_SHORT).show();
    }

    private void updateDepartmentCountUI() {
        runOnUiThread(() -> {
            if (tvTotalDepartmentsCount != null) {
                tvTotalDepartmentsCount.setText(String.valueOf(departmentList.size()));
            }
            if (departmentAdapter != null) {
                departmentAdapter.updateList(departmentList);
            }
        });
    }

    private void saveDepartmentToDatabase(DepartmentModel department) {
        if (department == null) return;
        String id = (department.getId() != null && !department.getId().isEmpty())
                ? department.getId() : "DEPT-" + System.currentTimeMillis();
        department.setId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("id", department.getId());
        data.put("name", department.getName());
        data.put("floor", department.getFloor());
        data.put("headDoctor", department.getHeadDoctor());
        data.put("status", department.getStatus());
        data.put("updatedAt", System.currentTimeMillis());

        db.collection("departments").document(id)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Department saved to Firestore: " + id))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving department to Firestore", e));
    }

    private void deleteDepartmentFromDatabase(DepartmentModel department) {
        if (department == null || department.getId() == null) return;
        db.collection("departments").document(department.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Department deleted from Firestore: " + department.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting department from Firestore", e));
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
