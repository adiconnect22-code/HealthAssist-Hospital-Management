package com.example.healthcare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Builds modal dialogs and saves live bookings directly to Firebase Cloud Firestore. */
public final class DialogHelper {

    private DialogHelper() {
    }

    public static final String[] SLOTS = {
            "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
            "2:00 PM", "3:00 PM", "4:00 PM", "4:30 PM", "5:00 PM", "6:00 PM"
    };

    /** Creates a floating rounded dialog from a layout. */
    public static Dialog create(Activity a, int layoutRes) {
        View content = LayoutInflater.from(a).inflate(layoutRes, null);
        Dialog d = new Dialog(a);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(content);
        Window w = d.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            w.setDimAmount(0.55f);
            int width = (int) (a.getResources().getDisplayMetrics().widthPixels * 0.88f);
            w.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return d;
    }

    private static void closeOn(final Dialog d, int... ids) {
        for (int id : ids) {
            View v = d.findViewById(id);
            if (v != null) v.setOnClickListener(x -> d.dismiss());
        }
    }

    // ------------------------------------------------------------------ pickers

    public static void pickDate(Activity a, final TextView target, boolean futureOnly) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(a, (view, y, m, day) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(y, m, day);
            target.setText(new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(sel.getTime()));
            target.setTag(Boolean.TRUE);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        if (futureOnly) {
            dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else {
            dp.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        dp.show();
    }

    public static void pickSlot(Activity a, final TextView target) {
        new AlertDialog.Builder(a)
                .setTitle("Select Time Slot")
                .setItems(SLOTS, (dialog, which) -> {
                    target.setText(SLOTS[which]);
                    target.setTag(Boolean.TRUE);
                })
                .show();
    }

    private static String valueOr(TextView tv, String fallback) {
        return (tv != null && tv.getTag() != null) ? tv.getText().toString() : fallback;
    }

    // ------------------------------------------------------------------ generic

    public static Dialog showMessage(Activity a, int iconRes, int iconColorRes, String title,
                                     String message, String badge, String primaryText,
                                     boolean primaryDanger, final Runnable onPrimary,
                                     String secondaryText) {
        final Dialog d = create(a, R.layout.dialog_message);

        ImageView icon = d.findViewById(R.id.dlgIcon);
        icon.setImageResource(iconRes);
        icon.setColorFilter(ContextCompat.getColor(a, iconColorRes));

        ((TextView) d.findViewById(R.id.dlgTitle)).setText(title);
        ((TextView) d.findViewById(R.id.dlgMessage)).setText(message);

        TextView b = d.findViewById(R.id.dlgBadge);
        if (badge == null) {
            b.setVisibility(View.GONE);
        } else {
            b.setText(badge);
            b.setVisibility(View.VISIBLE);
        }

        TextView p = d.findViewById(R.id.dlgPrimary);
        p.setText(primaryText);
        if (primaryDanger) p.setBackgroundResource(R.drawable.bg_btn_danger);
        p.setOnClickListener(x -> {
            d.dismiss();
            if (onPrimary != null) onPrimary.run();
        });

        TextView s = d.findViewById(R.id.dlgSecondary);
        if (secondaryText == null) {
            s.setVisibility(View.GONE);
        } else {
            s.setText(secondaryText);
            s.setVisibility(View.VISIBLE);
            s.setOnClickListener(x -> d.dismiss());
        }

        d.show();
        return d;
    }

    // ------------------------------------------------------------------ theme

    public static void showTheme(final Activity a) {
        final Dialog d = create(a, R.layout.dialog_theme);
        final View radioLight = d.findViewById(R.id.radioLight);
        final View radioDark = d.findViewById(R.id.radioDark);

        int nightBits = a.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        final boolean[] dark = {nightBits == Configuration.UI_MODE_NIGHT_YES};

        final Runnable refresh = () -> {
            radioLight.setSelected(!dark[0]);
            radioDark.setSelected(dark[0]);
        };
        refresh.run();

        d.findViewById(R.id.optLight).setOnClickListener(x -> {
            dark[0] = false;
            refresh.run();
        });
        d.findViewById(R.id.optDark).setOnClickListener(x -> {
            dark[0] = true;
            refresh.run();
        });
        closeOn(d, R.id.thClose);
        d.findViewById(R.id.thApply).setOnClickListener(x -> {
            d.dismiss();
            AppCompatDelegate.setDefaultNightMode(
                    dark[0] ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
        d.show();
    }

    // ------------------------------------------------------------------ booking

    public static void showBookAppointment(final Activity a, final String doctor,
                                           final Runnable onViewAppointments) {
        final Dialog d = create(a, R.layout.dialog_book);
        final TextView date = d.findViewById(R.id.bookDate);
        final TextView time = d.findViewById(R.id.bookTime);

        date.setOnClickListener(x -> pickDate(a, date, true));
        time.setOnClickListener(x -> pickSlot(a, time));
        closeOn(d, R.id.bookClose, R.id.bookCancel);

        d.findViewById(R.id.bookWaitlist).setOnClickListener(x -> {
            d.dismiss();
            Toast.makeText(a, "Added to waiting list", Toast.LENGTH_SHORT).show();
        });

        d.findViewById(R.id.bookSend).setOnClickListener(x -> {
            String dt = valueOr(date, "Today");
            String tm = valueOr(time, "10:30 AM");
            d.dismiss();

            SharedPreferences prefs = a.getSharedPreferences("healthcare_patient", Context.MODE_PRIVATE);
            String patientName = prefs.getString("patient_name", "adithya");
            String phone = prefs.getString("patient_phone", "+91 9876543210");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String apptId = "APT-" + System.currentTimeMillis();

            Map<String, Object> apptData = new HashMap<>();
            apptData.put("id", apptId);
            apptData.put("patientName", patientName);
            apptData.put("patientPhone", phone);
            apptData.put("doctorName", doctor);
            apptData.put("department", "General Medicine");
            apptData.put("date", dt);
            apptData.put("timeSlot", tm);
            apptData.put("status", "Pending");
            apptData.put("reason", "Consultation with " + doctor);

            db.collection("appointments").document(apptId)
                    .set(apptData)
                    .addOnSuccessListener(aVoid -> Log.d("DialogHelper", "Booking saved to Firestore: " + apptId))
                    .addOnFailureListener(e -> Log.e("DialogHelper", "Error saving booking to Firestore", e));

            showMessage(a, R.drawable.ic_ring_check, R.color.primary, "Booking Request Sent",
                    "Your request for " + dt + ", " + tm + " with " + doctor
                            + " is awaiting admin approval.",
                    "Pending Admin Approval", "View My Appointments", false,
                    onViewAppointments, null);
        });
        d.show();
    }

    public static void showReschedule(final Activity a, final String doctor) {
        final Dialog d = create(a, R.layout.dialog_reschedule);
        ((TextView) d.findViewById(R.id.rsCurrent))
                .setText("Current: 24 Aug, 4:30 PM \u2014 " + doctor);
        ((TextView) d.findViewById(R.id.rsAvail)).setText(doctor + " is available at this slot");

        final TextView date = d.findViewById(R.id.rsDate);
        final TextView time = d.findViewById(R.id.rsTime);
        date.setOnClickListener(x -> pickDate(a, date, true));
        time.setOnClickListener(x -> pickSlot(a, time));
        closeOn(d, R.id.rsClose, R.id.rsCancel);

        d.findViewById(R.id.rsSubmit).setOnClickListener(x -> {
            String dt = valueOr(date, "26 Aug");
            d.dismiss();
            showMessage(a, R.drawable.ic_ring_check, R.color.primary, "Submitted for Approval",
                    "Your reschedule request for " + dt + " with " + doctor
                            + " has been sent to the Admin for approval.",
                    "Pending Admin Approval", "Back to My Appointments", false, null, null);
        });
        d.show();
    }

    public static void showWithdraw(final Activity a) {
        showMessage(a, R.drawable.ic_ring_question, R.color.amber, "Withdraw this appointment?",
                "This request will be sent to the Admin and the slot will be released.",
                null, "Yes, Withdraw", true,
                () -> Toast.makeText(a, "Withdrawal request sent", Toast.LENGTH_SHORT).show(),
                "No");
    }

    // ------------------------------------------------------------------ prescription / rating

    public static void showPrescription(Activity a, String doctor, String date, String note) {
        final Dialog d = create(a, R.layout.dialog_prescription);
        ((TextView) d.findViewById(R.id.rxMeta)).setText("Uploaded by " + doctor + " \u00B7 " + date);
        ((TextView) d.findViewById(R.id.rxText)).setText(note);
        closeOn(d, R.id.rxClose, R.id.rxCloseBtn);
        d.findViewById(R.id.rxDownload).setOnClickListener(x ->
                Toast.makeText(d.getContext(), "Download started", Toast.LENGTH_SHORT).show());
        d.show();
    }

    public static void showRate(final Activity a, String doctorLine) {
        final Dialog d = create(a, R.layout.dialog_rate);
        ((TextView) d.findViewById(R.id.rateDoctor)).setText(doctorLine);

        final int[] ids = {R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5};
        final int[] rating = {4};
        final int starColor = ContextCompat.getColor(a, R.color.star);

        final Runnable refresh = () -> {
            for (int i = 0; i < ids.length; i++) {
                ImageView iv = d.findViewById(ids[i]);
                iv.setImageResource(i < rating[0] ? R.drawable.ic_star_filled : R.drawable.ic_star);
                iv.setColorFilter(starColor);
            }
        };
        refresh.run();

        for (int i = 0; i < ids.length; i++) {
            final int value = i + 1;
            d.findViewById(ids[i]).setOnClickListener(x -> {
                rating[0] = value;
                refresh.run();
            });
        }
        closeOn(d, R.id.rateCancel);
        d.findViewById(R.id.rateSubmit).setOnClickListener(x -> {
            d.dismiss();
            Toast.makeText(a, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
        });
        d.show();
    }

    // ------------------------------------------------------------------ password

    public static void showChangePassword(final Activity a) {
        final Dialog d = create(a, R.layout.dialog_change_password);
        closeOn(d, R.id.cpClose, R.id.cpCancel);
        d.findViewById(R.id.cpUpdate).setOnClickListener(x -> {
            d.dismiss();
            showMessage(a, R.drawable.ic_ring_check, R.color.primary, "Password Changed",
                    "Your password has been updated successfully.", null, "Done", false, null, null);
        });
        d.show();
    }
}
