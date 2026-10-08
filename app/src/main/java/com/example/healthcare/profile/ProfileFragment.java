package com.example.healthcare.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcare.DialogHelper;
import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        final MainActivity main = (MainActivity) requireActivity();
        HeaderHelper.bind(main, v, "My Profile", null, true, true);

        SharedPreferences prefs = requireContext().getSharedPreferences("healthcare_patient", Context.MODE_PRIVATE);
        String name = prefs.getString("patient_name", "adithya");
        String email = prefs.getString("patient_email", "adithya@mail.com");
        String phone = prefs.getString("patient_phone", "+91 9876543210");
        String gender = prefs.getString("patient_gender", "Male");
        String blood = prefs.getString("patient_blood", "O+");
        String dob = prefs.getString("patient_dob", "12/05/1998");

        TextView tvAvatar = v.findViewById(R.id.tvProfileAvatar);
        TextView tvName = v.findViewById(R.id.tvProfileName);
        TextView tvDob = v.findViewById(R.id.tvProfileDob);
        TextView tvGender = v.findViewById(R.id.tvProfileGender);
        TextView tvMobile = v.findViewById(R.id.tvProfileMobile);
        TextView tvEmail = v.findViewById(R.id.tvProfileEmail);
        TextView tvBlood = v.findViewById(R.id.tvProfileBlood);

        if (tvName != null) tvName.setText("Name: " + name);
        if (tvDob != null) tvDob.setText("DOB: " + dob);
        if (tvGender != null) tvGender.setText("Gender: " + gender);
        if (tvMobile != null) tvMobile.setText("Mobile: " + phone);
        if (tvEmail != null) tvEmail.setText("Email: " + email);
        if (tvBlood != null) tvBlood.setText("Blood Group: " + blood);

        String initials = "AD";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            initials = (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        } else if (name.length() >= 2) {
            initials = name.substring(0, 2).toUpperCase();
        }
        if (tvAvatar != null) tvAvatar.setText(initials);

        v.findViewById(R.id.rowChangePassword).setOnClickListener(x ->
                DialogHelper.showChangePassword(main));

        v.findViewById(R.id.btnSaveChanges).setOnClickListener(x ->
                Toast.makeText(main, "Profile details updated successfully!", Toast.LENGTH_SHORT).show());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_PROFILE);
    }
}
