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

public class PlanCategoryAdapter extends RecyclerView.Adapter<PlanCategoryAdapter.CategoryViewHolder> {
    public interface OnCategoryClickListener {
        void onCategoryClick(PlanCategory category);
    }

    private final ArrayList<PlanCategory> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public PlanCategoryAdapter(OnCategoryClickListener listener) {
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
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        PlanCategory category = categories.get(position);
        holder.txtCategoryName.setText(category.getCategoryName());
        holder.txtCategoryCode.setText(category.getCategoryCode().trim().isEmpty()
                ? holder.itemView.getContext().getString(R.string.no_category_code)
                : category.getCategoryCode());
        holder.txtCategoryNote.setText(category.getNote().trim().isEmpty()
                ? holder.itemView.getContext().getString(R.string.no_category_note)
                : category.getNote());
        try {
            holder.viewCategoryColor.setBackgroundColor(Color.parseColor(category.getColor()));
        } catch (IllegalArgumentException ignored) {
            holder.viewCategoryColor.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.primary)
            );
        }
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final View viewCategoryColor;
        final TextView txtCategoryName;
        final TextView txtCategoryCode;
        final TextView txtCategoryNote;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryColor = itemView.findViewById(R.id.viewCategoryColor);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            txtCategoryCode = itemView.findViewById(R.id.txtCategoryCode);
            txtCategoryNote = itemView.findViewById(R.id.txtCategoryNote);
        }
    }
}
