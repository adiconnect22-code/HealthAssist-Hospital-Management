package com.example.healthcare.admin;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.DialogHelper;
import com.example.healthcare.LoginActivity;
import com.example.healthcare.R;
import com.example.healthcare.admin.admin_section.AdminDashboardActivity;

public class AdminLoginActivity extends AppCompatActivity {

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        final EditText etUser = findViewById(R.id.etAdminUser);
        final EditText etPassword = findViewById(R.id.etAdminPassword);

        findViewById(R.id.btnEye).setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            int type = InputType.TYPE_CLASS_TEXT | (passwordVisible
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            if (etPassword != null) {
                etPassword.setInputType(type);
                etPassword.setTypeface(Typeface.DEFAULT);
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        findViewById(R.id.tvForgot).setOnClickListener(v -> showResetDialog());

        findViewById(R.id.tvPatientLogin).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
            prefs.edit().putBoolean("is_user_role", true).apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String username = (etUser != null && etUser.getText() != null) ? etUser.getText().toString().trim() : "";
            String password = (etPassword != null && etPassword.getText() != null) ? etPassword.getText().toString().trim() : "";

            boolean userValid = username.equalsIgnoreCase("Admin") || username.equalsIgnoreCase("admin") || username.equalsIgnoreCase("admin@hospital.com");
            boolean passValid = password.equals("Admin123");

            if (userValid && passValid) {
                SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
                prefs.edit().putBoolean("is_user_role", false).apply();
                Toast.makeText(this, "✓ Admin Authenticated Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "✕ Invalid Admin Credentials! Username: Admin, Password: Admin123", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showResetDialog() {
        final Dialog d = DialogHelper.create(this, R.layout.dialog_reset_password);
        d.findViewById(R.id.rpClose).setOnClickListener(v -> d.dismiss());
        d.findViewById(R.id.rpCancel).setOnClickListener(v -> d.dismiss());
        d.findViewById(R.id.rpSend).setOnClickListener(v ->
                Toast.makeText(this, "Admin verification code sent", Toast.LENGTH_SHORT).show());
        d.findViewById(R.id.rpReset).setOnClickListener(v -> {
            Toast.makeText(this, "Admin password reset to Admin123", Toast.LENGTH_SHORT).show();
            d.dismiss();
        });
        d.show();
    }
}
