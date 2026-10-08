package com.example.healthcare.admin.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;
import com.example.healthcare.admin.appts_section.AppointmentsActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationListDialog extends DialogFragment {

    private static final String TAG = "NotificationListDialog";
    private Runnable onDismissListener;

    public static NotificationListDialog newInstance() {
        return new NotificationListDialog();
    }

    public void setOnDismissNotificationListener(Runnable listener) {
        this.onDismissListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btnNotificationClose);
        MaterialButton btnDone = view.findViewById(R.id.btnDoneNotifications);

        LinearLayout container = view.findViewById(R.id.layoutNotificationContainer);
        TextView tvEmpty = view.findViewById(R.id.tvEmptyNotificationText);

        if (btnClose != null) btnClose.setOnClickListener(v -> handleDismiss());
        if (btnDone != null) btnDone.setOnClickListener(v -> handleDismiss());

        fetchPendingAppointments(inflaterFromContext(getContext()), container, tvEmpty);
    }

    private LayoutInflater inflaterFromContext(Context c) {
        return LayoutInflater.from(c != null ? c : requireContext());
    }

    private void fetchPendingAppointments(LayoutInflater inflater, LinearLayout container, TextView tvEmpty) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        if (container != null) container.removeAllViews();
                        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            String patient = doc.getString("patientName");
                            String doctor = doc.getString("doctorName");
                            String date = doc.getString("date");

                            if (patient == null || patient.isEmpty()) patient = "Patient";
                            if (doctor == null || doctor.isEmpty()) doctor = "Doctor";

                            View item = inflater.inflate(R.layout.item_notification, container, false);
                            ImageView icon = item.findViewById(R.id.nIcon);
                            TextView tvTitle = item.findViewById(R.id.nTitle);
                            TextView tvTime = item.findViewById(R.id.nTime);

                            if (icon != null) {
                                icon.setImageResource(R.drawable.ic_clock);
                                icon.setColorFilter(Color.parseColor("#F57F17"));
                            }
                            if (tvTitle != null) {
                                tvTitle.setText("New Booking Request: " + patient + " booked " + doctor);
                            }
                            if (tvTime != null) {
                                tvTime.setText(date != null ? date : "Just now");
                            }

                            item.setOnClickListener(v -> {
                                handleDismiss();
                                try {
                                    Intent intent = new Intent(getContext(), AppointmentsActivity.class);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error opening AppointmentsActivity", e);
                                }
                            });

                            if (container != null) container.addView(item);
                        }
                    } else {
                        if (container != null) container.removeAllViews();
                        if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
                });
    }

    private void handleDismiss() {
        if (onDismissListener != null) {
            onDismissListener.run();
        }
        dismiss();
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
