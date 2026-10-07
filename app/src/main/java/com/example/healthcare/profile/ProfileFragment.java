package com.example.healthcare.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        v.findViewById(R.id.rowChangePassword).setOnClickListener(x ->
                DialogHelper.showChangePassword(main));
        v.findViewById(R.id.btnSaveChanges).setOnClickListener(x ->
                Toast.makeText(main, "Changes saved (demo)", Toast.LENGTH_SHORT).show());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_PROFILE);
    }
}
