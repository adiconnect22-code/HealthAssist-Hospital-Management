package com.example.healthcare.appts_section;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.google.android.material.button.MaterialButton;

/**
 * DialogFragment in 'appts_section' folder.
 */
public class AppointmentDetailsDialog extends DialogFragment {

    private static final String ARG_APPOINTMENT = "arg_appointment";
    private AppointmentModel appointment;

    public static AppointmentDetailsDialog newInstance(AppointmentModel appointment) {
        AppointmentDetailsDialog fragment = new AppointmentDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_APPOINTMENT, appointment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointment = (AppointmentModel) getArguments().getSerializable(ARG_APPOINTMENT);
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
        return inflater.inflate(R.layout.dialog_appointment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        TextView tvPatientName = view.findViewById(R.id.tvDetailPatientName);
        TextView tvDoctorName = view.findViewById(R.id.tvDetailDoctorName);
        TextView tvDepartment = view.findViewById(R.id.tvDetailDepartment);
        TextView tvTime = view.findViewById(R.id.tvDetailTime);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);

        MaterialButton btnCancel = view.findViewById(R.id.btnCancelAppt);
        MaterialButton btnCloseBottom = view.findViewById(R.id.btnCloseDialog);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCloseBottom != null) btnCloseBottom.setOnClickListener(v -> dismiss());

        if (appointment != null) {
            if (tvPatientName != null) tvPatientName.setText(appointment.getPatientName());
            if (tvDoctorName != null) tvDoctorName.setText(appointment.getDoctorName());
            if (tvDepartment != null) tvDepartment.setText(appointment.getDepartment() != null ? appointment.getDepartment() : "General");
            if (tvTime != null) tvTime.setText(appointment.getDate() != null ? appointment.getDate() : "Today");
            if (tvStatus != null) tvStatus.setText(appointment.getStatus() != null ? appointment.getStatus() : "Confirmed");
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Appointment Cancelled", Toast.LENGTH_SHORT).show();
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
