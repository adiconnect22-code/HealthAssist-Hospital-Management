package com.example.healthcare.admin.more_section;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * DialogFragment in 'more_section' folder.
 */
public class ExportCsvDialog extends DialogFragment {

    public static ExportCsvDialog newInstance() {
        return new ExportCsvDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_export_csv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnDialogClose);
        MaterialButton btnDownload = view.findViewById(R.id.btnDownloadCsv);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelExport);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> dismiss());

        if (btnDownload != null) {
            btnDownload.setOnClickListener(v -> {
                exportCsvData();
                dismiss();
            });
        }
    }

    private void exportCsvData() {
        Context context = getContext();
        if (context == null) return;

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Appointment ID,Patient Name,Doctor Name,Department,Date,Status\n");

        List<String[]> rows = fetchReportDataFromDatabase();
        for (String[] row : rows) {
            csvBuilder.append(String.join(",", row)).append("\n");
        }

        String fileName = "hospital_appointments_report_" + System.currentTimeMillis() + ".csv";
        boolean success = saveCsvFileToStorage(context, fileName, csvBuilder.toString());

        if (success) {
            Toast.makeText(context, "CSV File saved: " + fileName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to save CSV file", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveCsvFileToStorage(Context context, String fileName, String content) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
                        if (os != null) {
                            os.write(content.getBytes(StandardCharsets.UTF_8));
                            return true;
                        }
                    }
                }
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloadsDir.exists()) downloadsDir.mkdirs();

                File file = new File(downloadsDir, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(content.getBytes(StandardCharsets.UTF_8));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<String[]> fetchReportDataFromDatabase() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"#ORD-1001", "John Doe", "Dr. Sarah Smith", "Cardiology", "2025-08-28", "Confirmed"});
        list.add(new String[]{"#ORD-1002", "Emma Watson", "Dr. David Lee", "Orthopedics", "2025-08-28", "Completed"});
        list.add(new String[]{"#ORD-1003", "Robert Brown", "Dr. Michael Clark", "Orthopedics", "2025-08-27", "Pending"});
        list.add(new String[]{"#ORD-1004", "Rahul", "Dr. Sharma", "General", "2025-08-25", "Completed"});
        list.add(new String[]{"#ORD-1005", "Suresh Raina", "Dr. Arjun Patel", "Cardiology", "2025-08-26", "Pending"});
        return list;
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
