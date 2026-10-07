package com.example.healthcare.admin.doctors_section;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter displaying live Doctor records fetched directly from Firebase Cloud Firestore.
 */
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    public interface OnDoctorActionListener {
        void onEditDoctor(DoctorModel doctor);
        void onDeleteDoctor(DoctorModel doctor);
        void onDoctorItemClick(DoctorModel doctor);
    }

    private final Context context;
    private final List<DoctorModel> originalList = new ArrayList<>();
    private final List<DoctorModel> filteredList = new ArrayList<>();
    private final OnDoctorActionListener listener;

    public DoctorAdapter(Context context, List<DoctorModel> doctorList, OnDoctorActionListener listener) {
        this.context = context;
        this.listener = listener;
        if (doctorList != null) {
            this.originalList.addAll(doctorList);
            this.filteredList.addAll(doctorList);
        }
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
            String lowerQuery = query.toLowerCase().trim().replace(".", "");
            for (DoctorModel doctor : originalList) {
                if (doctor != null) {
                    String name = (doctor.getName() != null) ? doctor.getName().toLowerCase().replace(".", "") : "";
                    String dept = (doctor.getDepartment() != null) ? doctor.getDepartment().toLowerCase() : "";
                    String spec = (doctor.getSpecialization() != null) ? doctor.getSpecialization().toLowerCase() : "";
                    String hosp = (doctor.getHospitalName() != null) ? doctor.getHospitalName().toLowerCase() : "";
                    String loc = (doctor.getLocation() != null) ? doctor.getLocation().toLowerCase() : "";

                    if (lowerQuery.equals("dr") || lowerQuery.equals("doctor") || lowerQuery.equals("doc")
                            || name.contains(lowerQuery)
                            || dept.contains(lowerQuery)
                            || spec.contains(lowerQuery)
                            || hosp.contains(lowerQuery)
                            || loc.contains(lowerQuery)) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_doctor_card, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorModel item = filteredList.get(position);
        if (item == null) return;

        String name = (item.getName() != null && !item.getName().isEmpty()) ? item.getName() : "Dr. Medical Specialist";
        holder.tvDoctorName.setText(name);

        String spec = (item.getSpecialization() != null && !item.getSpecialization().isEmpty()) ? item.getSpecialization() : "General Physician";
        holder.tvDoctorSubtitle.setText(spec);

        if (holder.tvDoctorQual != null) {
            String qual = (item.getQualification() != null && !item.getQualification().isEmpty()) ? item.getQualification() : "MBBS, MD";
            holder.tvDoctorQual.setText("🎓 " + qual);
        }

        if (holder.tvDoctorHospital != null) {
            String hosp = (item.getHospitalName() != null && !item.getHospitalName().isEmpty()) ? item.getHospitalName() : "City Care Hospital";
            holder.tvDoctorHospital.setText("🏥 " + hosp);
        }

        if (holder.tvDoctorLocation != null) {
            String loc = (item.getLocation() != null && !item.getLocation().isEmpty()) ? item.getLocation() : "Bengaluru";
            holder.tvDoctorLocation.setText("📍 " + loc);
        }

        if (item.isUnavailable()) {
            holder.tvStatusBadge.setText("● Unavailable");
            holder.tvStatusBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#7F1D1D")));
            holder.tvStatusBadge.setTextColor(Color.parseColor("#F87171"));
        } else {
            holder.tvStatusBadge.setText("● Available");
            holder.tvStatusBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#064E3B")));
            holder.tvStatusBadge.setTextColor(Color.parseColor("#34D399"));
        }

        if (holder.imgDoctorPhoto != null) {
            int resId = 0;
            if (item.getImageResName() != null && !item.getImageResName().isEmpty()) {
                resId = context.getResources().getIdentifier(item.getImageResName(), "drawable", context.getPackageName());
            }
            if (resId == 0) {
                resId = R.drawable.doc_male_1;
            }
            holder.imgDoctorPhoto.setImageResource(resId);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDoctorItemClick(item);
        });

        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteDoctor(item);
            });
        }

        if (holder.btnCall != null) {
            holder.btnCall.setOnClickListener(v -> {
                String phone = (item.getContactNumber() != null) ? item.getContactNumber() : "+91 9876500001";
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Calling " + item.getName() + " (" + phone + ")", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (holder.btnEmail != null) {
            holder.btnEmail.setOnClickListener(v -> {
                String email = (item.getEmail() != null) ? item.getEmail() : "doctor@hospital.com";
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Emailing " + item.getName() + " (" + email + ")", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDoctorPhoto;
        TextView tvDoctorName;
        TextView tvDoctorSubtitle;
        TextView tvDoctorQual;
        TextView tvDoctorHospital;
        TextView tvDoctorLocation;
        TextView tvStatusBadge;
        ImageView btnDelete;
        ImageView btnCall;
        ImageView btnEmail;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDoctorPhoto = itemView.findViewById(R.id.imgDoctorPhoto);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorSubtitle = itemView.findViewById(R.id.tvDoctorSubtitle);
            tvDoctorQual = itemView.findViewById(R.id.tvDoctorQual);
            tvDoctorHospital = itemView.findViewById(R.id.tvDoctorHospital);
            tvDoctorLocation = itemView.findViewById(R.id.tvDoctorLocation);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            btnDelete = itemView.findViewById(R.id.btnDeleteDoctor);
            btnCall = itemView.findViewById(R.id.btnCallDoctor);
            btnEmail = itemView.findViewById(R.id.btnEmailDoctor);
        }
    }
}
