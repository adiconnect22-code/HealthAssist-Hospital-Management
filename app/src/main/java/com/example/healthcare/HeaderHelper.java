package com.example.healthcare;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/** Fills the shared green header (include_header.xml). */
public final class HeaderHelper {

    private HeaderHelper() {
    }

    public static void bind(final Activity activity, View root, String title, String subtitle,
                            boolean showBack, boolean showMore) {
        TextView t = root.findViewById(R.id.headerTitle);
        t.setText(title);

        TextView s = root.findViewById(R.id.headerSubtitle);
        if (subtitle == null) {
            s.setVisibility(View.GONE);
        } else {
            s.setText(subtitle);
            s.setVisibility(View.VISIBLE);
        }

        View back = root.findViewById(R.id.btnBack);
        back.setVisibility(showBack ? View.VISIBLE : View.GONE);
        back.setOnClickListener(v ->
                ((AppCompatActivity) activity).getOnBackPressedDispatcher().onBackPressed());

        View more = root.findViewById(R.id.btnMore);
        more.setVisibility(showMore ? View.VISIBLE : View.GONE);
        if (showMore && activity instanceof MainActivity) {
            more.setOnClickListener(v -> ((MainActivity) activity).showMoreMenu(v));
        }
    }
}
