package com.example.healthcare.more_section;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.google.android.material.button.MaterialButton;

/**
 * DialogFragment in 'more_section' folder.
 */
public class AddPrescriptionDialog extends DialogFragment {

    private static final String ARG_PRESCRIPTION = "arg_prescription";

    public interface OnPrescriptionSavedListener {
        void onSaved(PrescriptionModel prescription);
    }

    private PrescriptionModel prescriptionToEdit;
    private OnPrescriptionSavedListener savedListener;
    private String selectedAttachment = "";

    public static AddPrescriptionDialog newInstance(@Nullable PrescriptionModel prescription) {
        AddPrescriptionDialog dialog = new AddPrescriptionDialog();
        if (prescription != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_PRESCRIPTION, prescription);
            dialog.setArguments(args);
        }
        return dialog;
    }

    public void setOnPrescriptionSavedListener(OnPrescriptionSavedListener listener) {
        this.savedListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            prescriptionToEdit = (PrescriptionModel) getArguments().getSerializable(ARG_PRESCRIPTION);
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
        return inflater.inflate(R.layout.dialog_add_prescription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        EditText etPatientName = view.findViewById(R.id.etPatientName);
        EditText etDiagnosis = view.findViewById(R.id.etDiagnosis);
        EditText etMedicines = view.findViewById(R.id.etMedicines);
        EditText etInstructions = view.findViewById(R.id.etInstructions);
        EditText etFollowUpDate = view.findViewById(R.id.etFollowUpDate);

        View layoutUploadFile = view.findViewById(R.id.layoutUploadFile);
        TextView tvUploadStatus = view.findViewById(R.id.tvUploadStatus);

        MaterialButton btnSave = view.findViewById(R.id.btnSavePrescription);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelPrescription);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> dismiss());

        if (layoutUploadFile != null) {
            layoutUploadFile.setOnClickListener(v -> {
                selectedAttachment = "prescription_document_" + System.currentTimeMillis() + ".pdf";
                if (tvUploadStatus != null) {
                    tvUploadStatus.setText("✔ Attached: " + selectedAttachment);
                }
                Toast.makeText(getContext(), "Document attached successfully", Toast.LENGTH_SHORT).show();
            });
        }

        if (prescriptionToEdit != null) {
            if (etPatientName != null) etPatientName.setText(prescriptionToEdit.getPatientName());
            if (etDiagnosis != null) etDiagnosis.setText(prescriptionToEdit.getDiagnosis());
            if (etMedicines != null) etMedicines.setText(prescriptionToEdit.getMedicines());
            if (etInstructions != null) etInstructions.setText(prescriptionToEdit.getInstructions());
            if (etFollowUpDate != null) etFollowUpDate.setText(prescriptionToEdit.getFollowUpDate());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String patient = (etPatientName != null && etPatientName.getText() != null)
                        ? etPatientName.getText().toString().trim() : "";
                String diagnosis = (etDiagnosis != null && etDiagnosis.getText() != null)
                        ? etDiagnosis.getText().toString().trim() : "";
                String medicines = (etMedicines != null && etMedicines.getText() != null)
                        ? etMedicines.getText().toString().trim() : "";
                String instructions = (etInstructions != null && etInstructions.getText() != null)
                        ? etInstructions.getText().toString().trim() : "";
                String followUp = (etFollowUpDate != null && etFollowUpDate.getText() != null)
                        ? etFollowUpDate.getText().toString().trim() : "";

                if (patient.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter patient name", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = (prescriptionToEdit != null) ? prescriptionToEdit.getId() : "RX-" + System.currentTimeMillis();
                String doctorName = (prescriptionToEdit != null && prescriptionToEdit.getDoctorName() != null)
                        ? prescriptionToEdit.getDoctorName() : "Dr. Mehta";
                String dateStr = "Today";

                PrescriptionModel updatedRx = new PrescriptionModel(
                        id, patient, doctorName, dateStr, diagnosis, medicines, instructions, followUp, selectedAttachment
                );

                savePrescriptionToDatabase(updatedRx);

                if (savedListener != null) {
                    savedListener.onSaved(updatedRx);
                }

                Toast.makeText(getContext(), "Prescription saved", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        }
    }

    private void savePrescriptionToDatabase(PrescriptionModel prescription) {
        // TODO: Save or update prescription in database
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
