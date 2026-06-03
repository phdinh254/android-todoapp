package com.example.personalplanner.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.R;
import com.example.personalplanner.model.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private ArrayList<Task> taskList;
    private final OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onTaskClick(Task task);
        void onStatusChanged(Task task, boolean isChecked);
    }

    public TaskAdapter(Context context, ArrayList<Task> taskList, OnTaskActionListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    public void setData(ArrayList<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.txtTaskTitle.setText(task.getTitle());
        holder.txtTaskDescription.setText(task.getDescription());
        holder.txtTaskDateTime.setText(task.getDate() + " - " + task.getTime());

        holder.chkStatus.setOnCheckedChangeListener(null);
        holder.chkStatus.setChecked(task.getStatus() == 1);

        if (task.getStatus() == 1) {
            holder.txtTaskTitle.setPaintFlags(
                    holder.txtTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            holder.txtTaskTitle.setPaintFlags(
                    holder.txtTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });

        holder.chkStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onStatusChanged(task, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView txtTaskTitle, txtTaskDescription, txtTaskDateTime;
        CheckBox chkStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTaskTitle = itemView.findViewById(R.id.txtTaskTitle);
            txtTaskDescription = itemView.findViewById(R.id.txtTaskDescription);
            txtTaskDateTime = itemView.findViewById(R.id.txtTaskDateTime);
            chkStatus = itemView.findViewById(R.id.chkStatus);
        }
    }
}
