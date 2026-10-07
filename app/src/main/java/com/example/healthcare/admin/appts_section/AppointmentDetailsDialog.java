package com.example.healthcare.admin.appts_section;

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
import com.example.healthcare.admin.more_section.AddPrescriptionDialog;
import com.example.healthcare.admin.more_section.PrescriptionModel;
import com.google.android.material.button.MaterialButton;

/**
 * DialogFragment in 'appts_section' folder showing full Appointment details.
 */
public class AppointmentDetailsDialog extends DialogFragment {

    public interface OnAppointmentStatusChangeListener {
        void onCancelAppointment(AppointmentModel appointment);
    }

    private static final String ARG_APPOINTMENT = "arg_appointment";
    private AppointmentModel appointment;
    private OnAppointmentStatusChangeListener statusChangeListener;

    public static AppointmentDetailsDialog newInstance(AppointmentModel appointment) {
        AppointmentDetailsDialog fragment = new AppointmentDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_APPOINTMENT, appointment);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAppointmentStatusChangeListener(OnAppointmentStatusChangeListener listener) {
        this.statusChangeListener = listener;
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
        TextView tvReason = view.findViewById(R.id.tvDetailReason);
        TextView tvTime = view.findViewById(R.id.tvDetailTime);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);

        MaterialButton btnCancel = view.findViewById(R.id.btnCancelAppt);
        MaterialButton btnAddPrescription = view.findViewById(R.id.btnAddPrescription);
        MaterialButton btnCloseBottom = view.findViewById(R.id.btnCloseDialog);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCloseBottom != null) btnCloseBottom.setOnClickListener(v -> dismiss());

        if (appointment != null) {
            if (tvPatientName != null) tvPatientName.setText(appointment.getPatientName());
            if (tvDoctorName != null) tvDoctorName.setText(appointment.getDoctorName());
            if (tvDepartment != null) tvDepartment.setText(appointment.getDepartment());
            if (tvReason != null) tvReason.setText(appointment.getReason());

            String timeStr = appointment.getDate() + " (" + appointment.getTimeSlot() + ")";
            if (tvTime != null) tvTime.setText(timeStr);

            String status = appointment.getStatus();
            if (tvStatus != null) tvStatus.setText(status);

            if (status.equalsIgnoreCase("Completed")) {
                if (btnCancel != null) btnCancel.setVisibility(View.GONE);
                if (btnAddPrescription != null) {
                    btnAddPrescription.setVisibility(View.VISIBLE);
                    btnAddPrescription.setOnClickListener(v -> {
                        PrescriptionModel sampleRx = new PrescriptionModel(
                                "RX-" + System.currentTimeMillis(),
                                appointment.getPatientName(),
                                appointment.getDoctorName(),
                                "Today",
                                "General Medical Followup",
                                "Paracetamol 500mg, Multivitamin",
                                "Take 1 tablet after meals for 5 days.",
                                "In 1 week",
                                ""
                        );
                        AddPrescriptionDialog rxDialog = AddPrescriptionDialog.newInstance(sampleRx);
                        rxDialog.show(getParentFragmentManager(), "AddPrescriptionDialog");
                        dismiss();
                    });
                }
            } else {
                if (btnAddPrescription != null) btnAddPrescription.setVisibility(View.GONE);
                if (btnCancel != null) {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setOnClickListener(v -> {
                        if (statusChangeListener != null) {
                            statusChangeListener.onCancelAppointment(appointment);
                        } else {
                            Toast.makeText(getContext(), "Appointment Cancelled", Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                    });
                }
            }
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
