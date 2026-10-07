package com.example.healthcare.admin.doctors_section;

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
 * DialogFragment in 'doctors_section' folder showing full Doctor Profile & Details.
 */
public class DoctorDetailsPopupDialog extends DialogFragment {

    private static final String ARG_DOCTOR = "arg_doctor";
    private DoctorModel doctor;

    public static DoctorDetailsPopupDialog newInstance(DoctorModel doctor) {
        DoctorDetailsPopupDialog dialog = new DoctorDetailsPopupDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DOCTOR, doctor);
        dialog.setArguments(args);
        return dialog;
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
        return inflater.inflate(R.layout.dialog_doctor_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        ImageView imgPhoto = view.findViewById(R.id.imgPopupDocPhoto);
        TextView tvName = view.findViewById(R.id.tvPopupDocName);
        TextView tvDeptSpec = view.findViewById(R.id.tvPopupDocDeptSpec);
        TextView tvQual = view.findViewById(R.id.tvPopupDocQual);
        TextView tvExpGender = view.findViewById(R.id.tvPopupDocExpGender);
        TextView tvContact = view.findViewById(R.id.tvPopupDocContact);
        TextView tvCapacity = view.findViewById(R.id.tvPopupDocCapacity);
        TextView tvSlots = view.findViewById(R.id.tvPopupDocSlots);
        TextView tvAbout = view.findViewById(R.id.tvPopupDocAbout);

        MaterialButton btnCloseBottom = view.findViewById(R.id.btnCloseDocPopup);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCloseBottom != null) btnCloseBottom.setOnClickListener(v -> dismiss());

        if (doctor != null) {
            if (tvName != null) tvName.setText(doctor.getName());
            if (tvDeptSpec != null) {
                String spec = (doctor.getSpecialization() != null && !doctor.getSpecialization().isEmpty())
                        ? doctor.getSpecialization() : "General Medicine";
                tvDeptSpec.setText(doctor.getDepartment() + " · " + spec);
            }
            if (tvQual != null) {
                tvQual.setText("Qualification: " + (doctor.getQualification() != null ? doctor.getQualification() : "MD / MBBS"));
            }
            if (tvExpGender != null) {
                tvExpGender.setText("Experience & Gender: " + (doctor.getExperienceGender() != null ? doctor.getExperienceGender() : "10 yrs · Male"));
            }
            if (tvContact != null) {
                tvContact.setText("Contact Number: " + (doctor.getContactNumber() != null ? doctor.getContactNumber() : "+91 9876500001"));
            }
            if (tvCapacity != null) {
                tvCapacity.setText("Consultation Capacity / Day: " + (doctor.getFeeCapacity() != null ? doctor.getFeeCapacity() : "10/day"));
            }
            if (tvSlots != null) {
                tvSlots.setText("Time Slots: " + (doctor.getTimeSlots() != null ? doctor.getTimeSlots() : "09:00 AM - 01:00 PM"));
            }
            if (tvAbout != null) {
                tvAbout.setText("About: " + (doctor.getAboutSummary() != null ? doctor.getAboutSummary() : "Experienced medical specialist committed to providing high-quality patient care."));
            }

            if (imgPhoto != null && doctor.getImageResName() != null) {
                int resId = getContext().getResources().getIdentifier(doctor.getImageResName(), "drawable", getContext().getPackageName());
                if (resId != 0) {
                    imgPhoto.setImageResource(resId);
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
