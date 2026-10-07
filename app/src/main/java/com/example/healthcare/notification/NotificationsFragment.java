package com.example.healthcare.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;
import com.example.healthcare.appointment.PrescriptionNotesFragment;

public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        final MainActivity main = (MainActivity) requireActivity();
        HeaderHelper.bind(main, v, "Notification Log", null, false, true);

        LinearLayout today = v.findViewById(R.id.listToday);
        LinearLayout yesterday = v.findViewById(R.id.listYesterday);

        add(inflater, today, R.drawable.ic_check_circle, "Appointment approved", "9:02 AM", false);
        add(inflater, today, R.drawable.ic_arrow_up_circle, "Waitlist upgraded to confirmed", "8:40 AM", false);

        add(inflater, yesterday, R.drawable.ic_clock, "Reminder: visit tomorrow, 4:30 PM", "6:15 PM", false);
        add(inflater, yesterday, R.drawable.ic_file, "New prescription uploaded", "3:20 PM", true);
        add(inflater, yesterday, R.drawable.ic_clipboard_check, "Registration approved", "11:05 AM", false);
        return v;
    }

    private void add(LayoutInflater inflater, LinearLayout parent, int icon, String title,
                     String time, boolean opensPrescription) {
        View item = inflater.inflate(R.layout.item_notification, parent, false);
        ((ImageView) item.findViewById(R.id.nIcon)).setImageResource(icon);
        ((TextView) item.findViewById(R.id.nTitle)).setText(title);
        ((TextView) item.findViewById(R.id.nTime)).setText(time);
        if (opensPrescription) {
            item.setOnClickListener(x ->
                    ((MainActivity) requireActivity()).navigateTo(new PrescriptionNotesFragment()));
        }
        parent.addView(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_NOTIFY);
    }
}
