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

        if (today != null) {
            today.removeAllViews();
            add(inflater, today, R.drawable.ic_check_circle, "Welcome to Health Assist Hospital!", "Just now");
        }

        if (yesterday != null) {
            yesterday.removeAllViews();
            yesterday.setVisibility(View.GONE);
        }

        return v;
    }

    private void add(LayoutInflater inflater, LinearLayout parent, int icon, String title, String time) {
        View item = inflater.inflate(R.layout.item_notification, parent, false);
        ((ImageView) item.findViewById(R.id.nIcon)).setImageResource(icon);
        ((TextView) item.findViewById(R.id.nTitle)).setText(title);
        ((TextView) item.findViewById(R.id.nTime)).setText(time);
        parent.addView(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_NOTIFY);
    }
}
