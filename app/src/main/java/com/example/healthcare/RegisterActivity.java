package com.example.healthcare;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String[] GENDERS = {"Female", "Male", "Other"};
    private static final String[] BLOOD_GROUPS = {"O+", "A+", "A-", "B+", "B-", "AB+", "AB-", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        HeaderHelper.bind(this, findViewById(android.R.id.content), "Patient Registration", null, true, false);

        final EditText etName = findViewById(R.id.etName);
        final TextView tvDob = findViewById(R.id.tvDob);
        final TextView tvGender = findViewById(R.id.tvGender);
        final EditText etMobile = findViewById(R.id.etMobile);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etPassword = findViewById(R.id.etPassword);
        final TextView tvBlood = findViewById(R.id.tvBlood);

        tvDob.setOnClickListener(v -> DialogHelper.pickDate(this, tvDob, false));
        tvGender.setOnClickListener(v -> pick("Gender", GENDERS, tvGender));
        tvBlood.setOnClickListener(v -> pick("Blood Group", BLOOD_GROUPS, tvBlood));

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String name = (etName != null && etName.getText() != null) ? etName.getText().toString().trim() : "Asha Rao";
            String email = (etEmail != null && etEmail.getText() != null) ? etEmail.getText().toString().trim() : "asha.rao@gmail.com";
            String mobile = (etMobile != null && etMobile.getText() != null) ? etMobile.getText().toString().trim() : "+91 9876543210";
            String gender = (tvGender != null && tvGender.getText() != null && !tvGender.getText().toString().isEmpty()) ? tvGender.getText().toString() : "Female";
            String blood = (tvBlood != null && tvBlood.getText() != null && !tvBlood.getText().toString().isEmpty()) ? tvBlood.getText().toString() : "O+";

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                return;
            }

            int randomNum = 1000 + new Random().nextInt(8999);
            String patientId = "#" + randomNum;

            SharedPreferences prefs = getSharedPreferences("healthcare_patient", Context.MODE_PRIVATE);
            prefs.edit()
                    .putString("patient_name", name)
                    .putString("patient_email", email)
                    .putString("patient_phone", mobile)
                    .putString("patient_gender", gender)
                    .putString("patient_blood", blood)
                    .putString("patient_id", patientId)
                    .apply();

            // Save Patient to Firebase Cloud Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> patientData = new HashMap<>();
            patientData.put("name", name);
            patientData.put("email", email);
            patientData.put("phone", mobile);
            patientData.put("gender", gender);
            patientData.put("bloodGroup", blood);
            patientData.put("patientId", patientId);
            patientData.put("registeredAt", System.currentTimeMillis());

            db.collection("users").document("PAT-" + randomNum)
                    .set(patientData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Patient profile created in Firestore: PAT-" + randomNum))
                    .addOnFailureListener(e -> Log.e(TAG, "Error saving patient to Firestore", e));

            Dialog d = DialogHelper.showMessage(this, R.drawable.ic_ring_check, R.color.primary,
                    "Registration Successful",
                    "Welcome " + name + "! Your account has been created successfully (Patient ID: " + patientId + ").",
                    null, "Continue to Dashboard", false, this::finish, null);
            d.setCancelable(false);
        });
    }

    private void pick(String title, final String[] items, final TextView target) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(items, (dialog, which) -> target.setText(items[which]))
                .show();
    }
}
