package com.example.healthcare.admin.more_section;

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
 * Adapter in 'more_section' folder.
 */
public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {

    public interface OnDepartmentActionListener {
        void onEdit(DepartmentModel department);
        void onDelete(DepartmentModel department);
    }

    private final Context context;
    private List<DepartmentModel> departmentList;
    private final OnDepartmentActionListener listener;

    public DepartmentAdapter(Context context, List<DepartmentModel> departmentList, OnDepartmentActionListener listener) {
        this.context = context;
        this.departmentList = (departmentList != null) ? departmentList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<DepartmentModel> newList) {
        this.departmentList = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_department, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        DepartmentModel item = departmentList.get(position);
        if (item == null) return;

        String title = item.getName() + " · " + item.getFloor();
        holder.tvDeptTitle.setText(title);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeptTitle;
        ImageView btnEdit;
        ImageView btnDelete;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeptTitle = itemView.findViewById(R.id.tvDeptTitle);
            btnEdit = itemView.findViewById(R.id.btnEditDepartment);
            btnDelete = itemView.findViewById(R.id.btnDeleteDepartment);
        }
    }
}
