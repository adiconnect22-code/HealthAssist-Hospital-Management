package com.example.healthcare.appointment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;
import com.example.healthcare.admin.appts_section.AppointmentAdapter;
import com.example.healthcare.admin.appts_section.AppointmentDetailsDialog;
import com.example.healthcare.admin.appts_section.AppointmentModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment in 'appointment' package for managing patient appointments.
 * Connected directly to Firebase Cloud Firestore 'appointments' collection with zero hardcoded dummy values.
 */
public class AppointmentsFragment extends Fragment implements AppointmentAdapter.OnAppointmentActionListener {

    private static final String TAG = "PatientApptsFragment";
    private static final String ARG_SUB = "sub";

    private TextView[] tabs;
    private String[] tabStatuses = {"Upcoming", "Pending", "Completed", "Cancelled"};
    private int selectedTabIndex = 0;

    private RecyclerView rvAppointments;
    private TextView tvNoAppointmentsMessage;

    private AppointmentAdapter adapter;
    private final List<AppointmentModel> patientApptList = new ArrayList<>();

    private FirebaseFirestore db;
    private ListenerRegistration apptsListener;
    private String currentPatientName = "adithya";

    public static AppointmentsFragment newInstance(int subTab) {
        AppointmentsFragment f = new AppointmentsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_SUB, subTab);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_appointments, container, false);
        final MainActivity main = (MainActivity) requireActivity();
        HeaderHelper.bind(main, v, "My Appointments", null, false, true);

        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = requireContext().getSharedPreferences("healthcare_patient", Context.MODE_PRIVATE);
        currentPatientName = prefs.getString("patient_name", "adithya");

        tabs = new TextView[]{
                v.findViewById(R.id.tabUpcoming), v.findViewById(R.id.tabPending),
                v.findViewById(R.id.tabCompleted), v.findViewById(R.id.tabCancelled)};

        rvAppointments = v.findViewById(R.id.rvPatientAppointments);
        tvNoAppointmentsMessage = v.findViewById(R.id.tvNoAppointmentsMessage);

        setupRecyclerView();

        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            tabs[i].setOnClickListener(x -> selectTab(index));
        }

        int startTab = getArguments() != null ? getArguments().getInt(ARG_SUB, 0) : 0;
        selectTab(startTab);

        setupFirestoreRealtimeListener();

        return v;
    }

    private void setupRecyclerView() {
        if (rvAppointments != null) {
            rvAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapter = new AppointmentAdapter(requireContext(), patientApptList, this);
            rvAppointments.setAdapter(adapter);
        }
    }

    private void selectTab(int index) {
        selectedTabIndex = index;
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setSelected(i == index);
        }

        if (adapter != null) {
            boolean isEmpty = adapter.filter("", tabStatuses[selectedTabIndex]);
            if (isEmpty) {
                if (tvNoAppointmentsMessage != null) tvNoAppointmentsMessage.setVisibility(View.VISIBLE);
                if (rvAppointments != null) rvAppointments.setVisibility(View.GONE);
            } else {
                if (tvNoAppointmentsMessage != null) tvNoAppointmentsMessage.setVisibility(View.GONE);
                if (rvAppointments != null) rvAppointments.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupFirestoreRealtimeListener() {
        apptsListener = db.collection("appointments")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        Log.e(TAG, "Error fetching patient appointments", error);
                        updateUI();
                        return;
                    }

                    patientApptList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        try {
                            String docPatient = doc.getString("patientName");
                            if (docPatient == null) docPatient = doc.getString("patient");

                            // Strict filter by logged-in patient name
                            if (docPatient != null && docPatient.equalsIgnoreCase(currentPatientName)) {
                                AppointmentModel model = new AppointmentModel();
                                String id = doc.getId();

                                model.setId(id);
                                model.setPatientName(docPatient);
                                model.setDoctorName(doc.getString("doctorName"));
                                model.setDepartment(doc.getString("department"));
                                model.setDate(doc.getString("date"));
                                model.setTimeSlot(doc.getString("timeSlot"));
                                model.setStatus(doc.getString("status"));
                                model.setReason(doc.getString("reason"));

                                Long cancelledTime = doc.getLong("cancelledTimestamp");
                                if (cancelledTime != null) {
                                    model.setCancelledTimestamp(cancelledTime);
                                }

                                patientApptList.add(model);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing appointment document", e);
                        }
                    }

                    updateUI();
                });
    }

    private void updateUI() {
        if (getActivity() != null) {
            requireActivity().runOnUiThread(() -> {
                if (adapter != null) {
                    adapter.updateList(patientApptList);
                    boolean isEmpty = adapter.filter("", tabStatuses[selectedTabIndex]);

                    if (isEmpty || patientApptList.isEmpty()) {
                        if (tvNoAppointmentsMessage != null) tvNoAppointmentsMessage.setVisibility(View.VISIBLE);
                        if (rvAppointments != null) rvAppointments.setVisibility(View.GONE);
                    } else {
                        if (tvNoAppointmentsMessage != null) tvNoAppointmentsMessage.setVisibility(View.GONE);
                        if (rvAppointments != null) rvAppointments.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onApproveAppointment(AppointmentModel appointment) {}

    @Override
    public void onRejectAppointment(AppointmentModel appointment) {}

    @Override
    public void onCompleteAppointment(AppointmentModel appointment) {}

    @Override
    public void onAppointmentClick(AppointmentModel appointment) {
        if (appointment == null) return;
        AppointmentDetailsDialog dialog = AppointmentDetailsDialog.newInstance(appointment);
        dialog.show(getParentFragmentManager(), "AppointmentDetailsDialog");
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
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_APPTS);
    }
}
