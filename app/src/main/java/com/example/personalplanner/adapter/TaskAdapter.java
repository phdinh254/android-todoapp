package com.example.personalplanner.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.R;
import com.example.personalplanner.data.model.StudyPlan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.PlanViewHolder> {
    public interface OnTaskActionListener {
        void onTaskClick(StudyPlan plan);
        void onStatusChanged(StudyPlan plan, boolean checked);
    }

    private final ArrayList<StudyPlan> plans = new ArrayList<>();
    private final OnTaskActionListener listener;

    public TaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<StudyPlan> newPlans) {
        int oldSize = plans.size();
        plans.clear();
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize);
        }
        if (newPlans != null) {
            plans.addAll(newPlans);
            if (!newPlans.isEmpty()) {
                notifyItemRangeInserted(0, newPlans.size());
            }
        }
    }

    public int getPosition(StudyPlan plan) {
        return plans.indexOf(plan);
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlanViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        StudyPlan plan = plans.get(position);
        holder.txtTaskTitle.setText(plan.getTitle());
        holder.txtTaskDescription.setText(plan.getDescription());
        holder.txtTaskDescription.setVisibility(plan.getDescription().trim().isEmpty()
                ? View.GONE : View.VISIBLE);
        holder.txtTaskDateTime.setText(holder.itemView.getContext().getString(
                R.string.task_date_time, plan.getDate(), plan.getTime()));
        holder.txtCategoryName.setText(plan.getCategoryName().trim().isEmpty()
                ? holder.itemView.getContext().getString(R.string.uncategorized_course)
                : plan.getCategoryName());
        String[] typeValues = holder.itemView.getResources().getStringArray(R.array.plan_type_values);
        String[] typeNames = holder.itemView.getResources().getStringArray(R.array.plan_type_names);
        String typeName = plan.getPlanType();
        for (int i = 0; i < typeValues.length; i++) {
            if (typeValues[i].equals(plan.getPlanType())) {
                typeName = typeNames[i];
                break;
            }
        }
        holder.txtStudyMeta.setText(holder.itemView.getContext().getString(
                R.string.study_meta,
                typeName + " / " + holder.itemView.getResources().getStringArray(R.array.priority_names)[plan.getPriority()],
                plan.getDurationMinutes()
        ));

        holder.chkStatus.setOnCheckedChangeListener(null);
        holder.chkStatus.setChecked(plan.getStatus() == StudyPlan.STATUS_COMPLETED);
        int flags = holder.txtTaskTitle.getPaintFlags();
        holder.txtTaskTitle.setPaintFlags(plan.getStatus() == StudyPlan.STATUS_COMPLETED
                ? flags | Paint.STRIKE_THRU_TEXT_FLAG
                : flags & ~Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtOverdueBadge.setVisibility(isOverdue(plan) ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> listener.onTaskClick(plan));
        holder.chkStatus.setOnCheckedChangeListener((button, checked) ->
                listener.onStatusChanged(plan, checked));
    }

    private boolean isOverdue(StudyPlan plan) {
        if (plan.getStatus() == StudyPlan.STATUS_COMPLETED
                || plan.getStatus() == StudyPlan.STATUS_CANCELLED) {
            return false;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date planDate = dateFormat.parse(plan.getDate());
            Date today = dateFormat.parse(dateFormat.format(new Date()));
            return planDate != null && today != null && planDate.before(today);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        final TextView txtTaskTitle;
        final TextView txtTaskDescription;
        final TextView txtTaskDateTime;
        final TextView txtCategoryName;
        final TextView txtStudyMeta;
        final TextView txtOverdueBadge;
        final CheckBox chkStatus;

        PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTaskTitle = itemView.findViewById(R.id.txtTaskTitle);
            txtTaskDescription = itemView.findViewById(R.id.txtTaskDescription);
            txtTaskDateTime = itemView.findViewById(R.id.txtTaskDateTime);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            txtStudyMeta = itemView.findViewById(R.id.txtStudyMeta);
            txtOverdueBadge = itemView.findViewById(R.id.txtOverdueBadge);
            chkStatus = itemView.findViewById(R.id.chkStatus);
        }
    }
}
