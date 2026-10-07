package com.example.healthcare.appointment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcare.DialogHelper;
import com.example.healthcare.DoctorData;
import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;

public class DoctorProfileFragment extends Fragment {

    private static final String ARG_NAME = "name";

    public static DoctorProfileFragment newInstance(String doctorName) {
        DoctorProfileFragment f = new DoctorProfileFragment();
        Bundle b = new Bundle();
        b.putString(ARG_NAME, doctorName);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        final MainActivity main = (MainActivity) requireActivity();

        String name = getArguments() != null ? getArguments().getString(ARG_NAME) : null;
        final DoctorData d = DoctorData.find(name);

        HeaderHelper.bind(main, v, d.name, null, true, false);

        ((TextView) v.findViewById(R.id.dpInitials)).setText(d.initials);
        ((TextView) v.findViewById(R.id.dpName)).setText(d.name);
        ((TextView) v.findViewById(R.id.dpMeta)).setText(
                d.dept + " \u00B7 " + d.qualification + " \u00B7 " + d.years + " yrs experience");
        ((TextView) v.findViewById(R.id.dpFee)).setText("Consultation Fee: " + d.fee);

        LinearLayout days = v.findViewById(R.id.dpDays);
        for (String day : d.days) {
            TextView chip = (TextView) inflater.inflate(R.layout.item_chip, days, false);
            chip.setText(day);
            chip.setSelected(true);
            chip.setClickable(false);
            chip.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(Math.round(8 * getResources().getDisplayMetrics().density));
            lp.gravity = Gravity.CENTER_VERTICAL;
            days.addView(chip, lp);
        }

        v.findViewById(R.id.btnCheckSlots).setOnClickListener(x ->
                Toast.makeText(main, d.available ? "Slots available: 4\u20136 PM (3 of 10 filled)"
                        : "All slots are full - you can join the waiting list", Toast.LENGTH_SHORT).show());

        v.findViewById(R.id.btnBook).setOnClickListener(x ->
                DialogHelper.showBookAppointment(main, d.name, () -> main.openAppointments(1)));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_NONE);
    }
}
