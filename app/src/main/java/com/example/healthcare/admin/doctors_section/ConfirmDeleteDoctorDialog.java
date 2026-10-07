package com.example.healthcare.admin.doctors_section;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.google.android.material.button.MaterialButton;

/**
 * DialogFragment for confirming doctor deletion with YES on Left and NO on Right.
 */
public class ConfirmDeleteDoctorDialog extends DialogFragment {

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed(DoctorModel doctor);
    }

    private static final String ARG_DOCTOR = "arg_doctor";
    private DoctorModel doctor;
    private OnDeleteConfirmedListener listener;

    public static ConfirmDeleteDoctorDialog newInstance(DoctorModel doctor) {
        ConfirmDeleteDoctorDialog dialog = new ConfirmDeleteDoctorDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DOCTOR, doctor);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnDeleteConfirmedListener(OnDeleteConfirmedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctor = (DoctorModel) getArguments().getSerializable(ARG_DOCTOR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_confirm_delete_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvMsg = view.findViewById(R.id.tvDeleteMessage);
        MaterialButton btnYes = view.findViewById(R.id.btnConfirmYes);
        MaterialButton btnNo = view.findViewById(R.id.btnConfirmNo);

        if (doctor != null && tvMsg != null) {
            tvMsg.setText("Are you sure you want to delete " + doctor.getName() + " from the hospital database?");
        }

        if (btnNo != null) btnNo.setOnClickListener(v -> dismiss());

        if (btnYes != null) {
            btnYes.setOnClickListener(v -> {
                if (listener != null && doctor != null) {
                    listener.onDeleteConfirmed(doctor);
                }
                dismiss();
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
