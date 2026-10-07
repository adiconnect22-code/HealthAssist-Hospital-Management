package com.example.healthcare.admin.more_section;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.google.android.material.button.MaterialButton;

/**
 * DialogFragment in 'more_section' folder.
 */
public class AdminDetailsDialog extends DialogFragment {

    public static AdminDetailsDialog newInstance() {
        return new AdminDetailsDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_admin_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        MaterialButton btnCloseBottom = view.findViewById(R.id.btnCloseAdminDetails);

        TextView tvAdminName = view.findViewById(R.id.tvAdminName);
        TextView tvAdminAgeGender = view.findViewById(R.id.tvAdminAgeGender);
        TextView tvAdminQualification = view.findViewById(R.id.tvAdminQualification);
        TextView tvAdminExperience = view.findViewById(R.id.tvAdminExperience);
        TextView tvAdminContact = view.findViewById(R.id.tvAdminContact);
        TextView tvAdminEmail = view.findViewById(R.id.tvAdminEmail);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCloseBottom != null) btnCloseBottom.setOnClickListener(v -> dismiss());

        fetchAdminDetailsFromDatabase();
    }

    private void fetchAdminDetailsFromDatabase() {
        // TODO: Database connection logic
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
