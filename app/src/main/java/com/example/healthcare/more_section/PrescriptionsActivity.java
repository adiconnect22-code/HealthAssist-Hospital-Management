package com.example.healthcare.more_section;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
public class PrescriptionsActivity extends AppCompatActivity implements PrescriptionAdapter.OnPrescriptionClickListener {

    private TextView tvTotalPrescriptionsCount;
    private TextView tvAddedThisWeekCount;
    private RecyclerView rvPrescriptions;
    private MaterialButton btnAddPrescription;
    private BottomNavigationView bottomNavigationView;

    private PrescriptionAdapter prescriptionAdapter;
    private final List<PrescriptionModel> prescriptionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        initViews();
        setupSeamlessBottomNavigation();
        setupRecyclerView();
        loadSamplePrescriptions();
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

        tvTotalPrescriptionsCount = findViewById(R.id.tvTotalPrescriptionsCount);
        tvAddedThisWeekCount = findViewById(R.id.tvAddedThisWeekCount);
        rvPrescriptions = findViewById(R.id.rvPrescriptions);
        btnAddPrescription = findViewById(R.id.btnAddPrescription);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        rvPrescriptions.setLayoutManager(new LinearLayoutManager(this));
        prescriptionAdapter = new PrescriptionAdapter(this, prescriptionList, this);
        rvPrescriptions.setAdapter(prescriptionAdapter);
    }

    private void loadSamplePrescriptions() {
        prescriptionList.clear();
        prescriptionList.add(new PrescriptionModel("RX-1", "Amit Sharma", "Dr. Mehta", "28 Aug", "Hypertension", "Amoxicillin, Paracetamol", "1 tab after meals", "05 Sep", ""));
        prescriptionList.add(new PrescriptionModel("RX-2", "Sneha Patil", "Dr. Iyer", "28 Aug", "Acute Allergy", "Cetirizine 10mg", "Once daily at night", "02 Sep", ""));
        prescriptionList.add(new PrescriptionModel("RX-3", "Rahul Verma", "Dr. Sharma", "25 Aug", "Gastroenteritis", "ORS, Dicyclomine", "As needed for pain", "01 Sep", ""));
        prescriptionList.add(new PrescriptionModel("RX-4", "Priya Das", "Dr. Arjun Patel", "20 Aug", "Arrhythmia Check", "Aspirin 75mg", "1 tab daily in morning", "10 Sep", ""));

        updateCountUI();
        if (prescriptionAdapter != null) {
            prescriptionAdapter.updateList(prescriptionList);
        }

        fetchPrescriptionsFromDatabase();
    }

    private void setupClickListeners() {
        if (btnAddPrescription != null) {
            btnAddPrescription.setOnClickListener(v -> {
                AddPrescriptionDialog dialog = AddPrescriptionDialog.newInstance(null);
                dialog.setOnPrescriptionSavedListener(newRx -> {
                    prescriptionList.add(0, newRx);
                    prescriptionAdapter.updateList(prescriptionList);
                    updateCountUI();
                    savePrescriptionToDatabase(newRx);
                });
                dialog.show(getSupportFragmentManager(), "AddPrescriptionDialog");
            });
        }
    }

    @Override
    public void onPrescriptionClick(PrescriptionModel prescription) {
        AddPrescriptionDialog dialog = AddPrescriptionDialog.newInstance(prescription);
        dialog.setOnPrescriptionSavedListener(updatedRx -> {
            for (int i = 0; i < prescriptionList.size(); i++) {
                if (prescriptionList.get(i).getId().equals(updatedRx.getId())) {
                    prescriptionList.set(i, updatedRx);
                    break;
                }
            }
            prescriptionAdapter.updateList(prescriptionList);
            updateCountUI();
            savePrescriptionToDatabase(updatedRx);
        });
        dialog.show(getSupportFragmentManager(), "EditPrescriptionDialog");
    }

    private void updateCountUI() {
        if (tvTotalPrescriptionsCount != null) {
            tvTotalPrescriptionsCount.setText(String.valueOf(prescriptionList.size() + 64));
        }
        if (tvAddedThisWeekCount != null) {
            tvAddedThisWeekCount.setText(String.valueOf(prescriptionList.size() + 2));
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

    private void fetchPrescriptionsFromDatabase() {
        // TODO: Database connection logic
    }

    private void savePrescriptionToDatabase(PrescriptionModel prescription) {
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
