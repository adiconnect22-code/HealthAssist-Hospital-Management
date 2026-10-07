package com.example.healthcare.admin.more_section;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;

/**
 * DialogFragment inside 'more_section' folder.
 */
public class MoreMenuDialog extends DialogFragment {

    public static MoreMenuDialog newInstance() {
        return new MoreMenuDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_more_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnMoreClose);
        View cardManageDepartments = view.findViewById(R.id.cardManageDepartments);
        View cardReportsAnalytics = view.findViewById(R.id.cardReportsAnalytics);
        View cardAuditLog = view.findViewById(R.id.cardAuditLog);
        View cardPrescriptions = view.findViewById(R.id.cardPrescriptions);
        View cardSettingsProfile = view.findViewById(R.id.cardSettingsProfile);
        View cardLogout = view.findViewById(R.id.cardLogout);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());

        if (cardManageDepartments != null) {
            cardManageDepartments.setOnClickListener(v -> {
                if (getContext() != null) {
                    startActivity(new Intent(getContext(), DepartmentsActivity.class));
                }
                dismiss();
            });
        }

        if (cardReportsAnalytics != null) {
            cardReportsAnalytics.setOnClickListener(v -> {
                if (getContext() != null) {
                    startActivity(new Intent(getContext(), ReportsAnalyticsActivity.class));
                }
                dismiss();
            });
        }

        if (cardAuditLog != null) {
            cardAuditLog.setOnClickListener(v -> {
                if (getContext() != null) {
                    startActivity(new Intent(getContext(), AuditLogActivity.class));
                }
                dismiss();
            });
        }

        if (cardPrescriptions != null) {
            cardPrescriptions.setOnClickListener(v -> {
                if (getContext() != null) {
                    startActivity(new Intent(getContext(), PrescriptionsActivity.class));
                }
                dismiss();
            });
        }

        if (cardSettingsProfile != null) {
            cardSettingsProfile.setOnClickListener(v -> {
                if (getContext() != null) {
                    startActivity(new Intent(getContext(), SettingsProfileActivity.class));
                }
                dismiss();
            });
        }

        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                dismiss();
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), com.example.healthcare.admin.AdminLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
