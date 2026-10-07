package com.example.healthcare.admin.doctors_section;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.google.android.material.button.MaterialButton;

/**
 * DialogFragment in 'doctors_section' folder for Add/Edit Doctor.
 */
public class AddEditDoctorDialog extends DialogFragment {

    private static final String ARG_DOCTOR = "arg_doctor";

    public interface OnDoctorSavedListener {
        void onDoctorSaved(DoctorModel doctor);
    }

    private DoctorModel doctorToEdit;
    private OnDoctorSavedListener savedListener;

    private final String[] AVATAR_RES = {"doc_female_1", "doc_male_1", "doc_female_2", "doc_male_2"};
    private int selectedAvatarIndex = 0;

    public static AddEditDoctorDialog newInstance(@Nullable DoctorModel doctor) {
        AddEditDoctorDialog dialog = new AddEditDoctorDialog();
        if (doctor != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_DOCTOR, doctor);
            dialog.setArguments(args);
        }
        return dialog;
    }

    public void setOnDoctorSavedListener(OnDoctorSavedListener listener) {
        this.savedListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctorToEdit = (DoctorModel) getArguments().getSerializable(ARG_DOCTOR);
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
        return inflater.inflate(R.layout.dialog_add_edit_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        LinearLayout layoutPickImage = view.findViewById(R.id.layoutPickDocImage);
        ImageView imgSelectedPhoto = view.findViewById(R.id.imgSelectedDocPhoto);
        TextView tvSelectedPhotoName = view.findViewById(R.id.tvSelectedPhotoName);

        EditText etName = view.findViewById(R.id.etDoctorName);
        EditText etDept = view.findViewById(R.id.etDoctorDepartment);
        EditText etSpec = view.findViewById(R.id.etDoctorSpecialization);
        EditText etQual = view.findViewById(R.id.etDoctorQualification);
        EditText etExpGender = view.findViewById(R.id.etExperienceGender);
        EditText etContact = view.findViewById(R.id.etContactNumber);
        EditText etFeeCapacity = view.findViewById(R.id.etFeeCapacity);
        EditText etTimeSlots = view.findViewById(R.id.etTimeSlots);

        MaterialButton btnSave = view.findViewById(R.id.btnSaveDoctor);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelDoctor);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> dismiss());

        if (layoutPickImage != null) {
            layoutPickImage.setOnClickListener(v -> {
                selectedAvatarIndex = (selectedAvatarIndex + 1) % AVATAR_RES.length;
                String resName = AVATAR_RES[selectedAvatarIndex];
                int resId = getContext().getResources().getIdentifier(resName, "drawable", getContext().getPackageName());
                if (resId != 0 && imgSelectedPhoto != null) {
                    imgSelectedPhoto.setImageResource(resId);
                }
                if (tvSelectedPhotoName != null) {
                    tvSelectedPhotoName.setText("Selected avatar: " + resName);
                }
            });
        }

        if (doctorToEdit != null) {
            if (etName != null) etName.setText(doctorToEdit.getName());
            if (etDept != null) etDept.setText(doctorToEdit.getDepartment());
            if (etSpec != null) etSpec.setText(doctorToEdit.getSpecialization());
            if (etQual != null) etQual.setText(doctorToEdit.getQualification());
            if (etExpGender != null) etExpGender.setText(doctorToEdit.getExperienceGender());
            if (etContact != null) etContact.setText(doctorToEdit.getContactNumber());
            if (etFeeCapacity != null) etFeeCapacity.setText(doctorToEdit.getFeeCapacity());
            if (etTimeSlots != null) etTimeSlots.setText(doctorToEdit.getTimeSlots());

            if (doctorToEdit.getImageResName() != null) {
                int resId = getContext().getResources().getIdentifier(doctorToEdit.getImageResName(), "drawable", getContext().getPackageName());
                if (resId != 0 && imgSelectedPhoto != null) {
                    imgSelectedPhoto.setImageResource(resId);
                }
            }
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String name = (etName != null && etName.getText() != null) ? etName.getText().toString().trim() : "";
                String dept = (etDept != null && etDept.getText() != null) ? etDept.getText().toString().trim() : "";
                String spec = (etSpec != null && etSpec.getText() != null) ? etSpec.getText().toString().trim() : "";
                String qual = (etQual != null && etQual.getText() != null) ? etQual.getText().toString().trim() : "";
                String expGender = (etExpGender != null && etExpGender.getText() != null) ? etExpGender.getText().toString().trim() : "";
                String contact = (etContact != null && etContact.getText() != null) ? etContact.getText().toString().trim() : "";
                String feeCap = (etFeeCapacity != null && etFeeCapacity.getText() != null) ? etFeeCapacity.getText().toString().trim() : "10/day";
                String slots = (etTimeSlots != null && etTimeSlots.getText() != null) ? etTimeSlots.getText().toString().trim() : "10:00 AM - 02:00 PM";

                if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter doctor name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!name.toLowerCase().startsWith("dr")) {
                    name = "Dr. " + name;
                }

                if (dept.isEmpty()) dept = "General Medicine";

                String id = (doctorToEdit != null) ? doctorToEdit.getId() : "DOC-" + System.currentTimeMillis();
                String about = "Specialist in " + dept + " (" + spec + ") providing expert care.";
                String avatar = AVATAR_RES[selectedAvatarIndex];

                DoctorModel savedDoctor = new DoctorModel(id, name, dept, spec, qual, expGender, contact, feeCap, slots, about, avatar);

                if (doctorToEdit != null) {
                    savedDoctor.setLeaveStartDate(doctorToEdit.getLeaveStartDate());
                    savedDoctor.setLeaveEndDate(doctorToEdit.getLeaveEndDate());
                    savedDoctor.setLeaveReason(doctorToEdit.getLeaveReason());
                    savedDoctor.setUnavailable(doctorToEdit.isUnavailable());
                }

                if (savedListener != null) {
                    savedListener.onDoctorSaved(savedDoctor);
                }

                Toast.makeText(getContext(), "✓ Doctor details saved successfully!", Toast.LENGTH_SHORT).show();
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
