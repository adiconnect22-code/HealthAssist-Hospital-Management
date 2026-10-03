package com.example.healthcare.more_section;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.healthcare.admin_section.AdminDashboardActivity;
import com.example.healthcare.appts_section.AppointmentsActivity;
import com.example.healthcare.doctors_section.DoctorsListDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity in 'more_section' folder.
 */
public class DepartmentsActivity extends AppCompatActivity implements DepartmentAdapter.OnDepartmentActionListener {

    private TextView tvTotalDepartmentsCount;
    private TextView tvTotalDoctorsCount;
    private RecyclerView rvDepartments;
    private MaterialButton btnAddDepartment;
    private BottomNavigationView bottomNavigationView;

    private DepartmentAdapter departmentAdapter;
    private final List<DepartmentModel> departmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departments);

        initViews();
        setupSeamlessBottomNavigation();
        setupRecyclerView();
        loadSampleDepartments();
        setupClickListeners();
        setupBottomNavigation();
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

    private void loadSampleDepartments() {
        departmentList.clear();
        departmentList.add(new DepartmentModel("DEPT-1", "Cardiology", "Floor 1", "Dr. Sarah Smith", "Active"));
        departmentList.add(new DepartmentModel("DEPT-2", "Orthopedics", "Floor 2", "Dr. David Lee", "Active"));
        departmentList.add(new DepartmentModel("DEPT-3", "Dermatology", "Floor 3", "Dr. Emily Davis", "Active"));
        departmentList.add(new DepartmentModel("DEPT-4", "Neurology", "Floor 4", "Dr. Robert Johnson", "Active"));

        updateDepartmentCountUI();
        if (departmentAdapter != null) {
            departmentAdapter.updateList(departmentList);
        }

        fetchDepartmentsFromDatabase();
    }

    private void setupClickListeners() {
        if (btnAddDepartment != null) {
            btnAddDepartment.setOnClickListener(v -> {
                AddEditDepartmentDialog dialog = AddEditDepartmentDialog.newInstance(null);
                dialog.setOnDepartmentSavedListener(newDept -> {
                    departmentList.add(newDept);
                    departmentAdapter.updateList(departmentList);
                    updateDepartmentCountUI();
                    saveDepartmentToDatabase(newDept);
                });
                dialog.show(getSupportFragmentManager(), "AddDepartmentDialog");
            });
        }
    }

    @Override
    public void onEdit(DepartmentModel department) {
        AddEditDepartmentDialog dialog = AddEditDepartmentDialog.newInstance(department);
        dialog.setOnDepartmentSavedListener(updatedDept -> {
            for (int i = 0; i < departmentList.size(); i++) {
                if (departmentList.get(i).getId().equals(updatedDept.getId())) {
                    departmentList.set(i, updatedDept);
                    break;
                }
            }
            departmentAdapter.updateList(departmentList);
            updateDepartmentCountUI();
            saveDepartmentToDatabase(updatedDept);
        });
        dialog.show(getSupportFragmentManager(), "EditDepartmentDialog");
    }

    @Override
    public void onDelete(DepartmentModel department) {
        if (department == null) return;

        departmentList.remove(department);
        departmentAdapter.updateList(departmentList);
        updateDepartmentCountUI();

        deleteDepartmentFromDatabase(department);
        Toast.makeText(this, department.getName() + " deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateDepartmentCountUI() {
        if (tvTotalDepartmentsCount != null) {
            tvTotalDepartmentsCount.setText(String.valueOf(departmentList.size()));
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

    private void fetchDepartmentsFromDatabase() {
        // TODO: Database connection logic
    }

    private void saveDepartmentToDatabase(DepartmentModel department) {
        // TODO: Database connection logic
    }

    private void deleteDepartmentFromDatabase(DepartmentModel department) {
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
