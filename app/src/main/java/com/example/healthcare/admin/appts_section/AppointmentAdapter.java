package com.example.healthcare.admin.appts_section;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter in 'appts_section' matching exact UI design in Screenshot 2 with optimized search filter.
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnAppointmentActionListener {
        void onApproveAppointment(AppointmentModel appointment);
        void onRejectAppointment(AppointmentModel appointment);
        void onCompleteAppointment(AppointmentModel appointment);
        void onAppointmentClick(AppointmentModel appointment);
    }

    private final Context context;
    private final List<AppointmentModel> originalList = new ArrayList<>();
    private final List<AppointmentModel> filteredList = new ArrayList<>();
    private final OnAppointmentActionListener listener;

    public AppointmentAdapter(Context context, List<AppointmentModel> appointmentList, OnAppointmentActionListener listener) {
        this.context = context;
        this.listener = listener;
        if (appointmentList != null) {
            this.originalList.addAll(appointmentList);
            this.filteredList.addAll(appointmentList);
        }
    }

    public void updateList(List<AppointmentModel> newList) {
        this.originalList.clear();
        if (newList != null) {
            this.originalList.addAll(newList);
        }
        this.filteredList.clear();
        this.filteredList.addAll(this.originalList);
        notifyDataSetChanged();
    }

    public boolean filter(String query, String tabStatus) {
        filteredList.clear();
        String cleanQuery = (query != null) ? query.toLowerCase().trim().replaceAll("[.,]", "") : "";
        String[] tokens = cleanQuery.isEmpty() ? new String[0] : cleanQuery.split("\\s+");

        for (AppointmentModel item : originalList) {
            if (item == null) continue;

            // 1. Tab Filtering
            boolean tabMatches = false;
            String status = (item.getStatus() != null) ? item.getStatus().trim() : "Pending";

            if (tabStatus == null || tabStatus.equalsIgnoreCase("All") || tabStatus.startsWith("All")) {
                tabMatches = true;
            } else if (tabStatus.equalsIgnoreCase("Upcoming")) {
                tabMatches = status.equalsIgnoreCase("Upcoming") || status.equalsIgnoreCase("Confirmed");
            } else if (tabStatus.equalsIgnoreCase("Pending") || tabStatus.startsWith("Pending")) {
                tabMatches = status.equalsIgnoreCase("Pending");
            } else if (tabStatus.equalsIgnoreCase("Completed") || tabStatus.startsWith("Completed")) {
                tabMatches = status.equalsIgnoreCase("Completed");
            } else if (tabStatus.equalsIgnoreCase("Rejected")) {
                tabMatches = status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Cancelled");
            }

            if (!tabMatches) continue;

            // 2. Optimized Case-Insensitive Multi-Token Substring Filtering
            if (tokens.length == 0) {
                filteredList.add(item);
            } else {
                String patient = (item.getPatientName() != null) ? item.getPatientName().toLowerCase().replaceAll("[.,]", "") : "";
                String doctor = (item.getDoctorName() != null) ? item.getDoctorName().toLowerCase().replaceAll("[.,]", "") : "";
                String dept = (item.getDepartment() != null) ? item.getDepartment().toLowerCase() : "";
                String dateStr = (item.getDate() != null) ? item.getDate().toLowerCase() : "";
                String reasonStr = (item.getReason() != null) ? item.getReason().toLowerCase() : "";

                String searchableBlob = patient + " " + doctor + " " + dept + " " + dateStr + " " + reasonStr;

                boolean allTokensMatch = true;
                for (String token : tokens) {
                    if (token.equals("dr") || token.equals("doctor") || token.equals("doc")) {
                        continue;
                    }
                    if (!searchableBlob.contains(token)) {
                        allTokensMatch = false;
                        break;
                    }
                }

                if (allTokensMatch) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
        return filteredList.isEmpty();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_all, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentModel item = filteredList.get(position);
        if (item == null) return;

        String patient = (item.getPatientName() != null && !item.getPatientName().isEmpty()) ? item.getPatientName() : "Patient";
        String doctor = (item.getDoctorName() != null && !item.getDoctorName().isEmpty()) ? item.getDoctorName() : "Dr. Medical Specialist";

        holder.tvApptTitle.setText(patient + " · " + doctor);

        String dateStr = (item.getDate() != null && !item.getDate().isEmpty()) ? item.getDate() : "Today";
        if (item.getTimeSlot() != null && !item.getTimeSlot().isEmpty()) {
            dateStr = dateStr + ", " + item.getTimeSlot();
        }
        holder.tvApptDate.setText(dateStr);

        String status = (item.getStatus() != null && !item.getStatus().isEmpty()) ? item.getStatus() : "Pending";
        holder.tvApptStatus.setText(status);

        if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Upcoming")) {
            holder.tvApptStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2D225C")));
            holder.tvApptStatus.setTextColor(Color.parseColor("#C084FC"));
        } else if (status.equalsIgnoreCase("Completed")) {
            holder.tvApptStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1C2E42")));
            holder.tvApptStatus.setTextColor(Color.parseColor("#38BDF8"));
        } else if (status.equalsIgnoreCase("Pending")) {
            holder.tvApptStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#3B2219")));
            holder.tvApptStatus.setTextColor(Color.parseColor("#FBBF24"));
        } else { // Cancelled / Rejected
            holder.tvApptStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#3B1C22")));
            holder.tvApptStatus.setTextColor(Color.parseColor("#F87171"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAppointmentClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvApptTitle;
        TextView tvApptDate;
        TextView tvApptStatus;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApptTitle = itemView.findViewById(R.id.tvApptTitle);
            tvApptDate = itemView.findViewById(R.id.tvApptDate);
            tvApptStatus = itemView.findViewById(R.id.tvApptStatus);
        }
    }
}
