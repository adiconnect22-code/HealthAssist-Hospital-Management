package com.example.healthcare;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        HeaderHelper.bind(this, findViewById(android.R.id.content), "Patient Login", null, false, false);

        final EditText etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnEye).setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            int type = InputType.TYPE_CLASS_TEXT | (passwordVisible
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setInputType(type);
            etPassword.setTypeface(Typeface.DEFAULT);
            etPassword.setSelection(etPassword.getText().length());
        });

        findViewById(R.id.tvForgot).setOnClickListener(v -> showResetDialog());

        findViewById(R.id.tvCreate).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // Patient Login: set is_user_role = true and navigate to Patient MainActivity
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("healthcare_settings", MODE_PRIVATE);
            prefs.edit().putBoolean("is_user_role", true).apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void showResetDialog() {
        final Dialog d = DialogHelper.create(this, R.layout.dialog_reset_password);
        d.findViewById(R.id.rpClose).setOnClickListener(v -> d.dismiss());
        d.findViewById(R.id.rpCancel).setOnClickListener(v -> d.dismiss());
        d.findViewById(R.id.rpSend).setOnClickListener(v ->
                Toast.makeText(this, "Verification code sent (demo)", Toast.LENGTH_SHORT).show());
        d.findViewById(R.id.rpReset).setOnClickListener(v -> {
            Toast.makeText(this, "Password reset (demo)", Toast.LENGTH_SHORT).show();
            d.dismiss();
        });
        d.show();
    }
}
