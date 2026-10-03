package com.example.healthcare.more_section;

import android.content.Context;
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
 * Adapter in 'more_section' folder.
 */
public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    public interface OnPrescriptionClickListener {
        void onPrescriptionClick(PrescriptionModel prescription);
    }

    private final Context context;
    private List<PrescriptionModel> prescriptionList;
    private final OnPrescriptionClickListener clickListener;

    public PrescriptionAdapter(Context context, List<PrescriptionModel> prescriptionList, OnPrescriptionClickListener clickListener) {
        this.context = context;
        this.prescriptionList = (prescriptionList != null) ? prescriptionList : new ArrayList<>();
        this.clickListener = clickListener;
    }

    public void updateList(List<PrescriptionModel> newList) {
        this.prescriptionList = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prescription, parent, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder holder, int position) {
        PrescriptionModel item = prescriptionList.get(position);
        if (item == null) return;

        String title = item.getPatientName() + " · " + item.getDoctorName() + " · " + item.getDate();
        holder.tvPrescriptionTitle.setText(title);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onPrescriptionClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    static class PrescriptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrescriptionTitle;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrescriptionTitle = itemView.findViewById(R.id.tvPrescriptionTitle);
        }
    }
}
