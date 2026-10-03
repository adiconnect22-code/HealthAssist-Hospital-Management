package com.example.healthcare.doctors_section;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter in 'doctors_section' folder.
 */
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    public interface OnDoctorActionListener {
        void onEditDoctor(DoctorModel doctor);
        void onDeleteDoctor(DoctorModel doctor);
    }

    private final Context context;
    private final List<DoctorModel> originalList;
    private final List<DoctorModel> filteredList;
    private final OnDoctorActionListener listener;

    public DoctorAdapter(Context context, List<DoctorModel> doctorList, OnDoctorActionListener listener) {
        this.context = context;
        this.originalList = (doctorList != null) ? doctorList : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.originalList);
        this.listener = listener;
    }

    public void updateList(List<DoctorModel> newList) {
        this.originalList.clear();
        if (newList != null) {
            this.originalList.addAll(newList);
        }
        this.filteredList.clear();
        this.filteredList.addAll(this.originalList);
        notifyDataSetChanged();
    }

    public boolean filter(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (DoctorModel doctor : originalList) {
                if (doctor != null) {
                    String name = (doctor.getName() != null) ? doctor.getName().toLowerCase() : "";
                    String dept = (doctor.getDepartment() != null) ? doctor.getDepartment().toLowerCase() : "";
                    String spec = (doctor.getSpecialization() != null) ? doctor.getSpecialization().toLowerCase() : "";

                    if (name.contains(lowerQuery) || dept.contains(lowerQuery) || spec.contains(lowerQuery)) {
                        filteredList.add(doctor);
                    }
                }
            }
        }
        notifyDataSetChanged();
        return filteredList.isEmpty();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor_card, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorModel item = filteredList.get(position);
        if (item == null) return;

        holder.tvDoctorInitials.setText(item.getInitials());
        holder.tvDoctorName.setText(item.getName());

        String subtitle = item.getDepartment() + " · Cap: " + (item.getFeeCapacity() != null ? item.getFeeCapacity() : "10/day");
        holder.tvDoctorSubtitle.setText(subtitle);

        // Show "Not Available" badge if doctor is on leave today
        if (item.isOnLeaveToday()) {
            holder.tvLeaveBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvLeaveBadge.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditDoctor(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteDoctor(item);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorInitials;
        TextView tvDoctorName;
        TextView tvDoctorSubtitle;
        TextView tvLeaveBadge;
        ImageView btnEdit;
        ImageView btnDelete;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorInitials = itemView.findViewById(R.id.tvDoctorInitials);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorSubtitle = itemView.findViewById(R.id.tvDoctorSubtitle);
            tvLeaveBadge = itemView.findViewById(R.id.tvLeaveBadge);
            btnEdit = itemView.findViewById(R.id.btnEditDoctor);
            btnDelete = itemView.findViewById(R.id.btnDeleteDoctor);
        }
    }
}
