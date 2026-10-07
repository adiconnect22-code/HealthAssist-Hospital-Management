package com.example.healthcare;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final String[] GENDERS = {"Male", "Female", "Other"};
    private static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        HeaderHelper.bind(this, findViewById(android.R.id.content), "Patient Registration", null, true, false);

        final TextView tvDob = findViewById(R.id.tvDob);
        final TextView tvGender = findViewById(R.id.tvGender);
        final TextView tvBlood = findViewById(R.id.tvBlood);

        tvDob.setOnClickListener(v -> DialogHelper.pickDate(this, tvDob, false));
        tvGender.setOnClickListener(v -> pick("Gender", GENDERS, tvGender));
        tvBlood.setOnClickListener(v -> pick("Blood Group", BLOOD_GROUPS, tvBlood));

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            Dialog d = DialogHelper.showMessage(this, R.drawable.ic_ring_check, R.color.primary,
                    "Registration Successful",
                    "Your account has been created. You can now log in to book and manage appointments.",
                    null, "Continue to Login", false, this::finish, null);
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
