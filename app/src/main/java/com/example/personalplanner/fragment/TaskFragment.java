package com.example.personalplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.R;
import com.example.personalplanner.activity.TaskDetailActivity;
import com.example.personalplanner.adapter.TaskAdapter;
import com.example.personalplanner.database.DatabaseHelper;
import com.example.personalplanner.model.Task;
import com.example.personalplanner.utils.SessionManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskFragment extends Fragment {

    private RecyclerView recyclerTask;
    private EditText edtSearchTask;
    private TextView txtEmptyTask;

    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public TaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task, container, false);

        initViews(view);
        initObjects();
        setupRecyclerView();
        handleEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (edtSearchTask != null && edtSearchTask.getText() != null) {
            String keyword = edtSearchTask.getText().toString().trim();

            if (keyword.isEmpty()) {
                loadTaskList();
            } else {
                searchTask(keyword);
            }
        }
    }

    private void initViews(View view) {
        recyclerTask = view.findViewById(R.id.recyclerTask);
        edtSearchTask = view.findViewById(R.id.edtSearchTask);
        txtEmptyTask = view.findViewById(R.id.txtEmptyTask);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        taskList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(requireContext(), taskList, new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onTaskClick(Task task) {
                openTaskDetail(task);
            }

            @Override
            public void onStatusChanged(Task task, boolean isChecked) {
                updateTaskStatus(task, isChecked);
            }
        });

        recyclerTask.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerTask.setAdapter(taskAdapter);
    }

    private void handleEvents() {
        edtSearchTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();

                if (keyword.isEmpty()) {
                    loadTaskList();
                } else {
                    searchTask(keyword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadTaskList() {
        int userId = sessionManager.getUserId();

        executorService.execute(() -> {
            ArrayList<Task> tasks = databaseHelper.getAllTasks(userId);

            mainHandler.post(() -> {
                if (!isAdded()) {
                    return;
                }

                taskAdapter.setData(tasks);
                updateEmptyView(tasks);
            });
        });
    }

    private void searchTask(String keyword) {
        int userId = sessionManager.getUserId();

        executorService.execute(() -> {
            ArrayList<Task> tasks = databaseHelper.searchTasks(keyword, userId);

            mainHandler.post(() -> {
                if (!isAdded()) {
                    return;
                }

                taskAdapter.setData(tasks);
                updateEmptyView(tasks);
            });
        });
    }

    private void updateTaskStatus(Task task, boolean isChecked) {
        int newStatus = isChecked ? 1 : 0;

        executorService.execute(() -> {
            boolean result = databaseHelper.updateTaskStatus(task.getTaskId(), newStatus);

            mainHandler.post(() -> {
                if (!isAdded()) {
                    return;
                }

                if (result) {
                    task.setStatus(newStatus);
                    taskAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    private void updateEmptyView(ArrayList<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            txtEmptyTask.setVisibility(View.VISIBLE);
            recyclerTask.setVisibility(View.GONE);
        } else {
            txtEmptyTask.setVisibility(View.GONE);
            recyclerTask.setVisibility(View.VISIBLE);
        }
    }

    private void openTaskDetail(Task task) {
        Intent intent = new Intent(requireContext(), TaskDetailActivity.class);

        intent.putExtra("task_id", task.getTaskId());
        intent.putExtra("title", task.getTitle());
        intent.putExtra("description", task.getDescription());
        intent.putExtra("date", task.getDate());
        intent.putExtra("time", task.getTime());
        intent.putExtra("status", task.getStatus());
        intent.putExtra("category_id", task.getCategoryId());

        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
