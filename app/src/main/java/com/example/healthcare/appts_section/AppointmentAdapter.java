package com.example.healthcare.appts_section;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter in 'appts_section' folder.
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final Context context;
    private final List<AppointmentModel> originalList;
    private final List<AppointmentModel> filteredList;

    public AppointmentAdapter(Context context, List<AppointmentModel> appointmentList) {
        this.context = context;
        this.originalList = (appointmentList != null) ? appointmentList : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.originalList);
    }

    public boolean filter(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (AppointmentModel item : originalList) {
                if (item != null) {
                    String patient = (item.getPatientName() != null) ? item.getPatientName().toLowerCase() : "";
                    String doctor = (item.getDoctorName() != null) ? item.getDoctorName().toLowerCase() : "";
                    String dept = (item.getDepartment() != null) ? item.getDepartment().toLowerCase() : "";

                    if (patient.contains(lowerQuery) || doctor.contains(lowerQuery) || dept.contains(lowerQuery)) {
                        filteredList.add(item);
                    }
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

        String titleText = item.getPatientName() + " · " + item.getDoctorName();
        holder.tvApptTitle.setText(titleText);

        if (item.getDate() != null && !item.getDate().isEmpty()) {
            holder.tvApptDate.setText(item.getDate());
            holder.tvApptDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvApptDate.setVisibility(View.GONE);
        }

        String status = (item.getStatus() != null) ? item.getStatus() : "";
        holder.tvApptStatus.setText(status);

        String statusLower = status.toLowerCase();
        if (statusLower.contains("confirmed")) {
            holder.tvApptStatus.setTextColor(ContextCompat.getColor(context, R.color.teal_primary));
        } else if (statusLower.contains("completed")) {
            holder.tvApptStatus.setTextColor(ContextCompat.getColor(context, R.color.status_approved_text));
        } else if (statusLower.contains("pending")) {
            holder.tvApptStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
        } else if (statusLower.contains("cancel")) {
            holder.tvApptStatus.setTextColor(ContextCompat.getColor(context, R.color.status_rejected_text));
        } else {
            holder.tvApptStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }
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
