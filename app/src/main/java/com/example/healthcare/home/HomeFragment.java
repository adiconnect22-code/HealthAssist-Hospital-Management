package com.example.healthcare.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;
import com.example.healthcare.appointment.DoctorProfileFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class HomeFragment extends Fragment {

    private TextView tvUserInitials;
    private TextView tvUserName;
    private TextView tvUserMeta;
    private TextView tvCountUpcoming;
    private TextView tvCountPending;
    private TextView tvCountCompleted;

    private TextView tvNextLabel;
    private View cardNext;
    private TextView tvNextDocTitle;
    private TextView tvNextDate;
    private TextView tvNextStatusBadge;

    private TextView tvRecentLabel;
    private View cardRecent;
    private TextView tvRecentDocTitle;

    private ListenerRegistration apptsListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        final MainActivity main = (MainActivity) requireActivity();

        SharedPreferences prefs = requireContext().getSharedPreferences("healthcare_patient", Context.MODE_PRIVATE);
        String name = prefs.getString("patient_name", "adithya");
        String blood = prefs.getString("patient_blood", "O+");
        String gender = prefs.getString("patient_gender", "Male");
        String patientId = prefs.getString("patient_id", "#9430");

        String firstName = name.split("\\s+")[0];
        HeaderHelper.bind(main, v, "Hi, " + firstName, "Health Assist Hospital", false, true);

        tvUserInitials = v.findViewById(R.id.tvUserInitials);
        tvUserName = v.findViewById(R.id.tvUserName);
        tvUserMeta = v.findViewById(R.id.tvUserMeta);

        tvCountUpcoming = v.findViewById(R.id.tvCountUpcoming);
        tvCountPending = v.findViewById(R.id.tvCountPending);
        tvCountCompleted = v.findViewById(R.id.tvCountCompleted);

        tvNextLabel = v.findViewById(R.id.tvNextLabel);
        cardNext = v.findViewById(R.id.cardNext);
        tvNextDocTitle = v.findViewById(R.id.tvNextDocTitle);
        tvNextDate = v.findViewById(R.id.tvNextDate);
        tvNextStatusBadge = v.findViewById(R.id.tvNextStatusBadge);

        tvRecentLabel = v.findViewById(R.id.tvRecentLabel);
        cardRecent = v.findViewById(R.id.cardRecent);
        tvRecentDocTitle = v.findViewById(R.id.tvRecentDocTitle);

        if (tvUserName != null) tvUserName.setText(name);
        if (tvUserMeta != null) tvUserMeta.setText(blood + " · " + gender + " · Patient ID " + patientId);

        String initials = "AD";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            initials = (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        } else if (name.length() >= 2) {
            initials = name.substring(0, 2).toUpperCase();
        }
        if (tvUserInitials != null) tvUserInitials.setText(initials);

        v.findViewById(R.id.btnByDoctor).setOnClickListener(x ->
                main.navigateTo(FindDoctorFragment.newInstance(0)));
        v.findViewById(R.id.btnBySymptom).setOnClickListener(x ->
                main.navigateTo(FindDoctorFragment.newInstance(1)));
        if (cardNext != null) {
            cardNext.setOnClickListener(x -> main.openAppointments(0));
        }
        if (cardRecent != null) {
            cardRecent.setOnClickListener(x ->
                    main.navigateTo(DoctorProfileFragment.newInstance("Dr. Rahul Sharma")));
        }

        fetchPatientAppointmentCountsFromFirestore(name);

        return v;
    }

    private void fetchPatientAppointmentCountsFromFirestore(String currentPatientName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        apptsListener = db.collection("appointments")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    int upcoming = 0;
                    int pending = 0;
                    int completed = 0;

                    DocumentSnapshot nextApptDoc = null;
                    DocumentSnapshot recentDoc = null;

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        String docPatient = doc.getString("patientName");
                        if (docPatient == null) docPatient = doc.getString("patient");

                        // Strict filter by logged in patient name
                        if (docPatient != null && docPatient.equalsIgnoreCase(currentPatientName)) {
                            String status = doc.getString("status");
                            if (status == null) status = "Pending";

                            if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Upcoming")) {
                                upcoming++;
                                if (nextApptDoc == null) nextApptDoc = doc;
                            } else if (status.equalsIgnoreCase("Pending")) {
                                pending++;
                                if (nextApptDoc == null) nextApptDoc = doc;
                            } else if (status.equalsIgnoreCase("Completed")) {
                                completed++;
                                if (recentDoc == null) recentDoc = doc;
                            }
                        }
                    }

                    final int upFinal = upcoming;
                    final int pFinal = pending;
                    final int compFinal = completed;
                    final DocumentSnapshot nextFinal = nextApptDoc;
                    final DocumentSnapshot recentFinal = recentDoc;

                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (tvCountUpcoming != null) tvCountUpcoming.setText(String.valueOf(upFinal));
                            if (tvCountPending != null) tvCountPending.setText(String.valueOf(pFinal));
                            if (tvCountCompleted != null) tvCountCompleted.setText(String.valueOf(compFinal));

                            // Hide Next Appointment section if user has 0 upcoming/pending appointments
                            if (nextFinal == null || (upFinal == 0 && pFinal == 0)) {
                                if (tvNextLabel != null) tvNextLabel.setVisibility(View.GONE);
                                if (cardNext != null) cardNext.setVisibility(View.GONE);
                            } else {
                                if (tvNextLabel != null) tvNextLabel.setVisibility(View.VISIBLE);
                                if (cardNext != null) cardNext.setVisibility(View.VISIBLE);

                                String docName = nextFinal.getString("doctorName");
                                String dept = nextFinal.getString("department");
                                String date = nextFinal.getString("date");
                                String slot = nextFinal.getString("timeSlot");
                                String status = nextFinal.getString("status");

                                if (tvNextDocTitle != null) tvNextDocTitle.setText((docName != null ? docName : "Doctor") + " · " + (dept != null ? dept : "General"));
                                if (tvNextDate != null) tvNextDate.setText((date != null ? date : "Today") + ", " + (slot != null ? slot : "10:30 AM"));
                                if (tvNextStatusBadge != null) tvNextStatusBadge.setText(status != null ? status : "Pending");
                            }

                            // Hide Recent Doctors section if user has 0 completed consultations
                            if (recentFinal == null || compFinal == 0) {
                                if (tvRecentLabel != null) tvRecentLabel.setVisibility(View.GONE);
                                if (cardRecent != null) cardRecent.setVisibility(View.GONE);
                            } else {
                                if (tvRecentLabel != null) tvRecentLabel.setVisibility(View.VISIBLE);
                                if (cardRecent != null) cardRecent.setVisibility(View.VISIBLE);

                                String docName = recentFinal.getString("doctorName");
                                String dept = recentFinal.getString("department");
                                if (tvRecentDocTitle != null) tvRecentDocTitle.setText((docName != null ? docName : "Doctor") + " · " + (dept != null ? dept : "General"));
                            }
                        });
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (apptsListener != null) {
            apptsListener.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_HOME);
    }
}
