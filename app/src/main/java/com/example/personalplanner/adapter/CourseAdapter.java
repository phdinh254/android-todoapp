package com.example.personalplanner.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.R;
import com.example.personalplanner.data.model.PlanCategory;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    public interface OnCourseClickListener {
        void onCourseClick(PlanCategory category);
    }

    private final ArrayList<PlanCategory> categories = new ArrayList<>();
    private final OnCourseClickListener listener;

    public CourseAdapter(OnCourseClickListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<PlanCategory> newCategories) {
        int oldSize = categories.size();
        categories.clear();
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize);
        }
        if (newCategories != null) {
            categories.addAll(newCategories);
            if (!newCategories.isEmpty()) {
                notifyItemRangeInserted(0, newCategories.size());
            }
        }
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        PlanCategory category = categories.get(position);
        holder.txtCourseName.setText(category.getCategoryName());
        holder.txtCourseCode.setText(category.getCategoryCode().trim().isEmpty()
                ? holder.itemView.getContext().getString(R.string.no_course_code)
                : category.getCategoryCode());
        holder.txtLecturer.setText(category.getNote().trim().isEmpty()
                ? holder.itemView.getContext().getString(R.string.no_lecturer)
                : category.getNote());
        try {
            holder.viewCourseColor.setBackgroundColor(Color.parseColor(category.getColor()));
        } catch (IllegalArgumentException ignored) {
            holder.viewCourseColor.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.primary)
            );
        }
        holder.itemView.setOnClickListener(v -> listener.onCourseClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        final View viewCourseColor;
        final TextView txtCourseName;
        final TextView txtCourseCode;
        final TextView txtLecturer;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCourseColor = itemView.findViewById(R.id.viewCourseColor);
            txtCourseName = itemView.findViewById(R.id.txtCourseName);
            txtCourseCode = itemView.findViewById(R.id.txtCourseCode);
            txtLecturer = itemView.findViewById(R.id.txtLecturer);
        }
    }
}
