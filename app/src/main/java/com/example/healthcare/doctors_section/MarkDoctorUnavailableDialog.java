package com.example.healthcare.doctors_section;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * DialogFragment in 'doctors_section' folder for Mark Doctor Unavailable.
 */
public class MarkDoctorUnavailableDialog extends DialogFragment {

    private static final String ARG_DOCTORS = "arg_doctors";

    public interface OnLeaveSavedListener {
        void onLeaveSaved(String doctorName, String startDate, String endDate, String reason);
    }

    private ArrayList<DoctorModel> doctorList = new ArrayList<>();
    private OnLeaveSavedListener savedListener;

    public static MarkDoctorUnavailableDialog newInstance(ArrayList<DoctorModel> doctors) {
        MarkDoctorUnavailableDialog dialog = new MarkDoctorUnavailableDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DOCTORS, (Serializable) doctors);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnLeaveSavedListener(OnLeaveSavedListener listener) {
        this.savedListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<DoctorModel> list = (ArrayList<DoctorModel>) getArguments().getSerializable(ARG_DOCTORS);
            if (list != null) {
                doctorList = list;
            }
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
        return inflater.inflate(R.layout.dialog_mark_doctor_unavailable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        AutoCompleteTextView etSelectDoctor = view.findViewById(R.id.etSelectDoctor);
        EditText etStartDate = view.findViewById(R.id.etLeaveStartDate);
        EditText etEndDate = view.findViewById(R.id.etLeaveEndDate);
        EditText etReason = view.findViewById(R.id.etLeaveReason);

        MaterialButton btnSave = view.findViewById(R.id.btnSaveLeave);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelLeave);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> dismiss());

        // Setup AutoComplete Doctors Dropdown
        ArrayList<String> doctorNames = new ArrayList<>();
        for (DoctorModel doc : doctorList) {
            if (doc != null && doc.getName() != null) {
                doctorNames.add(doc.getName());
            }
        }
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_dropdown_item_1line, doctorNames
            );
            etSelectDoctor.setAdapter(adapter);
            etSelectDoctor.setOnClickListener(v -> etSelectDoctor.showDropDown());
        }

        // Setup Date Pickers
        setupDatePicker(etStartDate);
        setupDatePicker(etEndDate);

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String docName = (etSelectDoctor != null && etSelectDoctor.getText() != null)
                        ? etSelectDoctor.getText().toString().trim() : "";
                String startDate = (etStartDate != null && etStartDate.getText() != null)
                        ? etStartDate.getText().toString().trim() : "";
                String endDate = (etEndDate != null && etEndDate.getText() != null)
                        ? etEndDate.getText().toString().trim() : "";
                String reason = (etReason != null && etReason.getText() != null)
                        ? etReason.getText().toString().trim() : "";

                if (docName.isEmpty()) {
                    Toast.makeText(getContext(), "Please select a doctor", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (startDate.isEmpty() || endDate.isEmpty()) {
                    Toast.makeText(getContext(), "Please select leave start and end dates", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (savedListener != null) {
                    savedListener.onLeaveSaved(docName, startDate, endDate, reason);
                }

                Toast.makeText(getContext(), "Doctor marked as unavailable", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        }
    }

    private void setupDatePicker(EditText dateEditText) {
        if (dateEditText == null || getContext() == null) return;

        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                String formattedMonth = (selectedMonth + 1 < 10) ? "0" + (selectedMonth + 1) : String.valueOf(selectedMonth + 1);
                String formattedDay = (selectedDay < 10) ? "0" + selectedDay : String.valueOf(selectedDay);
                String dateStr = selectedYear + "-" + formattedMonth + "-" + formattedDay;
                dateEditText.setText(dateStr);
            }, year, month, day);

            dialog.show();
        });
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
