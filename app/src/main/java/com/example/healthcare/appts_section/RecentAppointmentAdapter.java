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
 * Adapter in 'appts_section' folder for Recent Appointments.
 */
public class RecentAppointmentAdapter extends RecyclerView.Adapter<RecentAppointmentAdapter.RecentViewHolder> {

    public interface OnRecentAppointmentClickListener {
        void onAppointmentClick(AppointmentModel appointment);
    }

    private final Context context;
    private List<AppointmentModel> appointmentList;
    private final OnRecentAppointmentClickListener clickListener;

    public RecentAppointmentAdapter(Context context, List<AppointmentModel> appointmentList, OnRecentAppointmentClickListener clickListener) {
        this.context = context;
        this.appointmentList = (appointmentList != null) ? appointmentList : new ArrayList<>();
        this.clickListener = clickListener;
    }

    public void updateList(List<AppointmentModel> newList) {
        this.appointmentList = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_appointment, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        AppointmentModel item = appointmentList.get(position);
        if (item == null) return;

        String title = item.getPatientName() + " · " + item.getDoctorName();
        holder.tvPatientTitle.setText(title);

        if (item.getDate() != null && !item.getDate().isEmpty()) {
            holder.tvAppointmentTime.setText(item.getDate());
            holder.tvAppointmentTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvAppointmentTime.setVisibility(View.GONE);
        }

        String status = (item.getStatus() != null) ? item.getStatus() : "Confirmed";
        holder.tvAppointmentStatus.setText(status);

        String statusLower = status.toLowerCase();
        if (statusLower.contains("confirmed")) {
            holder.tvAppointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.teal_primary));
        } else if (statusLower.contains("completed")) {
            holder.tvAppointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.status_approved_text));
        } else if (statusLower.contains("pending")) {
            holder.tvAppointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
        } else if (statusLower.contains("cancel")) {
            holder.tvAppointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.status_rejected_text));
        } else {
            holder.tvAppointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onAppointmentClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class RecentViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientTitle;
        TextView tvAppointmentTime;
        TextView tvAppointmentStatus;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientTitle = itemView.findViewById(R.id.tvPatientTitle);
            tvAppointmentTime = itemView.findViewById(R.id.tvAppointmentTime);
            tvAppointmentStatus = itemView.findViewById(R.id.tvAppointmentStatus);
        }
    }
}
