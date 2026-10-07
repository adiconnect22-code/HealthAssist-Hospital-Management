package com.example.healthcare.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;
import com.example.healthcare.appointment.DoctorProfileFragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        final MainActivity main = (MainActivity) requireActivity();

        HeaderHelper.bind(main, v, "Hi, Asha", "Health Assist Hospital", false, true);

        v.findViewById(R.id.btnByDoctor).setOnClickListener(x ->
                main.navigateTo(FindDoctorFragment.newInstance(0)));
        v.findViewById(R.id.btnBySymptom).setOnClickListener(x ->
                main.navigateTo(FindDoctorFragment.newInstance(1)));
        v.findViewById(R.id.cardNext).setOnClickListener(x -> main.openAppointments(0));
        v.findViewById(R.id.cardRecent).setOnClickListener(x ->
                main.navigateTo(DoctorProfileFragment.newInstance("Dr. Mehta")));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_HOME);
    }
}
