package com.example.healthcare.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcare.DialogHelper;
import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;

public class AppointmentsFragment extends Fragment {

    private static final String ARG_SUB = "sub";

    private TextView[] tabs;
    private View[] panels;

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

        tabs = new TextView[]{
                v.findViewById(R.id.tabUpcoming), v.findViewById(R.id.tabPending),
                v.findViewById(R.id.tabCompleted), v.findViewById(R.id.tabCancelled)};
        panels = new View[]{
                v.findViewById(R.id.panelUpcoming), v.findViewById(R.id.panelPending),
                v.findViewById(R.id.panelCompleted), v.findViewById(R.id.panelCancelled)};

        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            tabs[i].setOnClickListener(x -> select(index));
        }

        v.findViewById(R.id.btnReschedule).setOnClickListener(x ->
                DialogHelper.showReschedule(main, "Dr. Mehta"));
        v.findViewById(R.id.btnWithdraw).setOnClickListener(x -> DialogHelper.showWithdraw(main));

        v.findViewById(R.id.btnRx1).setOnClickListener(x -> DialogHelper.showPrescription(main,
                "Dr. Iyer", "20 Aug",
                "Rest for 3 days, follow up in 2 weeks. Avoid salt-heavy food. "
                        + "Prescribed: Tab. Paracetamol 500mg, twice daily."));
        v.findViewById(R.id.btnRx2).setOnClickListener(x -> DialogHelper.showPrescription(main,
                "Dr. Mehta", "10 Aug",
                "Continue current medication. Blood pressure stable \u2014 recheck in 1 month."));
        v.findViewById(R.id.btnRate1).setOnClickListener(x ->
                DialogHelper.showRate(main, "Dr. Iyer \u00B7 Orthopedics"));
        v.findViewById(R.id.btnRate2).setOnClickListener(x ->
                DialogHelper.showRate(main, "Dr. Mehta \u00B7 Cardiology"));

        int start = getArguments() != null ? getArguments().getInt(ARG_SUB, 0) : 0;
        select(start);
        return v;
    }

    private void select(int index) {
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setSelected(i == index);
            panels[i].setVisibility(i == index ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_APPTS);
    }
}
