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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.google.android.material.button.MaterialButton;

/**
 * DialogFragment in 'more_section' folder.
 */
public class AddEditDepartmentDialog extends DialogFragment {

    private static final String ARG_DEPARTMENT = "arg_department";

    public interface OnDepartmentSavedListener {
        void onSaved(DepartmentModel department);
    }

    private DepartmentModel departmentToEdit;
    private OnDepartmentSavedListener savedListener;

    public static AddEditDepartmentDialog newInstance(@Nullable DepartmentModel department) {
        AddEditDepartmentDialog dialog = new AddEditDepartmentDialog();
        if (department != null) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_DEPARTMENT, department);
            dialog.setArguments(args);
        }
        return dialog;
    }

    public void setOnDepartmentSavedListener(OnDepartmentSavedListener listener) {
        this.savedListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            departmentToEdit = (DepartmentModel) getArguments().getSerializable(ARG_DEPARTMENT);
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
        return inflater.inflate(R.layout.dialog_add_edit_department, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        EditText etDepartmentName = view.findViewById(R.id.etDepartmentName);
        EditText etFloorNumber = view.findViewById(R.id.etFloorNumber);
        EditText etDepartmentHead = view.findViewById(R.id.etDepartmentHead);
        EditText etStatus = view.findViewById(R.id.etDepartmentStatus);

        MaterialButton btnSave = view.findViewById(R.id.btnSaveDepartment);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelDepartment);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> dismiss());

        if (departmentToEdit != null) {
            if (etDepartmentName != null) etDepartmentName.setText(departmentToEdit.getName());
            if (etFloorNumber != null) etFloorNumber.setText(departmentToEdit.getFloor());
            if (etDepartmentHead != null) etDepartmentHead.setText(departmentToEdit.getHeadDoctor());
            if (etStatus != null && departmentToEdit.getStatus() != null) {
                etStatus.setText(departmentToEdit.getStatus());
            }
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                String name = (etDepartmentName != null && etDepartmentName.getText() != null)
                        ? etDepartmentName.getText().toString().trim() : "";
                String floor = (etFloorNumber != null && etFloorNumber.getText() != null)
                        ? etFloorNumber.getText().toString().trim() : "";
                String head = (etDepartmentHead != null && etDepartmentHead.getText() != null)
                        ? etDepartmentHead.getText().toString().trim() : "";
                String status = (etStatus != null && etStatus.getText() != null)
                        ? etStatus.getText().toString().trim() : "Active";

                if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter department name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (floor.isEmpty()) {
                    floor = "Floor 1";
                }

                String id = (departmentToEdit != null) ? departmentToEdit.getId() : "DEPT-" + System.currentTimeMillis();
                DepartmentModel updatedDept = new DepartmentModel(id, name, floor, head, status);

                saveDepartmentToDatabase(updatedDept);

                if (savedListener != null) {
                    savedListener.onSaved(updatedDept);
                }

                Toast.makeText(getContext(), "Department saved", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        }
    }

    private void saveDepartmentToDatabase(DepartmentModel department) {
        // TODO: Save or update department in database
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
